package com.recruiting.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.*;

@WebListener
public class DemoDataInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[DemoData] Checking if demo data needs to be seeded...");
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();

            // Check if jobs table already has data
            int jobCount = 0;
            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM jobs")) {
                if (rs.next()) {
                    jobCount = rs.getInt(1);
                }
            }

            if (jobCount >= 5) {
                System.out.println("[DemoData] Database already has " + jobCount + " jobs. Skipping seed.");
                return;
            }

            System.out.println("[DemoData] Seeding demo data...");

            // Ensure demo users exist
            ensureUser(con, "admin@recruiting.com", "admin123", "Admin User", "ADMIN");
            ensureUser(con, "recruiter@recruiting.com", "recruiter123", "Maria Papadopoulou", "RECRUITER");
            ensureUser(con, "candidate@recruiting.com", "candidate123", "Nikos Georgiou", "CANDIDATE");
            ensureUser(con, "recruiter2@recruiting.com", "recruiter123", "Dimitris Kostas", "RECRUITER");
            ensureUser(con, "candidate2@recruiting.com", "candidate123", "Elena Stavrou", "CANDIDATE");

            int recruiterId = getUserId(con, "recruiter@recruiting.com");
            int recruiter2Id = getUserId(con, "recruiter2@recruiting.com");
            int candidateId = getUserId(con, "candidate@recruiting.com");
            int candidate2Id = getUserId(con, "candidate2@recruiting.com");

            if (recruiterId == -1 || recruiter2Id == -1) {
                System.err.println("[DemoData] Could not find recruiter users. Aborting seed.");
                return;
            }

            // IT Jobs
            insertJob(con, "Senior Java Developer",
                "We are looking for an experienced Java Developer to join our growing engineering team.\n\nRequirements:\n- 5+ years experience with Java 8+\n- Strong knowledge of Spring Boot and Hibernate\n- Experience with RESTful APIs and microservices\n- Familiarity with SQL databases (MySQL, PostgreSQL)\n\nBenefits:\n- Competitive salary\n- Remote work options\n- Health insurance\n- Annual bonus",
                "TechVision Solutions", "Athens", "IT", recruiterId);

            insertJob(con, "Frontend Developer (React)",
                "Join our product team as a Frontend Developer building modern web applications.\n\nRequirements:\n- 3+ years experience with React.js\n- Strong JavaScript/TypeScript skills\n- Experience with state management (Redux, Context API)\n- CSS/SASS proficiency\n- REST API integration",
                "Digital Innovations SA", "Thessaloniki", "IT", recruiterId);

            insertJob(con, "DevOps Engineer",
                "We need a DevOps Engineer to manage our cloud infrastructure and CI/CD pipelines.\n\nRequirements:\n- Experience with Docker and Kubernetes\n- AWS or GCP cloud experience\n- CI/CD tools (Jenkins, GitLab CI, GitHub Actions)\n- Scripting skills (Bash, Python)\n- Linux administration",
                "CloudFirst Technologies", "Remote", "IT", recruiterId);

            insertJob(con, "Data Analyst",
                "We are seeking a Data Analyst to help drive business decisions through data insights.\n\nRequirements:\n- 2+ years experience in data analysis\n- Strong SQL skills\n- Experience with visualization tools (Tableau/Power BI)\n- Statistical analysis knowledge\n- Excel/Google Sheets proficiency",
                "DataDriven Analytics", "Athens", "IT", recruiterId);

            insertJob(con, "Full Stack Developer (Python/Django)",
                "Build and maintain web applications using Python and Django framework.\n\nRequirements:\n- 2+ years Python/Django experience\n- HTML/CSS/JavaScript knowledge\n- Database design experience\n- REST API development\n- Git version control",
                "PyTech Labs", "Thessaloniki", "IT", recruiter2Id);

            insertJob(con, "UX/UI Designer",
                "We are looking for a creative UX/UI Designer to shape our digital products.\n\nRequirements:\n- 3+ years UX/UI design experience\n- Proficiency in Figma or Sketch\n- Portfolio demonstrating design thinking\n- Understanding of accessibility standards\n- Basic knowledge of HTML/CSS is a plus",
                "DesignStudio Pro", "Athens", "IT", recruiter2Id);

            // Marketing Jobs
            insertJob(con, "Digital Marketing Manager",
                "Lead our digital marketing strategy across all online channels.\n\nRequirements:\n- 4+ years digital marketing experience\n- Google Ads and Facebook Ads certified\n- SEO/SEM expertise\n- Analytics tools proficiency (GA4, GTM)\n- Team management experience",
                "MediaGroup International", "Athens", "Marketing", recruiterId);

            insertJob(con, "Content Marketing Specialist",
                "Create compelling content that drives engagement and brand awareness.\n\nRequirements:\n- 2+ years content marketing experience\n- Excellent writing skills in Greek and English\n- SEO knowledge\n- Social media management experience\n- Creative thinking and storytelling ability",
                "E-Commerce Plus", "Thessaloniki", "Marketing", recruiter2Id);

            // Sales Jobs
            insertJob(con, "Sales Representative - B2B",
                "Drive revenue growth by acquiring new business clients.\n\nRequirements:\n- 2+ years B2B sales experience\n- Excellent communication and negotiation skills\n- CRM experience (Salesforce, HubSpot)\n- Self-motivated with proven track record\n- Valid driver's license",
                "RetailCorp Greece", "Athens", "Sales", recruiterId);

            insertJob(con, "Account Manager",
                "Manage key client accounts and ensure customer satisfaction.\n\nRequirements:\n- 3+ years account management experience\n- Strong relationship-building skills\n- Problem-solving ability\n- Presentation skills\n- Fluent in Greek and English",
                "GlobalTech Partners", "Patras", "Sales", recruiter2Id);

            // HR Jobs
            insertJob(con, "HR Manager",
                "Lead the Human Resources department and drive people strategy.\n\nRequirements:\n- 5+ years HR experience, 2+ in management\n- Knowledge of Greek labor law\n- HRIS experience\n- Strong interpersonal skills\n- HR certification is a plus",
                "PeopleFirst Consulting", "Athens", "HR", recruiterId);

            insertJob(con, "Recruitment Specialist",
                "Find and attract top talent to grow our team.\n\nRequirements:\n- 2+ years recruitment experience\n- LinkedIn Recruiter experience\n- ATS knowledge\n- Excellent communication skills\n- Tech recruitment experience is a plus",
                "TalentHub Greece", "Thessaloniki", "HR", recruiter2Id);

            // Other category
            insertJob(con, "Customer Support Team Lead",
                "Lead our customer support team and ensure excellent service delivery.\n\nRequirements:\n- 3+ years customer support experience\n- 1+ year team leadership\n- Excellent problem-solving skills\n- Experience with ticketing systems (Zendesk, Freshdesk)\n- Bilingual: Greek and English",
                "ServiceHub Solutions", "Patras", "OTHER", recruiterId);

            insertJob(con, "Project Manager",
                "Manage software development projects from inception to delivery.\n\nRequirements:\n- 3+ years project management experience\n- Agile/Scrum methodology knowledge\n- PMP or equivalent certification preferred\n- Strong organizational skills\n- Technical background is a plus",
                "Management Pro SA", "Athens", "OTHER", recruiter2Id);

            // Insert demo applications
            if (candidateId != -1) {
                insertApplication(con, "Senior Java Developer", candidateId, "PENDING",
                    "Very interested in this position. I have relevant experience.");
                insertApplication(con, "Frontend Developer (React)", candidateId, "REVIEWED",
                    "Application under review. Strong React background.");
                insertApplication(con, "DevOps Engineer", candidateId, "PENDING",
                    "Interested in DevOps role. Experienced with Docker and Kubernetes.");
            }
            if (candidate2Id != -1) {
                insertApplication(con, "Digital Marketing Manager", candidate2Id, "ACCEPTED",
                    "Congratulations! Moved to interview stage.");
                insertApplication(con, "Data Analyst", candidate2Id, "PENDING",
                    "Looking for a new challenge in data analysis.");
            }

            System.out.println("[DemoData] Demo data seeded successfully! 14 jobs, 5 applications created.");

        } catch (SQLException e) {
            System.err.println("[DemoData] Error seeding demo data: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(con);
        }
    }

    private void ensureUser(Connection con, String email, String password, String name, String role) throws SQLException {
        String sql = "INSERT IGNORE INTO users (email, password, name, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, name);
            ps.setString(4, role);
            ps.executeUpdate();
        }
    }

    private int getUserId(Connection con, String email) throws SQLException {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        return -1;
    }

    private void insertJob(Connection con, String title, String description, String company, String location, String category, int postedBy) throws SQLException {
        // Check if job already exists
        String checkSql = "SELECT COUNT(*) FROM jobs WHERE title = ? AND company = ?";
        try (PreparedStatement ps = con.prepareStatement(checkSql)) {
            ps.setString(1, title);
            ps.setString(2, company);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) return;
            }
        }

        String sql = "INSERT INTO jobs (title, description, company, location, category, posted_by, status) VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, company);
            ps.setString(4, location);
            ps.setString(5, category);
            ps.setInt(6, postedBy);
            ps.executeUpdate();
        }
    }

    private void insertApplication(Connection con, String jobTitle, int candidateId, String status, String notes) throws SQLException {
        String sql = "INSERT IGNORE INTO applications (job_id, candidate_id, status, notes) " +
                "SELECT j.id, ?, ?, ? FROM jobs j WHERE j.title = ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, candidateId);
            ps.setString(2, status);
            ps.setString(3, notes);
            ps.setString(4, jobTitle);
            ps.executeUpdate();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup if needed
    }
}
