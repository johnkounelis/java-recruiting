-- Δημιουργία Database
CREATE DATABASE IF NOT EXISTS recruiting_db;
USE recruiting_db;

-- Πίνακας Users (Χρήστες)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    role ENUM('CANDIDATE', 'RECRUITER', 'ADMIN') DEFAULT 'CANDIDATE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email)
);

-- Πίνακας Jobs (Θέσεις Εργασίας)
CREATE TABLE IF NOT EXISTS jobs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    company VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    category VARCHAR(100) DEFAULT 'OTHER',
    posted_by INT NOT NULL,
    posted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ACTIVE', 'CLOSED') DEFAULT 'ACTIVE',
    FOREIGN KEY (posted_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_posted_by (posted_by),
    INDEX idx_status (status),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Πίνακας Applications (Αιτήσεις)
CREATE TABLE IF NOT EXISTS applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    job_id INT NOT NULL,
    candidate_id INT NOT NULL,
    status ENUM('PENDING', 'REVIEWED', 'ACCEPTED', 'REJECTED') DEFAULT 'PENDING',
    applied_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    resume_filename VARCHAR(255) DEFAULT NULL,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (candidate_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_application (job_id, candidate_id),
    INDEX idx_job_id (job_id),
    INDEX idx_candidate_id (candidate_id),
    INDEX idx_status (status)
);

-- Πίνακας Companies (Εταιρείες)
CREATE TABLE IF NOT EXISTS companies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE,
    description TEXT,
    website VARCHAR(500),
    location VARCHAR(200),
    industry VARCHAR(100),
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_created_by (created_by)
);

-- Πίνακας Password Reset Tokens
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token)
);

-- Εισαγωγή δοκιμαστικών δεδομένων (optional)
-- Χρησιμοποιούμε IGNORE για να μην δώσει error αν τρέξει 2 φορές
INSERT IGNORE INTO users (email, password, name, role) VALUES
('admin@recruiting.com', 'admin123', 'Admin User', 'ADMIN'),
('recruiter@recruiting.com', 'recruiter123', 'Recruiter User', 'RECRUITER'),
('candidate@recruiting.com', 'candidate123', 'Candidate User', 'CANDIDATE');

-- Εισαγωγή δοκιμαστικών θέσεων εργασίας (μετά τη δημιουργία των users)
-- Σημείωση: Οι users πρέπει να υπάρχουν πρώτα (id: 1=admin, 2=recruiter, 3=candidate)
INSERT IGNORE INTO jobs (id, title, description, company, location, posted_by, status) VALUES
(1, 'Java Developer', 'Ψάχνουμε έμπειρο Java Developer για να συνεργαστεί με την ομάδα μας. Απαιτήσεις: Java 8+, Spring Framework, SQL.', 'Tech Solutions Ltd', 'Αθήνα', 2, 'ACTIVE'),
(2, 'Frontend Developer', 'Θέση Frontend Developer με React.js. Εμπειρία με modern JavaScript frameworks. Remote work possible.', 'Digital Innovations', 'Θεσσαλονίκη', 2, 'ACTIVE'),
(3, 'DevOps Engineer', 'DevOps Engineer για cloud infrastructure. Kubernetes, Docker, CI/CD experience required.', 'Cloud Services Co', 'Πάτρα', 2, 'ACTIVE'),
(4, 'Project Manager', 'Experienced Project Manager για software projects. PMP certification preferred.', 'Management Pro', 'Αθήνα', 2, 'ACTIVE');

-- Εισαγωγή δοκιμαστικών αιτήσεων
-- Σημείωση: candidate_id = 3 (candidate user)
INSERT IGNORE INTO applications (job_id, candidate_id, status, notes) VALUES
(1, 3, 'PENDING', 'Ενδιαφέρομαι για τη θέση Java Developer. Έχω 3 χρόνια εμπειρίας.'),
(2, 3, 'REVIEWED', 'Application under review by recruiter.'),
(3, 3, 'PENDING', 'Interested in DevOps position.');