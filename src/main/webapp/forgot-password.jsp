<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="el">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Επαναφορά Κωδικού - Recruiting Platform</title>
    <meta name="description" content="Επαναφορά κωδικού πρόσβασης">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap"
        rel="stylesheet">
    <link rel="stylesheet" href="css/style.css">
</head>

<body class="d-flex align-items-center min-vh-100"
    style="background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #334155 100%);">

    <!-- Language Toggle (floating) -->
    <div class="position-fixed top-0 end-0 p-3" style="z-index: 1000;">
        <div class="btn-group btn-group-sm" role="group">
            <button type="button" class="btn btn-outline-light lang-btn-el active">EL</button>
            <button type="button" class="btn btn-outline-light lang-btn-en">EN</button>
        </div>
    </div>

    <div class="main-content container">
        <div class="row justify-content-center">
            <div class="col-md-5">
                <div class="text-center mb-4">
                    <h2 class="text-white fw-bold"><i class="bi bi-key me-2"></i><span data-i18n="forgot.title">Επαναφορά Κωδικού</span></h2>
                    <p class="text-white-50" data-i18n="forgot.subtitle">Εισάγετε το email σας για να λάβετε οδηγίες</p>
                </div>

                <!-- Step 1: Request Token -->
                <div id="requestForm" class="card border-0 shadow-lg rounded-4 p-4"
                    style="background: rgba(255,255,255,0.95); backdrop-filter: blur(10px);">
                    <div class="card-body">
                        <div id="requestAlert" class="alert d-none"></div>
                        <form id="resetRequestForm">
                            <div class="mb-3">
                                <label for="email" class="form-label fw-semibold"><i
                                        class="bi bi-envelope me-1"></i><span data-i18n="forgot.email">Email</span></label>
                                <input type="email" class="form-control form-control-lg rounded-3" id="email"
                                    placeholder="you@example.com" required>
                            </div>
                            <button type="submit" class="btn btn-primary btn-lg w-100 rounded-3 fw-semibold">
                                <i class="bi bi-send me-2"></i><span data-i18n="forgot.send">Αποστολή</span>
                            </button>
                        </form>
                    </div>
                </div>

                <!-- Step 2: Reset Password -->
                <div id="resetForm" class="card border-0 shadow-lg rounded-4 p-4 d-none"
                    style="background: rgba(255,255,255,0.95); backdrop-filter: blur(10px);">
                    <div class="card-body">
                        <div id="resetAlert" class="alert d-none"></div>
                        <div class="alert alert-info">
                            <i class="bi bi-info-circle me-2"></i>
                            <strong data-i18n="forgot.demoMode">Demo Mode:</strong> <span data-i18n="forgot.demoModeText">Το token εμφανίζεται εδώ αντί να σταλεί μέσω email.</span>
                        </div>
                        <form id="resetPasswordForm">
                            <div class="mb-3">
                                <label for="token" class="form-label fw-semibold"><i
                                        class="bi bi-key me-1"></i><span data-i18n="forgot.token">Token</span></label>
                                <input type="text" class="form-control form-control-lg rounded-3" id="token"
                                    readonly>
                            </div>
                            <div class="mb-3">
                                <label for="newPassword" class="form-label fw-semibold"><i
                                        class="bi bi-lock me-1"></i><span data-i18n="forgot.newPassword">Νέος Κωδικός</span></label>
                                <input type="password" class="form-control form-control-lg rounded-3"
                                    id="newPassword" data-i18n-placeholder="forgot.newPasswordPlaceholder" placeholder="Τουλάχιστον 6 χαρακτήρες" required minlength="6">
                            </div>
                            <div class="mb-3">
                                <label for="confirmPassword" class="form-label fw-semibold"><i
                                        class="bi bi-lock-fill me-1"></i><span data-i18n="forgot.confirmPassword">Επιβεβαίωση Κωδικού</span></label>
                                <input type="password" class="form-control form-control-lg rounded-3"
                                    id="confirmPassword" data-i18n-placeholder="forgot.confirmPasswordPlaceholder" placeholder="Επαναλάβετε τον κωδικό" required
                                    minlength="6">
                            </div>
                            <button type="submit" class="btn btn-success btn-lg w-100 rounded-3 fw-semibold">
                                <i class="bi bi-check-circle me-2"></i><span data-i18n="forgot.changePassword">Αλλαγή Κωδικού</span>
                            </button>
                        </form>
                    </div>
                </div>

                <div class="text-center mt-3">
                    <a href="login.jsp" class="text-white-50 text-decoration-none"><i
                            class="bi bi-arrow-left me-1"></i><span data-i18n="forgot.backToLogin">Επιστροφή στη Σύνδεση</span></a>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="mt-5 py-4 text-center" style="background: rgba(17, 24, 39, 0.6); backdrop-filter: blur(10px); border-top: 1px solid rgba(255,255,255,0.1);">
        <div class="container">
            <p class="mb-0 text-white-50 small">&copy; 2026 <span data-i18n="common.footer">Recruiting App. Με επιφύλαξη παντός δικαιώματος.</span></p>
        </div>
    </footer>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="js/common.js"></script>
    <script src="js/i18n.js"></script>
    <script>
        $(document).ready(function () {
            $('#resetRequestForm').on('submit', function (e) {
                e.preventDefault();
                const email = $('#email').val();

                $.ajax({
                    url: 'api/password-reset/request',
                    method: 'POST',
                    data: { email: email },
                    success: function (response) {
                        if (response.success) {
                            $('#requestAlert').removeClass('d-none alert-danger').addClass('alert-success')
                                .html('<i class="bi bi-check-circle me-2"></i>' + response.message);
                            if (response.resetToken) {
                                $('#token').val(response.resetToken);
                                $('#resetForm').removeClass('d-none');
                            }
                        } else {
                            $('#requestAlert').removeClass('d-none alert-success').addClass('alert-danger')
                                .html('<i class="bi bi-exclamation-circle me-2"></i>' + response.message);
                        }
                    },
                    error: function () {
                        var msg = (typeof i18n !== 'undefined') ? i18n.t('forgot.connectionError', 'Connection error') : 'Σφάλμα σύνδεσης';
                        $('#requestAlert').removeClass('d-none alert-success').addClass('alert-danger')
                            .html('<i class="bi bi-exclamation-circle me-2"></i>' + msg);
                    }
                });
            });

            $('#resetPasswordForm').on('submit', function (e) {
                e.preventDefault();
                const token = $('#token').val();
                const newPassword = $('#newPassword').val();
                const confirmPassword = $('#confirmPassword').val();

                if (newPassword !== confirmPassword) {
                    var msg = (typeof i18n !== 'undefined') ? i18n.t('forgot.passwordMismatch', 'Passwords do not match') : 'Οι κωδικοί δεν ταιριάζουν';
                    $('#resetAlert').removeClass('d-none alert-success').addClass('alert-danger')
                        .html('<i class="bi bi-exclamation-circle me-2"></i>' + msg);
                    return;
                }

                $.ajax({
                    url: 'api/password-reset/reset',
                    method: 'POST',
                    data: { token: token, newPassword: newPassword },
                    success: function (response) {
                        if (response.success) {
                            var loginText = (typeof i18n !== 'undefined') ? i18n.t('forgot.loginLink', 'Login') : 'Σύνδεση';
                            $('#resetAlert').removeClass('d-none alert-danger').addClass('alert-success')
                                .html('<i class="bi bi-check-circle me-2"></i>' + response.message + ' <a href="login.jsp" class="alert-link">' + loginText + '</a>');
                            $('#resetPasswordForm')[0].reset();
                        } else {
                            $('#resetAlert').removeClass('d-none alert-success').addClass('alert-danger')
                                .html('<i class="bi bi-exclamation-circle me-2"></i>' + response.message);
                        }
                    },
                    error: function () {
                        var msg = (typeof i18n !== 'undefined') ? i18n.t('forgot.connectionError', 'Connection error') : 'Σφάλμα σύνδεσης';
                        $('#resetAlert').removeClass('d-none alert-success').addClass('alert-danger')
                            .html('<i class="bi bi-exclamation-circle me-2"></i>' + msg);
                    }
                });
            });
        });
    </script>
</body>

</html>
