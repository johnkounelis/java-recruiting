package com.recruiting.controller;

import com.google.gson.Gson;
import com.recruiting.model.Application;
import com.recruiting.model.User;
import com.recruiting.service.ApplicationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@WebServlet("/api/resume/*")
@MultipartConfig(
    maxFileSize = 5 * 1024 * 1024,      // 5MB per file
    maxRequestSize = 10 * 1024 * 1024,   // 10MB total request
    fileSizeThreshold = 1024 * 1024       // 1MB before writing to disk
)
public class ResumeServlet extends HttpServlet {
    private ApplicationService applicationService = new ApplicationService();
    private Gson gson = new Gson();
    private static final String UPLOAD_DIR = "uploads/resumes";
    private static final String[] ALLOWED_TYPES = {
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    };
    private static final String[] ALLOWED_EXTENSIONS = { ".pdf", ".doc", ".docx" };

    private String getUploadPath() {
        String appPath = getServletContext().getRealPath("");
        String uploadPath = appPath + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        return uploadPath;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            result.put("success", false);
            result.put("message", "Πρέπει να είστε συνδεδεμένος");
            out.print(gson.toJson(result));
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"CANDIDATE".equals(user.getRole())) {
            result.put("success", false);
            result.put("message", "Μόνο candidates μπορούν να ανεβάσουν βιογραφικό");
            out.print(gson.toJson(result));
            return;
        }

        try {
            Part filePart = request.getPart("resume");
            if (filePart == null || filePart.getSize() == 0) {
                result.put("success", true);
                result.put("message", "Δεν επιλέχτηκε αρχείο");
                result.put("filename", null);
                out.print(gson.toJson(result));
                return;
            }

            // Validate file type
            String contentType = filePart.getContentType();
            String originalFilename = getSubmittedFileName(filePart);
            String extension = getFileExtension(originalFilename).toLowerCase();

            if (!isAllowedType(contentType, extension)) {
                result.put("success", false);
                result.put("message", "Επιτρέπονται μόνο αρχεία PDF, DOC, DOCX");
                out.print(gson.toJson(result));
                return;
            }

            // Generate safe filename
            String safeFilename = UUID.randomUUID().toString() + extension;
            String uploadPath = getUploadPath();

            // Save file
            filePart.write(uploadPath + File.separator + safeFilename);

            result.put("success", true);
            result.put("filename", safeFilename);
            result.put("originalName", originalFilename);

        } catch (IllegalStateException e) {
            result.put("success", false);
            result.put("message", "Το αρχείο είναι πολύ μεγάλο (max 5MB)");
        } catch (Exception e) {
            System.err.println("Error uploading resume: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Σφάλμα κατά το ανέβασμα");
        }

        out.print(gson.toJson(result));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User user = (User) session.getAttribute("user");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Path format: /api/resume/{applicationId}
        try {
            int applicationId = Integer.parseInt(pathInfo.substring(1));
            Optional<Application> appOpt = applicationService.getApplicationById(applicationId);

            if (!appOpt.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Application app = appOpt.get();

            // Authorization: candidate can download own, recruiter/admin can download
            if ("CANDIDATE".equals(user.getRole()) && app.getCandidateId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String filename = app.getResumeFilename();
            if (filename == null || filename.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No resume attached");
                return;
            }

            // Prevent path traversal
            Path filePath = Paths.get(getUploadPath(), filename).normalize();
            if (!filePath.startsWith(Paths.get(getUploadPath()).normalize())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            File file = filePath.toFile();
            if (!file.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null) mimeType = "application/octet-stream";

            response.setContentType(mimeType);
            response.setHeader("Content-Disposition", "attachment; filename=\"resume" + getFileExtension(filename) + "\"");
            response.setContentLengthLong(file.length());

            try (InputStream in = new FileInputStream(file);
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid application ID");
        }
    }

    private String getSubmittedFileName(Part part) {
        String header = part.getHeader("content-disposition");
        if (header != null) {
            for (String token : header.split(";")) {
                if (token.trim().startsWith("filename")) {
                    return token.substring(token.indexOf('=') + 1).trim()
                            .replace("\"", "");
                }
            }
        }
        return "unknown";
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot >= 0 ? filename.substring(lastDot) : "";
    }

    private boolean isAllowedType(String contentType, String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) return true;
        }
        for (String allowed : ALLOWED_TYPES) {
            if (allowed.equals(contentType)) return true;
        }
        return false;
    }
}
