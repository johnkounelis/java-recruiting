$(document).ready(function () {
    loadStatistics();
    loadApplications();

    function t(key, fallback) {
        if (typeof i18n !== 'undefined' && i18n.isReady()) {
            return i18n.t(key, fallback);
        }
        return fallback || key;
    }

    function loadStatistics() {
        $.ajax({
            url: '../api/statistics',
            method: 'GET',
            success: function (response) {
                if (response.success && response.statistics) {
                    var stats = response.statistics;
                    $('#totalApplications').text(stats.myApplications || 0);
                    $('#pendingCount').text(stats.pendingApplications || 0);
                    $('#acceptedCount').text(stats.acceptedApplications || 0);
                    $('#reviewedCount').text(stats.reviewedApplications || 0);
                }
            },
            error: function () {
                // Statistics load failed, continue anyway
            }
        });
    }

    function loadApplications(searchTerm, statusFilter) {
        var url = '../api/applications';
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
                    displayApplications(response.applications);
                    updateStatisticsFromApplications(response.applications);
                } else {
                    $('#applicationsContainer').html('<div class="alert alert-info">' + t('candidate.noApplicationsMsg', 'No applications yet.') + '</div>');
                }
            },
            error: function (xhr) {
                var errorMsg = t('common.loadError', 'Error loading');
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMsg = xhr.responseJSON.message;
                }
                $('#applicationsContainer').html('<div class="alert alert-danger">' + errorMsg + '</div>');
            }
        });
    }

    function updateStatisticsFromApplications(applications) {
        var pending = applications.filter(function(a) { return a.status === 'PENDING'; }).length;
        var reviewed = applications.filter(function(a) { return a.status === 'REVIEWED'; }).length;
        var accepted = applications.filter(function(a) { return a.status === 'ACCEPTED'; }).length;

        if (!$('#searchInput').val() && !$('#statusFilter').val()) {
            $('#totalApplications').text(applications.length);
            $('#pendingCount').text(pending);
            $('#acceptedCount').text(accepted);
            $('#reviewedCount').text(reviewed);
        }
    }

    function displayApplications(applications) {
        if (applications.length === 0) {
            var hasFilters = $('#searchInput').val() || $('#statusFilter').val();
            var message = hasFilters
                ? t('candidate.noApplicationsSearch', 'No applications found matching your criteria.')
                : t('candidate.noApplicationsMsg', 'You haven\'t submitted any applications yet.');

            $('#applicationsContainer').html(
                '<div class="col-12">' +
                    '<div class="empty-state card border-0 shadow-sm bg-light bg-opacity-50 p-5 rounded-4 mt-3">' +
                        '<i class="bi bi-file-earmark-x mb-3" style="font-size: 3rem; color: #cbd5e1;"></i>' +
                        '<h5 class="fw-bold text-dark">' + t('candidate.noApplications', 'No Applications') + '</h5>' +
                        '<p class="text-muted">' + escapeHtml(message) + '</p>' +
                        (!hasFilters ? '<a href="../jobs.jsp" class="btn btn-primary mt-2">' + t('candidate.searchJobs', 'Search Jobs') + '</a>' : '') +
                    '</div>' +
                '</div>'
            );
            return;
        }

        var hasJoinedData = applications[0].jobTitle != null;

        if (hasJoinedData) {
            displayApplicationsTable(applications);
        } else {
            var jobPromises = applications.map(function (app) {
                return $.ajax({ url: '../api/jobs/' + app.jobId, method: 'GET' });
            });

            Promise.all(jobPromises).then(function (jobResponses) {
                applications.forEach(function (app, index) {
                    var jobResponse = jobResponses[index];
                    if (jobResponse.success && jobResponse.job) {
                        app.jobTitle = jobResponse.job.title;
                        app.jobCompany = jobResponse.job.company;
                    }
                });
                displayApplicationsTable(applications);
            }).catch(function () {
                displayApplicationsTable(applications);
            });
        }
    }

    function displayApplicationsTable(applications) {
        var html = '<table class="table table-striped table-hover">';
        html += '<thead><tr>' +
            '<th>' + t('candidate.position', 'Position') + '</th>' +
            '<th>' + t('candidate.company', 'Company') + '</th>' +
            '<th>' + t('candidate.status', 'Status') + '</th>' +
            '<th>' + t('candidate.date', 'Date') + '</th>' +
            '<th>' + t('candidate.notes', 'Notes') + '</th>' +
            '</tr></thead>';
        html += '<tbody>';

        applications.forEach(function (app) {
            html += '<tr>';
            if (app.jobTitle) {
                html += '<td><a href="../job-details.jsp?id=' + app.jobId + '">' + escapeHtml(app.jobTitle) + '</a></td>';
                html += '<td>' + escapeHtml(app.jobCompany || '-') + '</td>';
            } else {
                html += '<td><a href="../job-details.jsp?id=' + app.jobId + '">Job #' + app.jobId + '</a></td>';
                html += '<td>-</td>';
            }
            html += '<td><span class="badge bg-' + getStatusColor(app.status) + '">' + getStatusText(app.status) + '</span></td>';
            html += '<td>' + formatDate(app.appliedDate) + '</td>';
            html += '<td>' + escapeHtml(app.notes || '-');
            if (app.resumeFilename) {
                html += ' <span class="badge bg-success ms-1"><i class="bi bi-file-earmark-check me-1"></i>CV</span>';
            }
            html += '</td>';
            html += '</tr>';
        });

        html += '</tbody></table>';
        $('#applicationsContainer').html(html);
    }

    function escapeHtml(text) {
        var map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
        return String(text || '').replace(/[&<>"']/g, function (m) { return map[m]; });
    }

    // Search & filter event handlers
    $('#searchBtn').on('click', function () {
        loadApplications($('#searchInput').val(), $('#statusFilter').val());
    });

    $('#searchInput').on('keypress', function (e) {
        if (e.which === 13) {
            $('#searchBtn').click();
        }
    });

    $('#clearSearchBtn').on('click', function () {
        $('#searchInput').val('');
        $('#statusFilter').val('');
        loadApplications();
    });

    $('#statusFilter').on('change', function () {
        loadApplications($('#searchInput').val(), $(this).val());
    });
});
