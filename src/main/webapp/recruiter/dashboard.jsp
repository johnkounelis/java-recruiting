<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.recruiting.model.User" %>
<% User user=(User) session.getAttribute("user"); if (user==null || (!user.getRole().equals("RECRUITER") &&
    !user.getRole().equals("ADMIN"))) { response.sendRedirect("../login.jsp"); return; } %>
<!DOCTYPE html>
<html lang="el">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Recruiter</title>
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
                    <li class="nav-item"><span class="navbar-text ms-2 me-2 text-light fw-semibold"><i class="bi bi-person-circle me-1"></i><%= user.getName() %></span></li>
                    <li class="nav-item"><a class="btn btn-outline-light btn-sm" href="../api/logout" data-i18n="nav.logout">Αποσύνδεση</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="main-content container mt-5">
        <h2 class="mb-4" data-i18n="recruiter.dashboardTitle">Dashboard - Recruiter</h2>

        <!-- Statistics Cards -->
        <div class="row mb-4">
            <div class="col-md-4">
                <div class="card text-center">
                    <div class="card-body">
                        <h5 class="card-title" data-i18n="recruiter.myJobs">Οι Θέσεις Μου</h5>
                        <p class="display-4 mb-0" id="myJobsCount">-</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center bg-success text-white">
                    <div class="card-body">
                        <h5 class="card-title" data-i18n="recruiter.active">Ενεργές</h5>
                        <p class="display-4 mb-0" id="activeJobsCount">-</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card text-center bg-info text-white">
                    <div class="card-body">
                        <h5 class="card-title" data-i18n="recruiter.totalApplications">Συνολικές Αιτήσεις</h5>
                        <p class="display-4 mb-0" id="totalApplicationsCount">-</p>
                    </div>
                </div>
            </div>
        </div>

        <div class="row mb-4">
            <div class="col-md-12">
                <button class="btn btn-primary btn-lg" data-bs-toggle="modal"
                    data-bs-target="#createJobModal">
                    + <span data-i18n="recruiter.createJob">Δημιούργησε Νέα Θέση</span>
                </button>
            </div>
        </div>

        <div class="row">
            <div class="col-md-12">
                <div class="card">
                    <div class="card-header">
                        <h4 data-i18n="recruiter.myJobs">Οι Θέσεις Μου</h4>
                    </div>
                    <div class="card-body">
                        <div id="jobsContainer">
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

    <!-- Create Job Modal -->
    <div class="modal fade" id="createJobModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" data-i18n="recruiter.createJob">Δημιούργησε Νέα Θέση</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div id="createJobAlert"></div>
                    <form id="createJobForm">
                        <div class="mb-3">
                            <label for="title" class="form-label" data-i18n="recruiter.jobTitle">Τίτλος</label>
                            <input type="text" class="form-control" id="title" required minlength="3">
                        </div>
                        <div class="mb-3">
                            <label for="company" class="form-label" data-i18n="recruiter.company">Εταιρεία</label>
                            <input type="text" class="form-control" id="company" required>
                        </div>
                        <div class="mb-3">
                            <label for="location" class="form-label" data-i18n="recruiter.location">Τοποθεσία</label>
                            <input type="text" class="form-control" id="location" required>
                        </div>
                        <div class="mb-3">
                            <label for="category" class="form-label" data-i18n="recruiter.category">Κατηγορία</label>
                            <select class="form-select" id="category" required>
                                <option value="IT" data-i18n="jobs.it">Πληροφορική (IT)</option>
                                <option value="Marketing" data-i18n="jobs.marketing">Μάρκετινγκ</option>
                                <option value="Sales" data-i18n="jobs.sales">Πωλήσεις (Sales)</option>
                                <option value="HR" data-i18n="jobs.hr">Ανθρώπινο Δυναμικό (HR)</option>
                                <option value="OTHER" data-i18n="jobs.other">Άλλο</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="description" class="form-label" data-i18n="recruiter.description">Περιγραφή</label>
                            <textarea class="form-control" id="description" rows="5" required
                                minlength="10"></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary" data-i18n="recruiter.create">Δημιούργησε</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Edit Job Modal -->
    <div class="modal fade" id="editJobModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" data-i18n="recruiter.editJob">Επεξεργασία Θέσης</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div id="editJobAlert"></div>
                    <form id="editJobForm">
                        <input type="hidden" id="editJobId">
                        <div class="mb-3">
                            <label for="editTitle" class="form-label" data-i18n="recruiter.jobTitle">Τίτλος</label>
                            <input type="text" class="form-control" id="editTitle" required minlength="3">
                        </div>
                        <div class="mb-3">
                            <label for="editCompany" class="form-label" data-i18n="recruiter.company">Εταιρεία</label>
                            <input type="text" class="form-control" id="editCompany" required>
                        </div>
                        <div class="mb-3">
                            <label for="editLocation" class="form-label" data-i18n="recruiter.location">Τοποθεσία</label>
                            <input type="text" class="form-control" id="editLocation" required>
                        </div>
                        <div class="mb-3">
                            <label for="editCategory" class="form-label" data-i18n="recruiter.category">Κατηγορία</label>
                            <select class="form-select" id="editCategory" required>
                                <option value="IT" data-i18n="jobs.it">Πληροφορική (IT)</option>
                                <option value="Marketing" data-i18n="jobs.marketing">Μάρκετινγκ</option>
                                <option value="Sales" data-i18n="jobs.sales">Πωλήσεις (Sales)</option>
                                <option value="HR" data-i18n="jobs.hr">Ανθρώπινο Δυναμικό (HR)</option>
                                <option value="OTHER" data-i18n="jobs.other">Άλλο</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="editDescription" class="form-label" data-i18n="recruiter.description">Περιγραφή</label>
                            <textarea class="form-control" id="editDescription" rows="5" required
                                minlength="10"></textarea>
                        </div>
                        <div class="mb-3">
                            <label for="editStatus" class="form-label" data-i18n="jobDetails.status">Κατάσταση</label>
                            <select class="form-select" id="editStatus" required>
                                <option value="ACTIVE" data-i18n="recruiter.statusActive">Ενεργή</option>
                                <option value="CLOSED" data-i18n="recruiter.statusClosed">Κλειστή</option>
                            </select>
                        </div>
                        <button type="submit" class="btn btn-primary" data-i18n="recruiter.update">Ενημέρωση</button>
                    </form>
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
    <script src="../js/recruiter-dashboard.js"></script>
</body>

</html>
