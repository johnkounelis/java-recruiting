<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Recruiting App</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-transparent sticky-top">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">
                <i class="bi bi-briefcase-fill me-2"></i>Recruiting App
            </a>
        </div>
    </nav>

    <div class="main-content container mt-5 pt-5">
        <div class="row justify-content-center">
            <div class="col-md-6 text-center">
                <div class="card p-5 border-0" style="background: rgba(255,255,255,0.1); backdrop-filter: blur(20px); border: 1px solid rgba(255,255,255,0.2);">
                    <i class="bi bi-exclamation-triangle text-warning mb-3" style="font-size: 4rem;"></i>
                    <h1 class="display-1 fw-bold text-white">404</h1>
                    <h3 class="text-white mb-3">Page Not Found</h3>
                    <p class="text-white-50 mb-4">The page you are looking for does not exist or has been moved.</p>
                    <div class="d-grid gap-2 d-sm-flex justify-content-sm-center">
                        <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-primary btn-lg px-4">
                            <i class="bi bi-house me-2"></i>Home
                        </a>
                        <a href="${pageContext.request.contextPath}/jobs.jsp" class="btn btn-outline-light btn-lg px-4">
                            <i class="bi bi-search me-2"></i>Jobs
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <footer class="mt-5 py-4 text-center" style="background: rgba(17, 24, 39, 0.6); backdrop-filter: blur(10px); border-top: 1px solid rgba(255,255,255,0.1);">
        <div class="container">
            <p class="mb-0 text-white-50 small">&copy; 2026 Recruiting App. All rights reserved.</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
