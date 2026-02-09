<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.recruiting.model.User" %>
<%
    // Check if already logged in
    User user = (User) session.getAttribute("user");
    if (user != null) {
        String redirectUrl = "candidate/dashboard.jsp";
        if (user.getRole().equals("ADMIN")) {
            redirectUrl = "admin/dashboard.jsp";
        } else if (user.getRole().equals("RECRUITER")) {
            redirectUrl = "recruiter/dashboard.jsp";
        }
        response.sendRedirect(redirectUrl);
        return;
    }
%>
<!DOCTYPE html>
<html lang="el">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Recruiting App</title>
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
                    <li class="nav-item"><a class="nav-link" href="register.jsp" data-i18n="nav.register">Εγγραφή</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="main-content container mt-5 pt-4">
        <div class="row justify-content-center">
            <div class="col-md-5">
                <div class="card shadow-lg border-0 rounded-4">
                    <div class="card-header bg-white border-bottom-0 pt-4 pb-0 rounded-top-4">
                        <div class="text-center mb-3">
                            <div class="bg-primary bg-opacity-10 d-inline-flex align-items-center justify-content-center rounded-circle mb-3" style="width: 64px; height: 64px;">
                                <i class="bi bi-person-circle text-primary" style="font-size: 2rem;"></i>
                            </div>
                        </div>
                        <h3 class="text-center fw-bold text-primary" data-i18n="login.title">Σύνδεση</h3>
                        <p class="text-center text-muted mb-0" data-i18n="login.subtitle">Καλώς ήρθατε ξανά</p>
                    </div>
                    <div class="card-body p-4 p-md-5">
                        <div id="alertMessage"></div>
                        <form id="loginForm">
                            <div class="mb-4">
                                <label for="email" class="form-label text-muted fw-semibold" data-i18n="login.email">Email</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-end-0"><i class="bi bi-envelope text-muted"></i></span>
                                    <input type="email" class="form-control form-control-lg bg-light border-start-0" id="email"
                                        name="email" placeholder="name@example.com" required>
                                </div>
                            </div>
                            <div class="mb-4">
                                <label for="password"
                                    class="form-label text-muted fw-semibold d-flex justify-content-between">
                                    <span data-i18n="login.password">Κωδικός</span>
                                    <a href="forgot-password.jsp"
                                        class="text-decoration-none small" data-i18n="login.forgotPassword">Ξεχάσατε τον κωδικό;</a>
                                </label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-end-0"><i class="bi bi-lock text-muted"></i></span>
                                    <input type="password" class="form-control form-control-lg bg-light border-start-0 border-end-0"
                                        id="password" name="password" placeholder="••••••••" required>
                                    <button class="btn bg-light border border-start-0" type="button" id="togglePassword" tabindex="-1">
                                        <i class="bi bi-eye text-muted" id="togglePasswordIcon"></i>
                                    </button>
                                </div>
                            </div>
                            <div class="d-grid mb-3">
                                <button type="submit" class="btn btn-primary btn-lg fw-bold rounded-3"
                                    id="loginBtn" data-i18n="login.loginBtn">Σύνδεση</button>
                            </div>
                        </form>
                        <div class="text-center mt-4 pt-2 border-top">
                            <p class="text-muted mb-0"><span data-i18n="login.noAccount">Δεν έχετε λογαριασμό;</span> <a href="register.jsp"
                                    class="fw-semibold text-decoration-none" data-i18n="login.registerHere">Εγγραφείτε εδώ</a></p>
                        </div>
                    </div>
                </div>

                <!-- Demo Accounts Card -->
                <div class="card border-0 shadow-sm rounded-4 mt-3 mb-4" style="background: rgba(255,255,255,0.08); backdrop-filter: blur(10px); border: 1px solid rgba(255,255,255,0.15) !important;">
                    <div class="card-body p-3">
                        <h6 class="text-center mb-3 text-white-50 fw-bold">
                            <i class="bi bi-info-circle me-1"></i><span data-i18n="login.demoTitle">Demo Λογαριασμοί</span>
                        </h6>
                        <div class="small">
                            <div class="d-flex justify-content-between align-items-center mb-2 p-2 rounded-3" style="background: rgba(255,255,255,0.05);">
                                <div>
                                    <span class="badge bg-danger bg-opacity-75 me-2"><i class="bi bi-shield-lock me-1"></i><span data-i18n="login.demoAdmin">Διαχειριστής</span></span>
                                </div>
                                <div class="text-end text-white-50">
                                    <code class="text-light demo-email" style="cursor:pointer" data-email="admin@recruiting.com" data-password="admin123">admin@recruiting.com</code>
                                </div>
                            </div>
                            <div class="d-flex justify-content-between align-items-center mb-2 p-2 rounded-3" style="background: rgba(255,255,255,0.05);">
                                <div>
                                    <span class="badge bg-primary bg-opacity-75 me-2"><i class="bi bi-person-badge me-1"></i><span data-i18n="login.demoRecruiter">Recruiter</span></span>
                                </div>
                                <div class="text-end text-white-50">
                                    <code class="text-light demo-email" style="cursor:pointer" data-email="recruiter@recruiting.com" data-password="recruiter123">recruiter@recruiting.com</code>
                                </div>
                            </div>
                            <div class="d-flex justify-content-between align-items-center p-2 rounded-3" style="background: rgba(255,255,255,0.05);">
                                <div>
                                    <span class="badge bg-success bg-opacity-75 me-2"><i class="bi bi-person me-1"></i><span data-i18n="login.demoCandidate">Υποψήφιος</span></span>
                                </div>
                                <div class="text-end text-white-50">
                                    <code class="text-light demo-email" style="cursor:pointer" data-email="candidate@recruiting.com" data-password="candidate123">candidate@recruiting.com</code>
                                </div>
                            </div>
                            <p class="text-center text-white-50 mt-2 mb-0" style="font-size: 0.75rem;"><i class="bi bi-hand-index me-1"></i>Click to auto-fill</p>
                        </div>
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
    <script src="js/common.js"></script>
    <script src="js/i18n.js"></script>
    <script src="js/login.js"></script>
</body>

</html>
