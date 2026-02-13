$(document).ready(function() {
    // Toggle password visibility
    $('#togglePassword').on('click', function() {
        const $pwd = $('#password');
        const $icon = $('#togglePasswordIcon');
        if ($pwd.attr('type') === 'password') {
            $pwd.attr('type', 'text');
            $icon.removeClass('bi-eye').addClass('bi-eye-slash');
        } else {
            $pwd.attr('type', 'password');
            $icon.removeClass('bi-eye-slash').addClass('bi-eye');
        }
    });

    // Demo account auto-fill
    $('.demo-email').on('click', function() {
        var email = $(this).data('email');
        var password = $(this).data('password');
        $('#email').val(email);
        $('#password').val(password);
        // Flash the form
        $('#loginForm').addClass('border-primary');
        setTimeout(function() { $('#loginForm').removeClass('border-primary'); }, 500);
    });

    // Email validation
    $('#email').on('blur', function() {
        var email = $(this).val();
        if (email && !isValidEmail(email)) {
            $(this).closest('.input-group').find('.form-control').addClass('is-invalid');
            showFieldError($(this), t('login.invalidEmail'));
        } else {
            $(this).closest('.input-group').find('.form-control').removeClass('is-invalid');
            clearFieldError($(this));
        }
    });

    function t(key, fallback) {
        if (typeof i18n !== 'undefined' && i18n.isReady()) {
            return i18n.t(key, fallback);
        }
        return fallback || key;
    }

    function isValidEmail(email) {
        var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    function showFieldError($field, message) {
        var errorDiv = $field.closest('.input-group').siblings('.invalid-feedback');
        if (errorDiv.length === 0) {
            errorDiv = $('<div class="invalid-feedback d-block"></div>');
            $field.closest('.input-group').after(errorDiv);
        }
        errorDiv.text(message);
    }

    function clearFieldError($field) {
        $field.closest('.input-group').siblings('.invalid-feedback').remove();
    }

    $('#loginForm').on('submit', function(e) {
        e.preventDefault();

        var email = $('#email').val().trim();
        var password = $('#password').val();

        // Clear previous errors
        $('.is-invalid').removeClass('is-invalid');
        $('#alertMessage').empty();

        var hasErrors = false;

        if (!email || !isValidEmail(email)) {
            $('#email').addClass('is-invalid');
            showFieldError($('#email'), t('login.invalidEmail', 'Invalid email'));
            hasErrors = true;
        }

        if (!password || password.length === 0) {
            $('#password').addClass('is-invalid');
            showFieldError($('#password'), t('login.passwordRequired', 'Password is required'));
            hasErrors = true;
        }

        if (hasErrors) {
            showAlert('danger', t('login.fixErrors', 'Please fix the errors'));
            return;
        }

        // Show loading state
        var $submitBtn = $('#loginForm button[type="submit"]');
        $submitBtn.prop('disabled', true);
        var originalText = $submitBtn.html();
        $submitBtn.html('<span class="spinner-border spinner-border-sm me-2"></span>' + t('login.loading', 'Logging in...'));

        $.ajax({
            url: 'api/login',
            method: 'POST',
            data: {
                email: email,
                password: password
            },
            success: function(response) {
                if (response.success) {
                    showAlert('success', response.message);
                    setTimeout(function() {
                        window.location.href = response.redirect;
                    }, 1000);
                } else {
                    showAlert('danger', response.message);
                    $submitBtn.prop('disabled', false);
                    $submitBtn.html(originalText);
                }
            },
            error: function(xhr) {
                var errorMsg = t('login.error', 'Error during login');
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMsg = xhr.responseJSON.message;
                }
                showAlert('danger', errorMsg);
                $submitBtn.prop('disabled', false);
                $submitBtn.html(originalText);
            }
        });
    });

    function showAlert(type, message) {
        var alertHtml = '<div class="alert alert-' + type + ' alert-dismissible fade show" role="alert">' +
            message +
            '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>' +
            '</div>';
        $('#alertMessage').html(alertHtml);
    }
});
