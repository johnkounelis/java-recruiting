// Common utility functions for the application

// Escape HTML to prevent XSS
function escapeHtml(text) {
    var map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
    return String(text || '').replace(/[&<>"']/g, function(m) { return map[m]; });
}

// Format date for display (language-aware)
function formatDate(dateString) {
    if (!dateString) return '-';
    var date = new Date(dateString);
    var locale = (typeof i18n !== 'undefined' && i18n.currentLang === 'en') ? 'en-US' : 'el-GR';
    return date.toLocaleDateString(locale, {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

// Status badge color mapping
function getStatusColor(status) {
    switch (status) {
        case 'ACCEPTED': return 'success';
        case 'REJECTED': return 'danger';
        case 'REVIEWED': return 'warning';
        default: return 'secondary';
    }
}

// Status display text (language-aware)
function getStatusText(status) {
    if (typeof i18n !== 'undefined' && i18n.isReady()) {
        var key = 'status.' + status.toLowerCase();
        var translated = i18n.t(key, null);
        if (translated && translated !== key) return translated;
    }
    // Fallback to Greek
    var statusMap = {
        'PENDING': 'Εκκρεμεί',
        'REVIEWED': 'Εξετάζεται',
        'ACCEPTED': 'Αποδεκτή',
        'REJECTED': 'Απορρίφθηκε'
    };
    return statusMap[status] || status;
}

// Show toast notification
function showToast(message, type) {
    type = type || 'info';
    var toastHtml =
        '<div class="toast align-items-center text-white bg-' + type + ' border-0" role="alert" aria-live="assertive" aria-atomic="true">' +
            '<div class="d-flex">' +
                '<div class="toast-body">' + escapeHtml(message) + '</div>' +
                '<button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>' +
            '</div>' +
        '</div>';

    var toastContainer = $('#toastContainer');
    if (toastContainer.length === 0) {
        $('body').append('<div id="toastContainer" class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 9999;"></div>');
        toastContainer = $('#toastContainer');
    }

    toastContainer.append(toastHtml);
    var toast = new bootstrap.Toast(toastContainer.find('.toast').last()[0]);
    toast.show();
}

// Get CSRF token from meta tag
function getCsrfToken() {
    var meta = document.querySelector('meta[name="csrf-token"]');
    return meta ? meta.getAttribute('content') : '';
}

// Setup CSRF token for all AJAX requests
$(document).ready(function() {
    $.ajaxSetup({
        beforeSend: function(xhr, settings) {
            if (settings.type !== 'GET') {
                var token = getCsrfToken();
                if (token) {
                    xhr.setRequestHeader('X-CSRF-Token', token);
                }
            }
        }
    });
});

// Handle AJAX errors globally - only redirect on 401 (not logged in)
$(document).ajaxError(function(event, xhr) {
    if (xhr.status === 401) {
        var msg = (typeof i18n !== 'undefined') ? i18n.t('common.sessionExpired', 'Session expired. Please login again.') : 'Session expired.';
        showToast(msg, 'warning');
        setTimeout(function() {
            var basePath = window.location.pathname.includes('/admin/') ||
                window.location.pathname.includes('/recruiter/') ||
                window.location.pathname.includes('/candidate/') ? '../' : '';
            window.location.href = basePath + 'login.jsp?expired=true';
        }, 2000);
    }
});

// Periodic session check - polls server every 5 minutes to detect expiration
(function() {
    var SESSION_CHECK_INTERVAL = 5 * 60 * 1000; // 5 minutes

    function checkSession() {
        $.ajax({
            url: (window.contextPath || '') + '/api/session',
            method: 'GET',
            dataType: 'json',
            success: function(data) {
                if (!data.valid) {
                    var msg = (typeof i18n !== 'undefined') ? i18n.t('common.sessionExpired', 'Session expired. Redirecting to login...') : 'Session expired.';
                    showToast(msg, 'warning');
                    setTimeout(function() {
                        window.location.href = (window.contextPath || '') + '/login.jsp?expired=true';
                    }, 1500);
                }
            }
        });
    }

    // Only run session checks on protected pages
    if (window.location.pathname.match(/\/(admin|recruiter|candidate)\//)) {
        setInterval(checkSession, SESSION_CHECK_INTERVAL);
    }
})();
