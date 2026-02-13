$(document).ready(function() {
    checkUserSession();

    var urlParams = new URLSearchParams(window.location.search);
    var jobId = urlParams.get('id');

    function t(key, fallback) {
        if (typeof i18n !== 'undefined' && i18n.isReady()) {
            return i18n.t(key, fallback);
        }
        return fallback || key;
    }

    if (!jobId) {
        $('#jobDetailsContainer').html('<div class="alert alert-danger">Invalid Job ID</div>');
        return;
    }

    loadJobDetails(jobId);

    function checkUserSession() {
        $.ajax({
            url: 'api/session',
            method: 'GET',
            success: function(response) {
                if (response.loggedIn) {
                    $('#loginLink').addClass('d-none');
                    $('#logoutLink').removeClass('d-none');
                    $('#userInfo').text(t('nav.welcome', 'Welcome') + ', ' + response.user.name);
                    var dashUrl = 'candidate/dashboard.jsp';
                    if (response.user.role === 'ADMIN') dashUrl = 'admin/dashboard.jsp';
                    else if (response.user.role === 'RECRUITER') dashUrl = 'recruiter/dashboard.jsp';
                    $('#dashboardLink').attr('href', dashUrl);
                    $('#dashboardLinkContainer').show();
                }
            },
            error: function() {
                // Not logged in
            }
        });
    }

    function loadJobDetails(id) {
        $.ajax({
            url: 'api/jobs/' + id,
            method: 'GET',
            success: function(response) {
                if (response.success) {
                    displayJobDetails(response.job);
                } else {
                    $('#jobDetailsContainer').html('<div class="alert alert-danger">' + escapeHtml(response.message) + '</div>');
                }
            },
            error: function() {
                $('#jobDetailsContainer').html('<div class="alert alert-danger">' + t('common.loadError', 'Error loading') + '</div>');
            }
        });
    }

    function escapeHtml(text) {
        var map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
        return String(text || '').replace(/[&<>"']/g, function(m) { return map[m]; });
    }

    function displayJobDetails(job) {
        var descLabel = t('recruiter.description', 'Description');
        var applyBtnText = t('jobDetails.applyBtn', 'Submit Application');
        var backText = t('jobDetails.backToJobs', 'Back to Jobs');
        var applyTitle = t('jobDetails.apply', 'Submit Application');
        var resumeLabel = t('jobDetails.resume', 'Resume (PDF, DOC, DOCX)');
        var notesLabel = t('jobDetails.notes', 'Notes');
        var notesPlaceholder = t('jobDetails.notesPlaceholder', 'Optional notes...');
        var cancelText = t('common.cancel', 'Cancel');

        var html = '<div class="card">';
        html += '<div class="card-body">';
        html += '<h2 class="card-title">' + escapeHtml(job.title) + '</h2>';
        html += '<h5 class="card-subtitle mb-3 text-muted">' + escapeHtml(job.company) + ' - ' + escapeHtml(job.location) + '</h5>';
        if (job.category && job.category !== 'OTHER') {
            html += '<span class="badge bg-secondary mb-3">' + escapeHtml(job.category) + '</span>';
        }
        html += '<div class="mb-3"><strong>' + descLabel + ':</strong></div>';
        html += '<p class="card-text">' + escapeHtml(job.description).replace(/\n/g, '<br>') + '</p>';
        html += '<div class="mt-4">';
        html += '<button id="applyBtn" class="btn btn-success btn-lg">' + applyBtnText + '</button>';
        html += '<a href="jobs.jsp" class="btn btn-secondary btn-lg ms-2">' + backText + '</a>';
        html += '</div>';
        html += '</div></div>';

        html += '<div id="applyForm" class="card mt-4" style="display:none;">';
        html += '<div class="card-body">';
        html += '<h4>' + applyTitle + '</h4>';
        html += '<div id="applyAlert"></div>';
        html += '<form id="applicationForm" enctype="multipart/form-data">';
        html += '<input type="hidden" id="jobId" value="' + escapeHtml(String(job.id)) + '">';
        html += '<div class="mb-3">';
        html += '<label for="resume" class="form-label"><i class="bi bi-file-earmark-pdf me-1"></i>' + resumeLabel + ' (max 5MB)</label>';
        html += '<input type="file" class="form-control" id="resume" name="resume" accept=".pdf,.doc,.docx">';
        html += '</div>';
        html += '<div class="mb-3">';
        html += '<label for="notes" class="form-label">' + notesLabel + '</label>';
        html += '<textarea class="form-control" id="notes" name="notes" rows="4" placeholder="' + escapeHtml(notesPlaceholder) + '"></textarea>';
        html += '</div>';
        html += '<button type="submit" class="btn btn-primary">' + applyBtnText + '</button>';
        html += '<button type="button" class="btn btn-secondary ms-2" id="cancelBtn">' + cancelText + '</button>';
        html += '</form>';
        html += '</div></div>';

        $('#jobDetailsContainer').html(html);

        $('#applyBtn').on('click', function() {
            $('#applyForm').slideDown();
            $(this).hide();
        });

        $('#cancelBtn').on('click', function() {
            $('#applyForm').slideUp();
            $('#applyBtn').show();
        });

        $('#applicationForm').on('submit', function(e) {
            e.preventDefault();
            var jid = $('#jobId').val();
            if (!jid) {
                showAlert('warning', t('jobDetails.loginToApply', 'Login to apply'));
                setTimeout(function() {
                    window.location.href = 'login.jsp';
                }, 2000);
                return;
            }
            submitApplication(job.id);
        });
    }

    function submitApplication(jobId) {
        var notes = $('#notes').val();
        var resumeFile = $('#resume')[0].files[0];

        var $submitBtn = $('#applicationForm button[type="submit"]');
        var originalText = $submitBtn.html();
        $submitBtn.prop('disabled', true);
        $submitBtn.html('<span class="spinner-border spinner-border-sm me-2"></span>' + t('common.loading', 'Loading...'));

        var formData = new FormData();
        formData.append('jobId', jobId);
        formData.append('notes', notes || '');
        if (resumeFile) {
            formData.append('resume', resumeFile);
        }

        $.ajax({
            url: 'api/applications',
            method: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                if (response.success) {
                    showAlert('success', response.message);
                    $('#applicationForm').hide();
                    setTimeout(function() {
                        window.location.href = 'jobs.jsp';
                    }, 2000);
                } else {
                    showAlert('danger', response.message);
                    $submitBtn.prop('disabled', false);
                    $submitBtn.html(originalText);
                }
            },
            error: function(xhr) {
                var errorMsg = t('common.error', 'Error');
                if (xhr.status === 401 || xhr.status === 403) {
                    errorMsg = t('jobDetails.loginToApply', 'Login to apply');
                    setTimeout(function() {
                        window.location.href = 'login.jsp';
                    }, 2000);
                } else if (xhr.responseJSON && xhr.responseJSON.message) {
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
        $('#applyAlert').html(alertHtml);
    }
});
