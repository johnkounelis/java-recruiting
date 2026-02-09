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
    <title>Εγγραφή - Recruiting App</title>
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
                </ul>
            </div>
        </div>
    </nav>

    <div class="main-content container mt-5 pt-3">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5">
                <div class="card shadow-lg border-0 rounded-4 mb-5">
                    <div class="card-header bg-white border-bottom-0 pt-4 pb-0 rounded-top-4">
                        <div class="text-center mb-3">
                            <div class="bg-primary bg-opacity-10 d-inline-flex align-items-center justify-content-center rounded-circle mb-3" style="width: 64px; height: 64px;">
                                <i class="bi bi-person-plus text-primary" style="font-size: 2rem;"></i>
                            </div>
                        </div>
                        <h3 class="text-center fw-bold text-primary" data-i18n="register.title">Εγγραφή</h3>
                        <p class="text-center text-muted mb-0" data-i18n="register.subtitle">Δημιουργήστε ένα νέο λογαριασμό</p>
                    </div>
                    <div class="card-body p-4 p-md-5">
                        <div id="alertMessage"></div>
                        <form id="registerForm">
                            <div class="mb-3">
                                <label for="name" class="form-label text-muted fw-semibold" data-i18n="register.name">Όνομα</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-end-0"><i class="bi bi-person text-muted"></i></span>
                                    <input type="text" class="form-control bg-light border-start-0" id="name" name="name"
                                        data-i18n-placeholder="register.namePlaceholder" placeholder="Το όνομά σας" required>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="email" class="form-label text-muted fw-semibold" data-i18n="register.email">Email</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-end-0"><i class="bi bi-envelope text-muted"></i></span>
                                    <input type="email" class="form-control bg-light border-start-0" id="email" name="email"
                                        placeholder="name@example.com" required>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="password" class="form-label text-muted fw-semibold" data-i18n="register.password">Κωδικός</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-end-0"><i class="bi bi-lock text-muted"></i></span>
                                    <input type="password" class="form-control bg-light border-start-0 border-end-0" id="password"
                                        name="password" placeholder="••••••••" required minlength="6">
                                    <button class="btn bg-light border border-start-0" type="button" id="togglePassword" tabindex="-1">
                                        <i class="bi bi-eye text-muted" id="togglePasswordIcon"></i>
                                    </button>
                                </div>
                                <div class="form-text small" data-i18n="register.passwordHint">Τουλάχιστον 6 χαρακτήρες</div>
                                <!-- Password Strength Bar -->
                                <div class="mt-2" id="passwordStrengthContainer" style="display:none;">
                                    <div class="progress" style="height: 6px;">
                                        <div class="progress-bar" id="passwordStrengthBar" role="progressbar" style="width: 0%"></div>
                                    </div>
                                    <small id="passwordStrengthText" class="form-text"></small>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="confirmPassword"
                                    class="form-label text-muted fw-semibold" data-i18n="register.confirmPassword">Επιβεβαίωση Κωδικού</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-end-0"><i class="bi bi-lock-fill text-muted"></i></span>
                                    <input type="password" class="form-control bg-light border-start-0" id="confirmPassword"
                                        name="confirmPassword" placeholder="••••••••" required>
                                </div>
                                <div id="passwordMatch" class="form-text small"></div>
                            </div>
                            <div class="mb-4">
                                <label for="role" class="form-label text-muted fw-semibold" data-i18n="register.role">Ρόλος</label>
                                <div class="row g-2">
                                    <div class="col-6">
                                        <input type="radio" class="btn-check" name="roleRadio" id="roleCandidate" value="CANDIDATE" checked>
                                        <label class="btn btn-outline-primary w-100 py-3 rounded-3" for="roleCandidate">
                                            <i class="bi bi-person-fill d-block mb-1" style="font-size: 1.5rem;"></i>
                                            <span class="fw-bold" data-i18n="register.candidate">Υποψήφιος</span>
                                            <br><small class="text-muted" data-i18n="register.candidateDesc">Αναζήτηση θέσεων εργασίας και υποβολή αιτήσεων</small>
                                        </label>
                                    </div>
                                    <div class="col-6">
                                        <input type="radio" class="btn-check" name="roleRadio" id="roleRecruiter" value="RECRUITER">
                                        <label class="btn btn-outline-primary w-100 py-3 rounded-3" for="roleRecruiter">
                                            <i class="bi bi-person-badge-fill d-block mb-1" style="font-size: 1.5rem;"></i>
                                            <span class="fw-bold" data-i18n="register.recruiter">Recruiter</span>
                                            <br><small class="text-muted" data-i18n="register.recruiterDesc">Δημοσίευση θέσεων και διαχείριση αιτήσεων</small>
                                        </label>
                                    </div>
                                </div>
                                <input type="hidden" id="role" name="role" value="CANDIDATE">
                            </div>
                            <button type="submit" class="btn btn-primary btn-lg w-100 fw-bold rounded-3"
                                id="submitBtn">
                                <span id="submitText" data-i18n="register.createAccount">Δημιουργία Λογαριασμού</span>
                                <span id="submitSpinner" class="spinner-border spinner-border-sm d-none"
                                    role="status"></span>
                            </button>
                        </form>
                        <div class="text-center mt-4 pt-2 border-top">
                            <p class="text-muted mb-0"><span data-i18n="register.hasAccount">Έχετε ήδη λογαριασμό;</span> <a href="login.jsp"
                                    class="fw-semibold text-decoration-none" data-i18n="register.loginHere">Συνδεθείτε</a></p>
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
    <script src="js/register.js"></script>
</body>

</html>
