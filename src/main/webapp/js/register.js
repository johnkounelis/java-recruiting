$(document).ready(function() {
    function t(key, fallback) {
        if (typeof i18n !== 'undefined' && i18n.isReady()) {
            return i18n.t(key, fallback);
        }
        return fallback || key;
    }

    // Role radio sync to hidden field
    $('input[name="roleRadio"]').on('change', function() {
        $('#role').val($(this).val());
    });

    // Toggle password visibility
    $('#togglePassword').on('click', function() {
        var $pwd = $('#password');
        var $icon = $('#togglePasswordIcon');
        if ($pwd.attr('type') === 'password') {
            $pwd.attr('type', 'text');
            $icon.removeClass('bi-eye').addClass('bi-eye-slash');
        } else {
            $pwd.attr('type', 'password');
            $icon.removeClass('bi-eye-slash').addClass('bi-eye');
        }
    });

    // Email validation
    $('#email').on('blur', function() {
        var email = $(this).val();
        if (email && !isValidEmail(email)) {
            $(this).addClass('is-invalid');
            showFieldError($(this), t('register.emailError', 'Invalid email'));
        } else {
            $(this).removeClass('is-invalid');
            clearFieldError($(this));
        }
    });

    // Password confirmation check
    $('#confirmPassword').on('keyup', function() {
        checkPasswordMatch();
    });

    $('#password').on('keyup', function() {
        checkPasswordMatch();
        checkPasswordStrength();
    });

    function checkPasswordMatch() {
        var password = $('#password').val();
        var confirmPassword = $('#confirmPassword').val();

        if (confirmPassword.length > 0) {
            if (password === confirmPassword) {
                $('#confirmPassword').removeClass('is-invalid').addClass('is-valid');
                $('#passwordMatch').removeClass('text-danger').addClass('text-success')
                    .text('✓ ' + t('register.passwordMatch', 'Passwords match'));
            } else {
                $('#confirmPassword').removeClass('is-valid').addClass('is-invalid');
                $('#passwordMatch').removeClass('text-success').addClass('text-danger')
                    .text('✗ ' + t('register.passwordMismatch', 'Passwords do not match'));
            }
        }
    }

    function checkPasswordStrength() {
        var password = $('#password').val();
        var $container = $('#passwordStrengthContainer');
        var $bar = $('#passwordStrengthBar');
        var $text = $('#passwordStrengthText');

        if (password.length === 0) {
            $container.hide();
            return;
        }
        $container.show();

        var strength = 0;
        if (password.length >= 6) strength++;
        if (password.length >= 8) strength++;
        if (/[A-Z]/.test(password)) strength++;
        if (/[0-9]/.test(password)) strength++;
        if (/[^A-Za-z0-9]/.test(password)) strength++;

        if (strength <= 2) {
            $bar.css('width', '33%').removeClass('bg-warning bg-success').addClass('bg-danger');
            $text.text(t('register.strengthWeak', 'Weak')).removeClass('text-warning text-success').addClass('text-danger');
        } else if (strength <= 3) {
            $bar.css('width', '66%').removeClass('bg-danger bg-success').addClass('bg-warning');
            $text.text(t('register.strengthMedium', 'Medium')).removeClass('text-danger text-success').addClass('text-warning');
        } else {
            $bar.css('width', '100%').removeClass('bg-danger bg-warning').addClass('bg-success');
            $text.text(t('register.strengthStrong', 'Strong')).removeClass('text-danger text-warning').addClass('text-success');
        }

        if (password.length > 0 && password.length < 6) {
            $('#password').addClass('is-invalid');
        } else if (password.length >= 6) {
            $('#password').removeClass('is-invalid').addClass('is-valid');
        }
    }

    function isValidEmail(email) {
        var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    function showFieldError($field, message) {
        var container = $field.closest('.input-group').length ? $field.closest('.input-group') : $field;
        var errorDiv = container.siblings('.invalid-feedback');
        if (errorDiv.length === 0) {
            errorDiv = $('<div class="invalid-feedback d-block"></div>');
            container.after(errorDiv);
        }
        errorDiv.text(message);
    }

    function clearFieldError($field) {
        var container = $field.closest('.input-group').length ? $field.closest('.input-group') : $field;
        container.siblings('.invalid-feedback').remove();
    }

    $('#registerForm').on('submit', function(e) {
        e.preventDefault();

        var name = $('#name').val().trim();
        var email = $('#email').val().trim();
        var password = $('#password').val();
        var confirmPassword = $('#confirmPassword').val();
        var role = $('#role').val();

        // Clear previous errors
        $('.is-invalid').removeClass('is-invalid');
        $('#alertMessage').empty();

        var hasErrors = false;

        if (!name || name.length < 2) {
            $('#name').addClass('is-invalid');
            showFieldError($('#name'), t('register.nameError', 'Name must be at least 2 characters'));
            hasErrors = true;
        }

        if (!email || !isValidEmail(email)) {
            $('#email').addClass('is-invalid');
            showFieldError($('#email'), t('register.emailError', 'Invalid email'));
            hasErrors = true;
        }

        if (!password || password.length < 6) {
            $('#password').addClass('is-invalid');
            showFieldError($('#password'), t('register.passwordError', 'Password must be at least 6 characters'));
            hasErrors = true;
        }

        if (password !== confirmPassword) {
            $('#confirmPassword').addClass('is-invalid');
            showFieldError($('#confirmPassword'), t('register.passwordMismatch', 'Passwords do not match'));
            hasErrors = true;
        }

        if (hasErrors) {
            showAlert('danger', t('register.formError', 'Please fix the errors'));
            return;
        }

        // Show loading state
        $('#submitBtn').prop('disabled', true);
        $('#submitText').text(t('register.loading', 'Registering...'));
        $('#submitSpinner').removeClass('d-none');

        $.ajax({
            url: 'api/register',
            method: 'POST',
            data: {
                name: name,
                email: email,
                password: password,
                role: role
            },
            success: function(response) {
                if (response.success) {
                    showAlert('success', response.message);
                    setTimeout(function() {
                        window.location.href = 'login.jsp';
                    }, 2000);
                } else {
                    showAlert('danger', response.message);
                    resetSubmitButton();
                }
            },
            error: function(xhr) {
                var errorMsg = t('register.error', 'Error during registration');
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMsg = xhr.responseJSON.message;
                }
                showAlert('danger', errorMsg);
                resetSubmitButton();
            }
        });
    });

    function resetSubmitButton() {
        $('#submitBtn').prop('disabled', false);
        $('#submitText').text(t('register.createAccount', 'Create Account'));
        $('#submitSpinner').addClass('d-none');
    }

    function showAlert(type, message) {
        var alertHtml = '<div class="alert alert-' + type + ' alert-dismissible fade show" role="alert">' +
            message +
            '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>' +
            '</div>';
        $('#alertMessage').html(alertHtml);
    }
});
