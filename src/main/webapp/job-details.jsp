<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Λεπτομέρειες Θέσης - Recruiting App</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-transparent sticky-top">
        <div class="container">
            <a class="navbar-brand" href="index.jsp">
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
                    <li class="nav-item"><a class="nav-link" href="jobs.jsp" data-i18n="nav.jobs">Θέσεις</a></li>
                    <li class="nav-item"><span id="userInfo" class="navbar-text ms-3 me-3 text-light fw-semibold"></span></li>
                    <li class="nav-item" id="dashboardLinkContainer" style="display:none;"><a class="nav-link" href="#" id="dashboardLink" data-i18n="nav.dashboard">Dashboard</a></li>
                    <li class="nav-item"><a class="btn btn-light btn-sm text-primary fw-bold" href="login.jsp" id="loginLink" data-i18n="nav.loginBtn">Σύνδεση</a></li>
                    <li class="nav-item"><a class="btn btn-outline-light btn-sm d-none" href="api/logout" id="logoutLink" data-i18n="nav.logout">Αποσύνδεση</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="main-content container mt-5">
        <div id="jobDetailsContainer">
            <div class="text-center">
                <div class="spinner-border" role="status">
                    <span class="visually-hidden" data-i18n="common.loading">Φόρτωση...</span>
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
    <script src="js/common.js"></script>
    <script src="js/i18n.js"></script>
    <script src="js/job-details.js"></script>
</body>
</html>
