$(document).ready(function () {
    loadProfile();

    function loadProfile() {
        $.ajax({
            url: '../api/profile',
            method: 'GET',
            success: function (response) {
                if (response.success && response.profile) {
                    $('#name').val(response.profile.name);
                    $('#email').val(response.profile.email);
                    const roleMap = {
                        'CANDIDATE': 'Υποψήφιος',
                        'RECRUITER': 'Recruiter',
                        'ADMIN': 'Διαχειριστής'
                    };
                    $('#role').val(roleMap[response.profile.role] || response.profile.role);
                }
            },
            error: function () {
                showAlert('danger', 'Σφάλμα κατά τη φόρτωση του προφίλ');
            }
        });
    }

    $('#profileForm').on('submit', function (e) {
        e.preventDefault();

        const name = $('#name').val().trim();
        const email = $('#email').val().trim();
        const currentPassword = $('#currentPassword').val();
        const newPassword = $('#newPassword').val();
        const confirmPassword = $('#confirmPassword').val();

        // Validation
        if (name.length < 2) {
            showAlert('danger', 'Το όνομα πρέπει να έχει τουλάχιστον 2 χαρακτήρες');
            return;
        }

        if (!email) {
            showAlert('danger', 'Το email είναι υποχρεωτικό');
            return;
        }

        // Password validation
        if (newPassword) {
            if (!currentPassword) {
                showAlert('danger', 'Εισάγετε τον τρέχοντα κωδικό σας');
                return;
            }
            if (newPassword.length < 6) {
                showAlert('danger', 'Ο νέος κωδικός πρέπει να έχει τουλάχιστον 6 χαρακτήρες');
                return;
            }
            if (newPassword !== confirmPassword) {
                showAlert('danger', 'Οι κωδικοί δεν ταιριάζουν');
                return;
            }
        }

        const $btn = $('#saveBtn');
        $btn.prop('disabled', true).html('<span class="spinner-border spinner-border-sm me-2"></span>Αποθήκευση...');

        const data = { name: name, email: email };
        if (currentPassword) data.currentPassword = currentPassword;
        if (newPassword) data.newPassword = newPassword;

        $.ajax({
            url: '../api/profile',
            method: 'POST',
            data: data,
            success: function (response) {
                if (response.success) {
                    showAlert('success', response.message);
                    // Clear password fields
                    $('#currentPassword').val('');
                    $('#newPassword').val('');
                    $('#confirmPassword').val('');
                } else {
                    showAlert('danger', response.message);
                }
                $btn.prop('disabled', false).html('Αποθήκευση');
            },
            error: function () {
                showAlert('danger', 'Σφάλμα κατά την αποθήκευση');
                $btn.prop('disabled', false).html('Αποθήκευση');
            }
        });
    });

    function showAlert(type, message) {
        const alertHtml = '<div class="alert alert-' + type + ' alert-dismissible fade show" role="alert">' +
            message +
            '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>' +
            '</div>';
        $('#alertMessage').html(alertHtml);
    }
});
