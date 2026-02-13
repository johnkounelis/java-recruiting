$(document).ready(function () {
    loadStatistics();
    loadAllUsers();
    loadAllJobs();
    loadAllApplications();

    function t(key, fallback) {
        if (typeof i18n !== 'undefined' && i18n.isReady()) {
            return i18n.t(key, fallback);
        }
        return fallback || key;
    }

    // ---- Statistics ----
    function loadStatistics() {
        $.ajax({
            url: '../api/statistics',
            method: 'GET',
            success: function (response) {
                if (response.success && response.statistics) {
                    var stats = response.statistics;
                    $('#usersCount').text(stats.totalUsers || 0);
                    $('#jobsCount').text(stats.totalJobs || 0);
                    $('#applicationsCount').text(stats.totalApplications || 0);
                }
            }
        });
    }

    // ---- Users Management ----
    function loadAllUsers() {
        $.ajax({
            url: '../api/users',
            method: 'GET',
            success: function (response) {
                if (response.success) {
                    displayAllUsers(response.users);
                } else {
                    $('#allUsersContainer').html('<div class="alert alert-info">' + t('admin.noUsers', 'No users found') + '</div>');
                }
            },
            error: function () {
                $('#allUsersContainer').html('<div class="alert alert-danger">' + t('admin.loadError', 'Error loading') + '</div>');
            }
        });
    }

    function displayAllUsers(users) {
        if (users.length === 0) {
            $('#allUsersContainer').html(
                '<div class="empty-state text-center p-4">' +
                    '<i class="bi bi-people text-muted" style="font-size: 2.5rem;"></i>' +
                    '<h5 class="mt-2 text-muted">' + t('admin.noUsers', 'No users found') + '</h5>' +
                '</div>'
            );
            return;
        }

        var html = '<table class="table table-striped table-hover">';
        html += '<thead><tr>' +
            '<th>' + t('admin.id', 'ID') + '</th>' +
            '<th>' + t('admin.name', 'Name') + '</th>' +
            '<th>' + t('admin.email', 'Email') + '</th>' +
            '<th>' + t('admin.role', 'Role') + '</th>' +
            '<th>' + t('admin.actions', 'Actions') + '</th>' +
            '</tr></thead>';
        html += '<tbody>';

        users.forEach(function (u) {
            html += '<tr id="user-row-' + u.id + '">';
            html += '<td>' + u.id + '</td>';
            html += '<td>' + escapeHtml(u.name) + '</td>';
            html += '<td>' + escapeHtml(u.email) + '</td>';
            html += '<td>';
            html += '<select class="form-select form-select-sm role-select" data-user-id="' + u.id + '" style="width: auto; display: inline-block;">';
            html += '<option value="CANDIDATE"' + (u.role === 'CANDIDATE' ? ' selected' : '') + '>CANDIDATE</option>';
            html += '<option value="RECRUITER"' + (u.role === 'RECRUITER' ? ' selected' : '') + '>RECRUITER</option>';
            html += '<option value="ADMIN"' + (u.role === 'ADMIN' ? ' selected' : '') + '>ADMIN</option>';
            html += '</select>';
            html += '</td>';
            html += '<td>';
            html += '<button class="btn btn-sm btn-danger delete-user-btn" data-user-id="' + u.id + '" data-user-name="' + escapeHtml(u.name) + '">' + t('admin.delete', 'Delete') + '</button>';
            html += '</td>';
            html += '</tr>';
        });

        html += '</tbody></table>';
        html += '<p class="text-muted">' + t('common.total', 'Total') + ': ' + users.length + ' ' + t('common.users', 'users') + '</p>';
        $('#allUsersContainer').html(html);

        // Event handlers
        $('.role-select').on('change', function () {
            var userId = $(this).data('user-id');
            var newRole = $(this).val();
            changeUserRole(userId, newRole);
        });

        $('.delete-user-btn').on('click', function () {
            var userId = $(this).data('user-id');
            var userName = $(this).data('user-name');
            if (confirm(t('admin.deleteConfirm', 'Are you sure you want to delete user') + ' "' + userName + '";')) {
                deleteUser(userId);
            }
        });
    }

    function changeUserRole(userId, newRole) {
        $.ajax({
            url: '../api/users/' + userId,
            method: 'PUT',
            data: { role: newRole },
            success: function (response) {
                if (response.success) {
                    $('#user-row-' + userId).css('background-color', '#d4edda').delay(1000).queue(function (next) {
                        $(this).css('background-color', '');
                        next();
                    });
                } else {
                    alert(response.message);
                    loadAllUsers();
                }
            },
            error: function () {
                alert(t('admin.roleChangeError', 'Error changing role'));
                loadAllUsers();
            }
        });
    }

    function deleteUser(userId) {
        $.ajax({
            url: '../api/users/' + userId,
            method: 'DELETE',
            success: function (response) {
                if (response.success) {
                    $('#user-row-' + userId).fadeOut(400, function () {
                        $(this).remove();
                    });
                    loadStatistics();
                } else {
                    alert(response.message);
                }
            },
            error: function () {
                alert(t('admin.deleteError', 'Error deleting'));
            }
        });
    }

    // ---- Jobs ----
    function loadAllJobs() {
        $.ajax({
            url: '../api/jobs',
            method: 'GET',
            success: function (response) {
                if (response.success) {
                    displayAllJobs(response.jobs);
                } else {
                    $('#allJobsContainer').html('<div class="alert alert-info">' + t('admin.noJobs', 'No jobs found') + '</div>');
                }
            },
            error: function () {
                $('#allJobsContainer').html('<div class="alert alert-danger">' + t('admin.loadError', 'Error loading') + '</div>');
            }
        });
    }

    function displayAllJobs(jobs) {
        if (jobs.length === 0) {
            $('#allJobsContainer').html(
                '<div class="empty-state text-center p-4">' +
                    '<i class="bi bi-briefcase text-muted" style="font-size: 2.5rem;"></i>' +
                    '<h5 class="mt-2 text-muted">' + t('admin.noJobs', 'No jobs found') + '</h5>' +
                '</div>'
            );
            return;
        }

        var html = '<table class="table table-striped">';
        html += '<thead><tr>' +
            '<th>' + t('admin.id', 'ID') + '</th>' +
            '<th>' + t('recruiter.jobTitle', 'Title') + '</th>' +
            '<th>' + t('recruiter.company', 'Company') + '</th>' +
            '<th>' + t('recruiter.location', 'Location') + '</th>' +
            '<th>' + t('jobDetails.status', 'Status') + '</th>' +
            '<th>' + t('admin.applications', 'Applications') + '</th>' +
            '</tr></thead>';
        html += '<tbody>';

        jobs.forEach(function (job) {
            html += '<tr>';
            html += '<td>' + job.id + '</td>';
            html += '<td><a href="../job-details.jsp?id=' + job.id + '">' + escapeHtml(job.title) + '</a></td>';
            html += '<td>' + escapeHtml(job.company) + '</td>';
            html += '<td>' + escapeHtml(job.location) + '</td>';
            html += '<td><span class="badge bg-' + (job.status === 'ACTIVE' ? 'success' : 'secondary') + '">' + escapeHtml(job.status) + '</span></td>';
            html += '<td><a href="../recruiter/job-applications.jsp?jobId=' + job.id + '" class="btn btn-sm btn-outline-primary">' + t('recruiter.viewApplications', 'View Applications') + '</a></td>';
            html += '</tr>';
        });

        html += '</tbody></table>';
        html += '<p class="text-muted">' + t('common.total', 'Total') + ': ' + jobs.length + ' ' + t('common.positions', 'positions') + '</p>';
        $('#allJobsContainer').html(html);
    }

    // ---- All Applications ----
    function loadAllApplications(searchTerm, statusFilter) {
        var url = '../api/applications/all';
        var params = [];
        if (searchTerm && searchTerm.trim() !== '') {
            params.push('search=' + encodeURIComponent(searchTerm.trim()));
        }
        if (statusFilter && statusFilter.trim() !== '') {
            params.push('status=' + encodeURIComponent(statusFilter.trim()));
        }
        if (params.length > 0) {
            url += '?' + params.join('&');
        }

        $.ajax({
            url: url,
            method: 'GET',
            success: function (response) {
                if (response.success) {
                    displayAllApplications(response.applications);
                } else {
                    $('#allApplicationsContainer').html('<div class="alert alert-info">' + t('admin.noApplications', 'No applications') + '</div>');
                }
            },
            error: function () {
                $('#allApplicationsContainer').html('<div class="alert alert-danger">' + t('admin.loadError', 'Error loading') + '</div>');
            }
        });
    }

    function displayAllApplications(applications) {
        if (applications.length === 0) {
            var hasFilters = $('#appSearchInput').val() || $('#appStatusFilter').val();
            var message = hasFilters
                ? t('admin.noApplicationsSearch', 'No applications found matching your criteria.')
                : t('admin.noApplications', 'No applications found');
            $('#allApplicationsContainer').html(
                '<div class="empty-state text-center p-4">' +
                    '<i class="bi bi-file-earmark-text text-muted" style="font-size: 2.5rem;"></i>' +
                    '<h5 class="mt-2 text-muted">' + escapeHtml(message) + '</h5>' +
                '</div>'
            );
            return;
        }

        var html = '<table class="table table-striped table-hover">';
        html += '<thead><tr>' +
            '<th>' + t('admin.id', 'ID') + '</th>' +
            '<th>' + t('candidate.position', 'Position') + '</th>' +
            '<th>' + t('candidate.company', 'Company') + '</th>' +
            '<th>' + t('recruiter.candidateCol', 'Candidate') + '</th>' +
            '<th>' + t('candidate.status', 'Status') + '</th>' +
            '<th>' + t('candidate.date', 'Date') + '</th>' +
            '<th>CV</th>' +
            '</tr></thead>';
        html += '<tbody>';

        applications.forEach(function (app) {
            html += '<tr>';
            html += '<td>' + app.id + '</td>';
            html += '<td>' + (app.jobTitle ? '<a href="../job-details.jsp?id=' + app.jobId + '">' + escapeHtml(app.jobTitle) + '</a>' : 'Job #' + app.jobId) + '</td>';
            html += '<td>' + escapeHtml(app.jobCompany || '-') + '</td>';
            html += '<td>' + escapeHtml(app.candidateName || 'Candidate #' + app.candidateId) + '</td>';
            html += '<td><span class="badge bg-' + getStatusColor(app.status) + '">' + getStatusText(app.status) + '</span></td>';
            html += '<td>' + formatDate(app.appliedDate) + '</td>';
            html += '<td>' + (app.resumeFilename ? '<a href="../api/resume/' + app.id + '" class="badge bg-primary text-decoration-none"><i class="bi bi-download me-1"></i>CV</a>' : '-') + '</td>';
            html += '</tr>';
        });

        html += '</tbody></table>';
        html += '<p class="text-muted">' + t('common.total', 'Total') + ': ' + applications.length + ' ' + t('common.applicationsCount', 'applications') + '</p>';
        $('#allApplicationsContainer').html(html);
    }

    // Search & filter handlers for applications
    $('#appSearchBtn').on('click', function () {
        loadAllApplications($('#appSearchInput').val(), $('#appStatusFilter').val());
    });
    $('#appSearchInput').on('keypress', function (e) {
        if (e.which === 13) $('#appSearchBtn').click();
    });
    $('#appClearSearchBtn').on('click', function () {
        $('#appSearchInput').val('');
        $('#appStatusFilter').val('');
        loadAllApplications();
    });
    $('#appStatusFilter').on('change', function () {
        loadAllApplications($('#appSearchInput').val(), $(this).val());
    });

    // ---- Utility functions ----
    function escapeHtml(text) {
        var map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
        return String(text || '').replace(/[&<>"']/g, function (m) { return map[m]; });
    }
});
