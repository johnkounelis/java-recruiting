$(document).ready(function () {
    var currentUserId = null;

    function t(key, fallback) {
        if (typeof i18n !== 'undefined' && i18n.isReady()) {
            return i18n.t(key, fallback);
        }
        return fallback || key;
    }

    // Get current user info
    $.ajax({
        url: '../api/session',
        method: 'GET',
        success: function (response) {
            if (response.loggedIn && response.user) {
                currentUserId = response.user.id;
                loadStatistics();
                loadMyJobs();
            }
        }
    });

    function loadStatistics() {
        $.ajax({
            url: '../api/statistics',
            method: 'GET',
            success: function (response) {
                if (response.success && response.statistics) {
                    var stats = response.statistics;
                    $('#myJobsCount').text(stats.myJobs || 0);
                }
            },
            error: function () {}
        });
    }

    $('#createJobForm').on('submit', function (e) {
        e.preventDefault();
        createJob();
    });

    function loadMyJobs() {
        $.ajax({
            url: '../api/jobs',
            method: 'GET',
            success: function (response) {
                if (response.success) {
                    var myJobs = response.jobs.filter(function (job) {
                        return job.postedBy === currentUserId;
                    });
                    displayJobs(myJobs);
                } else {
                    $('#jobsContainer').html(
                        '<div class="empty-state text-center p-5 card border-0 shadow-sm rounded-4">' +
                            '<i class="bi bi-briefcase text-muted mb-3" style="font-size: 3rem;"></i>' +
                            '<h4 class="text-dark fw-bold">' + t('recruiter.noJobs', 'No jobs') + '</h4>' +
                            '<p class="text-muted">' + t('recruiter.noJobs', 'No jobs found') + '</p>' +
                            '<button class="btn btn-primary mt-2" data-bs-toggle="modal" data-bs-target="#createJobModal">+ ' + t('recruiter.createJob', 'Create New Job') + '</button>' +
                        '</div>'
                    );
                }
            },
            error: function (xhr) {
                var errorMsg = t('common.loadError', 'Error loading');
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMsg = xhr.responseJSON.message;
                }
                $('#jobsContainer').html('<div class="alert alert-danger shadow-sm border-0">' + errorMsg + '</div>');
            }
        });
    }

    function displayJobs(jobs) {
        if (jobs.length === 0) {
            $('#jobsContainer').html(
                '<div class="empty-state text-center p-5 card border-0 shadow-sm rounded-4">' +
                    '<i class="bi bi-briefcase text-muted mb-3" style="font-size: 3rem;"></i>' +
                    '<h4 class="text-dark fw-bold">' + t('recruiter.noJobs', 'No jobs') + '</h4>' +
                    '<button class="btn btn-primary mt-2" data-bs-toggle="modal" data-bs-target="#createJobModal"><i class="bi bi-plus-lg me-1"></i> ' + t('recruiter.createJob', 'Create New Job') + '</button>' +
                '</div>'
            );
            $('#myJobsCount').text('0');
            $('#activeJobsCount').text('0');
            return;
        }

        var activeJobs = jobs.filter(function(j) { return j.status === 'ACTIVE'; }).length;
        $('#myJobsCount').text(jobs.length);
        $('#activeJobsCount').text(activeJobs);

        var viewText = t('jobs.viewDetails', 'View Details');
        var appsText = t('recruiter.applications', 'Applications');
        var editText = t('recruiter.edit', 'Edit');
        var deleteText = t('common.delete', 'Delete');
        var closeText = t('recruiter.statusClosed', 'Close');

        var html = '';
        jobs.forEach(function (job) {
            html += '<div class="card mb-3">';
            html += '<div class="card-body">';
            html += '<div class="d-flex justify-content-between align-items-start mb-2">';
            html += '<div>';
            html += '<h5 class="card-title mb-1">' + escapeHtml(job.title) + '</h5>';
            html += '<h6 class="card-subtitle mb-2 text-muted">' + escapeHtml(job.company) + ' - ' + escapeHtml(job.location) + '</h6>';
            html += '</div>';
            html += '<div>';
            if (job.category) {
                html += '<span class="badge bg-secondary me-2">' + escapeHtml(job.category) + '</span>';
            }
            html += '<span class="badge bg-' + (job.status === 'ACTIVE' ? 'success' : 'secondary') + '">' + (job.status === 'ACTIVE' ? t('status.active', 'Active') : t('status.closed', 'Closed')) + '</span>';
            html += '</div>';
            html += '</div>';
            html += '<p class="card-text">' + escapeHtml(job.description.substring(0, 150)) + (job.description.length > 150 ? '...' : '') + '</p>';
            html += '<div class="mt-3">';
            html += '<a href="../job-details.jsp?id=' + job.id + '" class="btn btn-primary btn-sm">' + viewText + '</a>';
            html += '<button class="btn btn-info btn-sm ms-2" onclick="viewApplications(' + job.id + ')">' + appsText + '</button>';
            html += '<button class="btn btn-warning btn-sm ms-2" onclick="editJob(' + job.id + ')">' + editText + '</button>';
            html += '<button class="btn btn-danger btn-sm ms-2" onclick="deleteJob(' + job.id + ', \'' + escapeHtml(job.title) + '\')">' + deleteText + '</button>';
            if (job.status === 'ACTIVE') {
                html += '<button class="btn btn-secondary btn-sm ms-2" onclick="closeJob(' + job.id + ')">' + closeText + '</button>';
            }
            html += '</div>';
            html += '</div></div>';
        });

        $('#jobsContainer').html(html);
    }

    function escapeHtml(text) {
        var map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
        return String(text || '').replace(/[&<>"']/g, function (m) { return map[m]; });
    }

    window.editJob = function (jobId) {
        $.ajax({
            url: '../api/jobs/' + jobId,
            method: 'GET',
            success: function (response) {
                if (response.success && response.job) {
                    var job = response.job;
                    $('#editJobId').val(job.id);
                    $('#editTitle').val(job.title);
                    $('#editCompany').val(job.company);
                    $('#editLocation').val(job.location);
                    $('#editCategory').val(job.category || 'OTHER');
                    $('#editDescription').val(job.description);
                    $('#editStatus').val(job.status);
                    $('#editJobModal').modal('show');
                } else {
                    showAlert('danger', t('common.error', 'Error'));
                }
            },
            error: function () {
                showAlert('danger', t('common.loadError', 'Error loading'));
            }
        });
    };

    window.deleteJob = function (jobId, jobTitle) {
        if (!confirm(t('admin.deleteConfirm', 'Are you sure you want to delete') + ' "' + jobTitle + '"?')) {
            return;
        }
        $.ajax({
            url: '../api/jobs/' + jobId,
            method: 'DELETE',
            success: function (response) {
                if (response.success) {
                    showAlert('success', response.message);
                    loadMyJobs();
                } else {
                    showAlert('danger', response.message);
                }
            },
            error: function (xhr) {
                var errorMsg = t('common.error', 'Error');
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMsg = xhr.responseJSON.message;
                }
                showAlert('danger', errorMsg);
            }
        });
    };

    window.closeJob = function (jobId) {
        if (!confirm(t('common.yes', 'Yes') + '?')) {
            return;
        }
        $.ajax({
            url: '../api/jobs/' + jobId,
            method: 'PUT',
            data: { id: jobId, status: 'CLOSED' },
            success: function (response) {
                if (response.success) {
                    showAlert('success', response.message);
                    loadMyJobs();
                } else {
                    showAlert('danger', response.message);
                }
            },
            error: function (xhr) {
                var errorMsg = t('common.error', 'Error');
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMsg = xhr.responseJSON.message;
                }
                showAlert('danger', errorMsg);
            }
        });
    };

    function createJob() {
        var title = $('#title').val().trim();
        var company = $('#company').val().trim();
        var location = $('#location').val().trim();
        var category = $('#category').val() || 'OTHER';
        var description = $('#description').val().trim();

        if (!title || title.length < 3 || !company || !location || !description || description.length < 10) {
            showAlert('danger', t('register.formError', 'Please fix the errors'));
            return;
        }

        var $submitBtn = $('#createJobForm button[type="submit"]');
        var originalText = $submitBtn.html();
        $submitBtn.prop('disabled', true);
        $submitBtn.html('<span class="spinner-border spinner-border-sm me-2"></span>' + t('common.loading', 'Loading...'));

        $.ajax({
            url: '../api/jobs',
            method: 'POST',
            data: {
                title: title,
                company: company,
                location: location,
                category: category,
                description: description
            },
            success: function (response) {
                if (response.success) {
                    showAlert('success', response.message);
                    $('#createJobForm')[0].reset();
                    $('#createJobModal').modal('hide');
                    loadMyJobs();
                } else {
                    showAlert('danger', response.message);
                    $submitBtn.prop('disabled', false);
                    $submitBtn.html(originalText);
                }
            },
            error: function (xhr) {
                var errorMsg = t('common.error', 'Error');
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMsg = xhr.responseJSON.message;
                }
                showAlert('danger', errorMsg);
                $submitBtn.prop('disabled', false);
                $submitBtn.html(originalText);
            }
        });
    }

    function showAlert(type, message) {
        var alertHtml = '<div class="alert alert-' + type + ' alert-dismissible fade show" role="alert">' +
            message +
            '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>' +
            '</div>';
        var alertContainer = $('#createJobAlert').length > 0 ? $('#createJobAlert') : $('#jobsContainer');
        alertContainer.prepend(alertHtml);
        setTimeout(function () {
            $('.alert').fadeOut(function () { $(this).remove(); });
        }, 5000);
    }

    window.viewApplications = function (jobId) {
        window.location.href = 'job-applications.jsp?jobId=' + jobId;
    };

    // Edit job form handler
    $('#editJobForm').on('submit', function (e) {
        e.preventDefault();
        var jobId = $('#editJobId').val();
        var jobData = {
            title: $('#editTitle').val().trim(),
            company: $('#editCompany').val().trim(),
            location: $('#editLocation').val().trim(),
            category: $('#editCategory').val() || 'OTHER',
            description: $('#editDescription').val().trim(),
            status: $('#editStatus').val()
        };

        if (!jobData.title || jobData.title.length < 3 || !jobData.description || jobData.description.length < 10) {
            alert(t('register.formError', 'Please fix the errors'));
            return;
        }

        var $submitBtn = $('#editJobForm button[type="submit"]');
        var originalText = $submitBtn.html();
        $submitBtn.prop('disabled', true);
        $submitBtn.html('<span class="spinner-border spinner-border-sm me-2"></span>' + t('common.loading', 'Loading...'));

        $.ajax({
            url: '../api/jobs/' + jobId,
            method: 'PUT',
            data: jobData,
            success: function (response) {
                if (response.success) {
                    showAlert('success', response.message);
                    $('#editJobModal').modal('hide');
                    loadMyJobs();
                } else {
                    showAlert('danger', response.message);
                    $submitBtn.prop('disabled', false);
                    $submitBtn.html(originalText);
                }
            },
            error: function (xhr) {
                var errorMsg = t('common.error', 'Error');
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMsg = xhr.responseJSON.message;
                }
                showAlert('danger', errorMsg);
                $submitBtn.prop('disabled', false);
                $submitBtn.html(originalText);
            }
        });
    });

    $('#editJobModal').on('hidden.bs.modal', function () {
        $('#editJobForm')[0].reset();
        $('#editJobAlert').empty();
    });
});
