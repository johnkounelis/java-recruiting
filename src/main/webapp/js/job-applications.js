$(document).ready(function () {
    var urlParams = new URLSearchParams(window.location.search);
    var jobId = urlParams.get('jobId');

    function t(key, fallback) {
        if (typeof i18n !== 'undefined' && i18n.isReady()) {
            return i18n.t(key, fallback);
        }
        return fallback || key;
    }

    if (!jobId) {
        $('#applicationsContainer').html('<div class="alert alert-danger">Invalid Job ID</div>');
        return;
    }

    loadJobInfo(jobId);
    loadApplications(jobId);

    function loadJobInfo(jobId) {
        $.ajax({
            url: '../api/jobs/' + jobId,
            method: 'GET',
            success: function (response) {
                if (response.success) {
                    var job = response.job;
                    var jobInfoHtml =
                        '<div class="card mb-4">' +
                            '<div class="card-body">' +
                                '<h4>' + escapeHtml(job.title) + '</h4>' +
                                '<h6 class="text-muted">' + escapeHtml(job.company) + ' - ' + escapeHtml(job.location) + '</h6>' +
                            '</div>' +
                        '</div>';
                    $('#applicationsContainer').prepend(jobInfoHtml);
                }
            }
        });
    }

    function loadApplications(jobId, searchTerm, statusFilter) {
        var url = '../api/applications/job/' + jobId;
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
                } else {
                    showNoResults();
                }
            },
            error: function () {
                $('#applicationsContainer').find('.card.mb-4').nextAll().remove();
                $('#applicationsContainer').append('<div class="alert alert-danger">' + t('common.loadError', 'Error loading') + '</div>');
            }
        });
    }

    function showNoResults() {
        var hasFilters = $('#searchInput').val() || $('#statusFilter').val();
        var message = hasFilters
            ? t('recruiter.noApplicationsSearch', 'No applications found matching your criteria')
            : t('recruiter.noApplicationsForJob', 'No applications for this job');
        $('#applicationsContainer').find('.card:not(:first)').remove();
        $('#applicationsContainer').find('.alert').remove();
        $('#applicationsContainer').append('<div class="alert alert-info">' + message + '</div>');
    }

    function displayApplications(applications) {
        if (applications.length === 0) {
            showNoResults();
            return;
        }

        $('#applicationsContainer').find('.card:not(:first)').remove();
        $('#applicationsContainer').find('.alert').remove();

        var html = '<div class="card"><div class="card-header"><h5>' + t('recruiter.applications', 'Applications') + ' (' + applications.length + ')</h5></div><div class="card-body">';
        html += '<table class="table table-striped">';
        html += '<thead><tr>' +
            '<th>' + t('recruiter.candidateCol', 'Candidate') + '</th>' +
            '<th>' + t('candidate.status', 'Status') + '</th>' +
            '<th>' + t('candidate.date', 'Date') + '</th>' +
            '<th>' + t('candidate.notes', 'Notes') + '</th>' +
            '<th>' + t('admin.actions', 'Actions') + '</th>' +
            '</tr></thead>';
        html += '<tbody>';

        applications.forEach(function (app) {
            html += '<tr id="app-row-' + app.id + '">';
            var candidateDisplay = app.candidateName
                ? escapeHtml(app.candidateName)
                : 'Candidate #' + app.candidateId;
            html += '<td>' + candidateDisplay + '</td>';
            html += '<td><span class="badge bg-' + getStatusColor(app.status) + '">' + getStatusText(app.status) + '</span></td>';
            html += '<td>' + formatDate(app.appliedDate) + '</td>';
            html += '<td>' + escapeHtml(app.notes || '-');
            if (app.resumeFilename) {
                html += ' <a href="../api/resume/' + app.id + '" class="badge bg-primary text-decoration-none ms-1" title="Download CV"><i class="bi bi-file-earmark-arrow-down me-1"></i>CV</a>';
            }
            html += '</td>';
            html += '<td>';
            html += '<select class="form-select form-select-sm status-select" data-app-id="' + app.id + '" style="width: auto; display: inline-block;">';
            html += '<option value="PENDING"' + (app.status === 'PENDING' ? ' selected' : '') + '>PENDING</option>';
            html += '<option value="REVIEWED"' + (app.status === 'REVIEWED' ? ' selected' : '') + '>REVIEWED</option>';
            html += '<option value="ACCEPTED"' + (app.status === 'ACCEPTED' ? ' selected' : '') + '>ACCEPTED</option>';
            html += '<option value="REJECTED"' + (app.status === 'REJECTED' ? ' selected' : '') + '>REJECTED</option>';
            html += '</select>';
            html += '</td>';
            html += '</tr>';
        });

        html += '</tbody></table></div></div>';
        $('#applicationsContainer').append(html);

        // Add change handlers
        $('.status-select').on('change', function () {
            var appId = $(this).data('app-id');
            var newStatus = $(this).val();
            updateApplicationStatus(appId, newStatus);
        });
    }

    function updateApplicationStatus(appId, status) {
        $.ajax({
            url: '../api/applications/update',
            method: 'POST',
            data: {
                id: appId,
                status: status,
                notes: ''
            },
            success: function (response) {
                if (response.success) {
                    var $row = $('#app-row-' + appId);
                    $row.find('.badge').removeClass('bg-success bg-danger bg-warning bg-secondary')
                        .addClass('bg-' + getStatusColor(status)).text(getStatusText(status));
                } else {
                    alert(t('common.error', 'Error') + ': ' + response.message);
                }
            },
            error: function () {
                alert(t('common.updateError', 'Error updating'));
            }
        });
    }

    function escapeHtml(text) {
        var map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
        return String(text || '').replace(/[&<>"']/g, function (m) { return map[m]; });
    }

    // Search & filter event handlers
    $('#searchBtn').on('click', function () {
        loadApplications(jobId, $('#searchInput').val(), $('#statusFilter').val());
    });

    $('#searchInput').on('keypress', function (e) {
        if (e.which === 13) {
            $('#searchBtn').click();
        }
    });

    $('#clearSearchBtn').on('click', function () {
        $('#searchInput').val('');
        $('#statusFilter').val('');
        loadApplications(jobId);
    });

    $('#statusFilter').on('change', function () {
        loadApplications(jobId, $('#searchInput').val(), $(this).val());
    });
});
