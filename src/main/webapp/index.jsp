<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="el">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recruiting App - Αρχική</title>
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
                    <li class="nav-item"><a class="nav-link" href="login.jsp" data-i18n="nav.login">Σύνδεση</a></li>
                    <li class="nav-item ms-lg-2"><a class="btn btn-primary btn-sm px-4" href="register.jsp"
                            data-i18n="nav.register">Εγγραφή</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="main-content container mt-5 pt-5">
        <div class="row align-items-center justify-content-center min-vh-75">
            <div class="col-lg-8 text-center">
                <div class="card p-5 mb-5 border-0"
                    style="background: rgba(255,255,255,0.1); backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px); border: 1px solid rgba(255,255,255,0.2); box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);">
                    <h1 class="display-3 fw-bold text-white mb-4" style="text-shadow: 0 2px 10px rgba(0,0,0,0.3);">
                        <span data-i18n="home.welcome">Καλώς ήρθατε στο</span><br><span class="text-primary"
                            style="background: linear-gradient(to right, #60a5fa, #a78bfa); -webkit-background-clip: text; background-clip: text; -webkit-text-fill-color: transparent; text-shadow: none;">Recruiting
                            App</span>
                    </h1>
                    <p class="lead text-light mb-5 fs-4" style="text-shadow: 0 1px 5px rgba(0,0,0,0.5);"
                        data-i18n="home.subtitle">
                        Βρείτε την ιδανική θέση εργασίας ή αναζητήστε τους καλύτερους υποψηφίους για την εταιρεία
                        σας.
                    </p>
                    <div class="d-grid gap-3 d-sm-flex justify-content-sm-center">
                        <a href="jobs.jsp" class="btn btn-primary btn-lg px-5 py-3 rounded-pill fw-bold shadow-lg">
                            <i class="bi bi-search me-2"></i><span data-i18n="home.viewJobs">Δείτε τις Διαθέσιμες
                                Θέσεις</span>
                        </a>
                        <a href="register.jsp" class="btn btn-light btn-lg px-5 py-3 rounded-pill fw-bold"
                            style="color: var(--primary-color);" data-i18n="home.startNow">
                            Ξεκινήστε Τώρα
                        </a>
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

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="js/i18n.js"></script>
</body>

</html>
