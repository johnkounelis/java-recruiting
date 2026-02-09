<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="el">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Θέσεις Εργασίας - Recruiting App</title>
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
                    <li class="nav-item"><a class="nav-link active fw-bold" href="jobs.jsp"
                            data-i18n="nav.jobs">Θέσεις</a></li>
                    <li class="nav-item" id="dashboardLinkContainer" style="display:none;"><a class="nav-link" href="#" id="dashboardLink" data-i18n="nav.dashboard">Dashboard</a></li>
                    <li class="nav-item"><span id="userInfo"
                            class="navbar-text ms-3 me-3 text-light fw-semibold"></span></li>
                    <li class="nav-item"><a class="btn btn-light btn-sm text-primary fw-bold" href="login.jsp"
                            id="loginLink" data-i18n="nav.loginBtn">Σύνδεση</a></li>
                    <li class="nav-item"><a class="btn btn-outline-light btn-sm d-none" href="api/logout"
                            id="logoutLink" data-i18n="nav.logout">Αποσύνδεση</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="main-content container mt-5 pt-3">
        <div class="d-flex justify-content-between align-items-end mb-4">
            <div>
                <h2 class="display-6 fw-bold mb-1" data-i18n="jobs.title">Διαθέσιμες Θέσεις Εργασίας</h2>
                <p class="text-muted fs-5 mb-0" data-i18n="jobs.subtitle">Ανακαλύψτε την επόμενη επαγγελματική σας ευκαιρία.</p>
            </div>
        </div>

        <div class="card shadow-md border-0 mb-5 p-3">
            <div class="card-body">
                <div class="row g-3 align-items-center">
                    <div class="col-md-6">
                        <div class="input-group input-group-lg shadow-sm rounded-3">
                            <span class="input-group-text bg-white border-end-0 text-muted"><i
                                    class="bi bi-search"></i></span>
                            <input type="text" class="form-control border-start-0 ps-0" id="searchInput"
                                data-i18n-placeholder="jobs.searchPlaceholder" placeholder="Αναζήτηση (τίτλος, εταιρεία...)">
                        </div>
                    </div>
                    <div class="col-md-3">
                        <select class="form-select form-select-lg shadow-sm" id="categoryFilter">
                            <option value="" data-i18n="jobs.allCategories">Όλες οι Κατηγορίες</option>
                            <option value="IT" data-i18n="jobs.it">Πληροφορική (IT)</option>
                            <option value="Marketing" data-i18n="jobs.marketing">Μάρκετινγκ</option>
                            <option value="Sales" data-i18n="jobs.sales">Πωλήσεις (Sales)</option>
                            <option value="HR" data-i18n="jobs.hr">Ανθρώπινο Δυναμικό (HR)</option>
                            <option value="OTHER" data-i18n="jobs.other">Άλλο</option>
                        </select>
                    </div>
                    <div class="col-md-3 d-flex gap-2">
                        <button class="btn btn-primary btn-lg flex-grow-1 fw-bold" type="button" id="searchBtn" data-i18n="jobs.search">
                            Αναζήτηση
                        </button>
                        <button class="btn btn-outline-secondary btn-lg" type="button" id="clearSearchBtn"
                            data-i18n-title="jobs.clear" title="Καθαρισμός">
                            <i class="bi bi-x-lg"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <div id="jobsContainer" class="row g-4 pb-4">
            <div class="col-12 text-center py-5">
                <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">
                    <span class="visually-hidden" data-i18n="jobs.loadingSpinner">Φόρτωση...</span>
                </div>
                <p class="mt-3 text-muted fw-semibold" data-i18n="jobs.loading">Φόρτωση θέσεων...</p>
            </div>
        </div>

        <nav aria-label="Job pagination" class="pb-5">
            <ul id="paginationContainer" class="pagination justify-content-center mb-0">
            </ul>
        </nav>
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
    <script src="js/jobs.js"></script>
</body>

</html>
