<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.recruiting.model.User" %>
<% User user=(User) session.getAttribute("user"); if (user==null || !user.getRole().equals("ADMIN")) {
    response.sendRedirect("../login.jsp"); return; } %>
<!DOCTYPE html>
<html lang="el">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>

<body>
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
                    <li class="nav-item"><a class="nav-link active fw-bold" href="dashboard.jsp" data-i18n="nav.dashboard">Dashboard</a></li>
                    <li class="nav-item"><a class="nav-link" href="../candidate/profile.jsp" data-i18n="nav.profile">Προφίλ</a></li>
                    <li class="nav-item"><span class="navbar-text ms-2 me-2 text-light fw-semibold"><i class="bi bi-shield-lock me-1"></i><%= user.getName() %></span></li>
                    <li class="nav-item"><a class="btn btn-outline-light btn-sm" href="../api/logout" data-i18n="nav.logout">Αποσύνδεση</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="main-content container mt-5">
        <h2 class="mb-4" data-i18n="admin.dashboardTitle">Dashboard - Διαχειριστής</h2>

        <!-- Statistics Cards -->
        <div class="row">
            <div class="col-md-4">
                <div class="card dashboard-card">
                    <div class="card-body text-center">
                        <h5 data-i18n="admin.users">Χρήστες</h5>
                        <p class="display-4" id="usersCount">-</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card dashboard-card">
                    <div class="card-body text-center">
                        <h5 data-i18n="admin.jobs">Θέσεις</h5>
                        <p class="display-4" id="jobsCount">-</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card dashboard-card">
                    <div class="card-body text-center">
                        <h5 data-i18n="admin.applications">Αιτήσεις</h5>
                        <p class="display-4" id="applicationsCount">-</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- All Users Section -->
        <div class="row mt-4">
            <div class="col-md-12">
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h4 class="mb-0" data-i18n="admin.userManagement">Διαχείριση Χρηστών</h4>
                    </div>
                    <div class="card-body">
                        <div id="allUsersContainer">
                            <div class="text-center">
                                <div class="spinner-border" role="status">
                                    <span class="visually-hidden" data-i18n="common.loading">Φόρτωση...</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- All Jobs Section -->
        <div class="row mt-4">
            <div class="col-md-12">
                <div class="card">
                    <div class="card-header">
                        <h4 class="mb-0" data-i18n="admin.allJobs">Όλες οι Θέσεις</h4>
                    </div>
                    <div class="card-body">
                        <div id="allJobsContainer">
                            <div class="text-center">
                                <div class="spinner-border" role="status">
                                    <span class="visually-hidden" data-i18n="common.loading">Φόρτωση...</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- All Applications Section -->
        <div class="row mt-4">
            <div class="col-md-12">
                <div class="card">
                    <div class="card-header">
                        <h4 class="mb-0" data-i18n="admin.allApplications">Όλες οι Αιτήσεις</h4>
                    </div>
                    <div class="card-body">
                        <div class="row mb-3">
                            <div class="col-md-5">
                                <div class="input-group">
                                    <input type="text" class="form-control" id="appSearchInput"
                                        data-i18n-placeholder="admin.searchPlaceholder" placeholder="Αναζήτηση (θέση, εταιρεία, υποψήφιος...)">
                                    <button class="btn btn-outline-primary" type="button" id="appSearchBtn" data-i18n="admin.search">Αναζήτηση</button>
                                    <button class="btn btn-outline-secondary" type="button" id="appClearSearchBtn" data-i18n="admin.clear">Καθαρισμός</button>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <select class="form-select" id="appStatusFilter">
                                    <option value="" data-i18n="admin.allStatuses">Όλες οι καταστάσεις</option>
                                    <option value="PENDING" data-i18n="status.pending">Εκκρεμεί</option>
                                    <option value="REVIEWED" data-i18n="status.reviewed">Εξετάζεται</option>
                                    <option value="ACCEPTED" data-i18n="status.accepted">Αποδεκτή</option>
                                    <option value="REJECTED" data-i18n="status.rejected">Απορρίφθηκε</option>
                                </select>
                            </div>
                        </div>
                        <div id="allApplicationsContainer">
                            <div class="text-center">
                                <div class="spinner-border" role="status">
                                    <span class="visually-hidden" data-i18n="common.loading">Φόρτωση...</span>
                                </div>
                            </div>
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
    <script src="../js/common.js"></script>
    <script src="../js/i18n.js"></script>
    <script src="../js/admin-dashboard.js"></script>
</body>

</html>
