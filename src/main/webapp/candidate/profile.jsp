<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.recruiting.model.User" %>
<% User user=(User) session.getAttribute("user"); if (user==null) { response.sendRedirect("../login.jsp");
    return; } %>
<!DOCTYPE html>
<html lang="el">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Προφίλ - Recruiting App</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>

<body>
    <% String dashboardUrl="dashboard.jsp" ; if (user.getRole().equals("ADMIN")) {
        dashboardUrl="../admin/dashboard.jsp" ; } else if (user.getRole().equals("RECRUITER")) {
        dashboardUrl="../recruiter/dashboard.jsp" ; } %>
    <nav class="navbar navbar-expand-lg navbar-dark bg-transparent sticky-top">
        <div class="container">
            <a class="navbar-brand" href="../index.jsp">
                <i class="bi bi-briefcase-fill me-2"></i>Recruiting App
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto align-items-center">
                    <li class="nav-item me-3">
                        <div class="btn-group btn-group-sm" role="group">
                            <button type="button" class="btn btn-outline-light lang-btn-el active">EL</button>
                            <button type="button" class="btn btn-outline-light lang-btn-en">EN</button>
                        </div>
                    </li>
                    <li class="nav-item"><a class="nav-link" href="../jobs.jsp" data-i18n="nav.jobs">Θέσεις</a></li>
                    <li class="nav-item"><a class="nav-link" href="<%= dashboardUrl %>" data-i18n="nav.dashboard">Dashboard</a></li>
                    <li class="nav-item"><a class="nav-link active fw-bold" href="profile.jsp" data-i18n="nav.profile">Προφίλ</a></li>
                    <li class="nav-item"><span class="navbar-text ms-2 me-2 text-light fw-semibold"><i class="bi bi-person-circle me-1"></i><%= user.getName() %></span></li>
                    <li class="nav-item"><a class="btn btn-outline-light btn-sm" href="../api/logout" data-i18n="nav.logout">Αποσύνδεση</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="main-content container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header">
                        <h3 data-i18n="profile.title">Το Προφίλ Μου</h3>
                    </div>
                    <div class="card-body">
                        <div id="alertMessage"></div>
                        <form id="profileForm">
                            <div class="mb-3">
                                <label for="name" class="form-label" data-i18n="profile.fullName">Ονοματεπώνυμο</label>
                                <input type="text" class="form-control" id="name" name="name" required
                                    minlength="2">
                            </div>
                            <div class="mb-3">
                                <label for="email" class="form-label" data-i18n="profile.email">Email</label>
                                <input type="email" class="form-control" id="email" name="email" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label" data-i18n="profile.role">Ρόλος</label>
                                <input type="text" class="form-control" id="role" disabled>
                            </div>

                            <hr>
                            <h5 data-i18n="profile.changePassword">Αλλαγή Κωδικού (προαιρετικά)</h5>
                            <div class="mb-3">
                                <label for="currentPassword" class="form-label" data-i18n="profile.currentPassword">Τρέχων Κωδικός</label>
                                <input type="password" class="form-control" id="currentPassword"
                                    name="currentPassword">
                            </div>
                            <div class="mb-3">
                                <label for="newPassword" class="form-label" data-i18n="profile.newPassword">Νέος Κωδικός</label>
                                <input type="password" class="form-control" id="newPassword"
                                    name="newPassword" minlength="6">
                            </div>
                            <div class="mb-3">
                                <label for="confirmPassword" class="form-label" data-i18n="profile.confirmPassword">Επιβεβαίωση Νέου Κωδικού</label>
                                <input type="password" class="form-control" id="confirmPassword">
                            </div>

                            <button type="submit" class="btn btn-primary" id="saveBtn" data-i18n="profile.save">Αποθήκευση</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="mt-5 py-4 text-center" style="background: rgba(17, 24, 39, 0.6); backdrop-filter: blur(10px); border-top: 1px solid rgba(255,255,255,0.1);">
        <div class="container">
            <p class="mb-0 text-white-50 small">&copy; 2026 <span data-i18n="common.footer">Recruiting App. Με επιφύλαξη παντός δικαιώματος.</span></p>
        </div>
    </footer>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="../js/common.js"></script>
    <script src="../js/i18n.js"></script>
    <script src="../js/profile.js"></script>
</body>

</html>
