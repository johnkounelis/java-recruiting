$(document).ready(function () {
    let currentPage = 1;
    let currentSearchTerm = '';
    let currentCategoryTerm = '';

    checkUserSession();
    loadJobs();

    let currentUserRole = null;

    function t(key, fallback) {
        if (typeof i18n !== 'undefined' && i18n.isReady()) {
            return i18n.t(key, fallback);
        }
        return fallback || key;
    }

    function checkUserSession() {
        $.ajax({
            url: 'api/session',
            method: 'GET',
            success: function (response) {
                if (response.loggedIn) {
                    $('#loginLink').addClass('d-none');
                    $('#logoutLink').removeClass('d-none');
                    $('#userInfo').text(t('nav.welcome', 'Welcome') + ', ' + response.user.name);
                    currentUserRole = response.user.role;
                    var dashUrl = 'candidate/dashboard.jsp';
                    if (response.user.role === 'ADMIN') dashUrl = 'admin/dashboard.jsp';
                    else if (response.user.role === 'RECRUITER') dashUrl = 'recruiter/dashboard.jsp';
                    $('#dashboardLink').attr('href', dashUrl);
                    $('#dashboardLinkContainer').show();
                }
            },
            error: function () {
                // Not logged in
            }
        });
    }

    function loadJobs(searchTerm, categoryTerm, page) {
        page = page || 1;
        let url = 'api/jobs?page=' + page;
        if (searchTerm && searchTerm.trim() !== '') {
            url += '&search=' + encodeURIComponent(searchTerm.trim());
        }

        $.ajax({
            url: url,
            method: 'GET',
            success: function (response) {
                if (response.success) {
                    let finalJobs = response.jobs;
                    if (categoryTerm && categoryTerm.trim() !== '') {
                        finalJobs = finalJobs.filter(function(j) { return j.category === categoryTerm; });
                    }
                    displayJobs(finalJobs);

                    if (response.totalPages) {
                        renderPagination(response.currentPage, response.totalPages);
                    } else {
                        $('#paginationContainer').empty();
                    }

                    if (response.searchTerm) {
                        $('#searchInput').val(response.searchTerm);
                    }
                } else {
                    displayEmptyState(
                        t('jobs.errorLoading', 'Error Loading'),
                        t('jobs.errorLoadingMsg', 'Failed to retrieve data.'),
                        'bi-exclamation-circle text-danger'
                    );
                    $('#paginationContainer').empty();
                }
            },
            error: function (xhr) {
                var errorMsg = t('jobs.errorTempMsg', 'Temporary error loading jobs.');
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMsg = xhr.responseJSON.message;
                }
                displayEmptyState(t('jobs.error', 'Error'), errorMsg, 'bi-x-circle text-danger');
            }
        });
    }

    function displayEmptyState(title, message, iconClass) {
        iconClass = iconClass || 'bi-search';
        $('#jobsContainer').html(
            '<div class="col-12">' +
                '<div class="empty-state card border-0 shadow-md bg-white p-5 rounded-4 text-center">' +
                    '<i class="bi ' + iconClass + ' mb-3" style="font-size: 3rem; color: #cbd5e1;"></i>' +
                    '<h4 class="fw-bold text-dark">' + escapeHtml(title) + '</h4>' +
                    '<p class="text-muted fs-5">' + escapeHtml(message) + '</p>' +
                '</div>' +
            '</div>'
        );
    }

    function renderPagination(currentPage, totalPages) {
        var paginationHtml = '';

        if (totalPages <= 1) {
            $('#paginationContainer').html(paginationHtml);
            return;
        }

        paginationHtml += '<li class="page-item ' + (currentPage === 1 ? 'disabled' : '') + '">' +
            '<a class="page-link" href="#" data-page="' + (currentPage - 1) + '" aria-label="Previous">' +
            '<span aria-hidden="true">&laquo;</span></a></li>';

        for (var i = 1; i <= totalPages; i++) {
            paginationHtml += '<li class="page-item ' + (currentPage === i ? 'active' : '') + '">' +
                '<a class="page-link" href="#" data-page="' + i + '">' + i + '</a></li>';
        }

        paginationHtml += '<li class="page-item ' + (currentPage === totalPages ? 'disabled' : '') + '">' +
            '<a class="page-link" href="#" data-page="' + (currentPage + 1) + '" aria-label="Next">' +
            '<span aria-hidden="true">&raquo;</span></a></li>';

        $('#paginationContainer').html(paginationHtml);
    }

    function displayJobs(jobs) {
        if (!jobs || jobs.length === 0) {
            displayEmptyState(
                t('jobs.noJobs', 'No Jobs Found'),
                t('jobs.noJobsHint', 'Try changing your search criteria.'),
                'bi-inbox'
            );
            return;
        }

        var html = '';
        var locale = (typeof i18n !== 'undefined' && i18n.currentLang === 'en') ? 'en-US' : 'el-GR';

        jobs.forEach(function (job) {
            var date = job.postedDate ? new Date(job.postedDate).toLocaleDateString(locale) : t('jobs.recent', 'Recent');
            var viewDetailsText = t('jobs.viewDetails', 'View Details');
            var positionText = t('jobs.position', 'Position');

            html += '<div class="col-md-6 col-lg-4">' +
                '<div class="card h-100 shadow-sm border border-light rounded-4 feature-card transition-all">' +
                    '<div class="card-body p-4 d-flex flex-column">' +
                        '<div class="d-flex justify-content-between align-items-start mb-3">' +
                            '<div>' +
                                '<span class="badge bg-primary bg-opacity-10 text-primary px-3 py-2 rounded-pill fw-semibold"><i class="bi bi-briefcase me-1"></i>' + positionText + '</span>' +
                                (job.category && job.category !== 'OTHER' ? '<span class="badge border border-secondary text-secondary ms-2 px-2 py-2 rounded-pill fw-semibold"><i class="bi bi-tag me-1"></i>' + escapeHtml(job.category) + '</span>' : '') +
                            '</div>' +
                            '<small class="text-muted fw-semibold">' + date + '</small>' +
                        '</div>' +
                        '<h4 class="card-title fw-bold mb-1 text-dark">' + escapeHtml(job.title) + '</h4>' +
                        '<h6 class="card-subtitle mb-3 text-primary fw-semibold"><i class="bi bi-building me-2"></i>' + escapeHtml(job.company) + ' <span class="text-muted fw-normal ms-1">(&nbsp;<i class="bi bi-geo-alt me-1"></i>' + escapeHtml(job.location) + '&nbsp;)</span></h6>' +
                        '<p class="card-text text-muted flex-grow-1" style="display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden;">' +
                            escapeHtml(job.description) +
                        '</p>' +
                        '<div class="mt-4 pt-3 border-top border-light d-grid">' +
                            '<a href="job-details.jsp?id=' + job.id + '" class="btn btn-outline-primary fw-bold stretched-link">' + viewDetailsText + '</a>' +
                        '</div>' +
                    '</div>' +
                '</div>' +
            '</div>';
        });

        $('#jobsContainer').html(html);
    }

    function escapeHtml(text) {
        var map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
        return String(text || '').replace(/[&<>"']/g, function (m) { return map[m]; });
    }

    // Search functionality
    $('#searchBtn').on('click', function () {
        currentSearchTerm = $('#searchInput').val();
        currentCategoryTerm = $('#categoryFilter').val();
        currentPage = 1;
        loadJobs(currentSearchTerm, currentCategoryTerm, currentPage);
    });

    $('#categoryFilter').on('change', function () {
        $('#searchBtn').click();
    });

    // Pagination click handler
    $('#paginationContainer').on('click', '.page-link', function (e) {
        e.preventDefault();
        var parent = $(this).parent();
        if (parent.hasClass('disabled') || parent.hasClass('active')) {
            return;
        }
        currentPage = parseInt($(this).data('page'));
        loadJobs(currentSearchTerm, currentCategoryTerm, currentPage);
        $('html, body').animate({
            scrollTop: $(".container").offset().top - 100
        }, 500);
    });

    $('#searchInput').on('keypress', function (e) {
        if (e.which === 13) {
            $('#searchBtn').click();
        }
    });

    $('#clearSearchBtn').on('click', function () {
        $('#searchInput').val('');
        $('#categoryFilter').val('');
        currentSearchTerm = '';
        currentCategoryTerm = '';
        currentPage = 1;
        loadJobs(currentSearchTerm, currentCategoryTerm, currentPage);
    });
});
