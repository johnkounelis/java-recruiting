-- Comprehensive Demo Data for Recruiting App
-- Run this after database_schema.sql to populate with realistic demo jobs

USE recruiting_db;

-- Ensure demo users exist
INSERT IGNORE INTO users (email, password, name, role) VALUES
('admin@recruiting.com', 'admin123', 'Admin User', 'ADMIN'),
('recruiter@recruiting.com', 'recruiter123', 'Maria Papadopoulou', 'RECRUITER'),
('candidate@recruiting.com', 'candidate123', 'Nikos Georgiou', 'CANDIDATE'),
('recruiter2@recruiting.com', 'recruiter123', 'Dimitris Kostas', 'RECRUITER'),
('candidate2@recruiting.com', 'candidate123', 'Elena Stavrou', 'CANDIDATE');

-- Get recruiter user IDs (assuming recruiter@recruiting.com is id=2, recruiter2 is id=4)
-- Delete old demo jobs to avoid duplicates and re-insert
DELETE FROM applications WHERE job_id IN (SELECT id FROM jobs WHERE posted_by IN (
    SELECT id FROM users WHERE email IN ('recruiter@recruiting.com', 'recruiter2@recruiting.com')
));
DELETE FROM jobs WHERE posted_by IN (
    SELECT id FROM users WHERE email IN ('recruiter@recruiting.com', 'recruiter2@recruiting.com')
);

-- Insert comprehensive demo jobs across all categories
-- IT Jobs
INSERT INTO jobs (title, description, company, location, category, posted_by, status) VALUES
('Senior Java Developer',
'We are looking for an experienced Java Developer to join our growing engineering team.\n\nRequirements:\n- 5+ years experience with Java 8+\n- Strong knowledge of Spring Boot and Hibernate\n- Experience with RESTful APIs and microservices\n- Familiarity with SQL databases (MySQL, PostgreSQL)\n- Version control with Git\n\nBenefits:\n- Competitive salary\n- Remote work options\n- Health insurance\n- Annual bonus',
'TechVision Solutions', 'Athens', 'IT',
(SELECT id FROM users WHERE email = 'recruiter@recruiting.com'), 'ACTIVE'),

('Frontend Developer (React)',
'Join our product team as a Frontend Developer building modern web applications.\n\nWhat you will do:\n- Build responsive user interfaces with React.js\n- Collaborate with designers and backend engineers\n- Write clean, tested, and maintainable code\n- Participate in code reviews and sprint planning\n\nRequirements:\n- 3+ years experience with React.js\n- Strong JavaScript/TypeScript skills\n- Experience with state management (Redux, Context API)\n- CSS/SASS proficiency\n- REST API integration',
'Digital Innovations SA', 'Thessaloniki', 'IT',
(SELECT id FROM users WHERE email = 'recruiter@recruiting.com'), 'ACTIVE'),

('DevOps Engineer',
'We need a DevOps Engineer to manage our cloud infrastructure and CI/CD pipelines.\n\nResponsibilities:\n- Design and maintain CI/CD pipelines\n- Manage Kubernetes clusters on AWS/GCP\n- Monitor system performance and reliability\n- Automate infrastructure provisioning with Terraform\n- Implement security best practices\n\nRequirements:\n- Experience with Docker and Kubernetes\n- AWS or GCP cloud experience\n- CI/CD tools (Jenkins, GitLab CI, GitHub Actions)\n- Scripting skills (Bash, Python)\n- Linux administration',
'CloudFirst Technologies', 'Remote', 'IT',
(SELECT id FROM users WHERE email = 'recruiter@recruiting.com'), 'ACTIVE'),

('Data Analyst',
'We are seeking a Data Analyst to help drive business decisions through data insights.\n\nResponsibilities:\n- Analyze large datasets to identify trends and patterns\n- Create dashboards and reports using Tableau/Power BI\n- Work with stakeholders to define KPIs\n- Write SQL queries for data extraction\n- Present findings to management\n\nRequirements:\n- 2+ years experience in data analysis\n- Strong SQL skills\n- Experience with visualization tools\n- Statistical analysis knowledge\n- Excel/Google Sheets proficiency',
'DataDriven Analytics', 'Athens', 'IT',
(SELECT id FROM users WHERE email = 'recruiter@recruiting.com'), 'ACTIVE'),

('Full Stack Developer (Python/Django)',
'Build and maintain web applications using Python and Django framework.\n\nWhat we offer:\n- Modern tech stack (Python 3.11, Django 5, PostgreSQL, Docker)\n- Agile development environment\n- Mentorship from senior engineers\n- Conference attendance budget\n\nRequirements:\n- 2+ years Python/Django experience\n- HTML/CSS/JavaScript knowledge\n- Database design experience\n- REST API development\n- Git version control',
'PyTech Labs', 'Thessaloniki', 'IT',
(SELECT id FROM users WHERE email = 'recruiter2@recruiting.com'), 'ACTIVE'),

('UX/UI Designer',
'We are looking for a creative UX/UI Designer to shape our digital products.\n\nResponsibilities:\n- Design intuitive user interfaces for web and mobile apps\n- Conduct user research and usability testing\n- Create wireframes, prototypes, and high-fidelity mockups\n- Collaborate closely with developers\n- Maintain and evolve our design system\n\nRequirements:\n- 3+ years UX/UI design experience\n- Proficiency in Figma or Sketch\n- Portfolio demonstrating design thinking\n- Understanding of accessibility standards\n- Basic knowledge of HTML/CSS is a plus',
'DesignStudio Pro', 'Athens', 'IT',
(SELECT id FROM users WHERE email = 'recruiter2@recruiting.com'), 'ACTIVE'),

-- Marketing Jobs
('Digital Marketing Manager',
'Lead our digital marketing strategy across all online channels.\n\nResponsibilities:\n- Develop and execute digital marketing campaigns\n- Manage Google Ads and Social Media advertising\n- SEO strategy and content optimization\n- Email marketing campaigns\n- Analytics and reporting\n\nRequirements:\n- 4+ years digital marketing experience\n- Google Ads and Facebook Ads certified\n- SEO/SEM expertise\n- Analytics tools proficiency (GA4, GTM)\n- Team management experience',
'MediaGroup International', 'Athens', 'Marketing',
(SELECT id FROM users WHERE email = 'recruiter@recruiting.com'), 'ACTIVE'),

('Content Marketing Specialist',
'Create compelling content that drives engagement and brand awareness.\n\nWhat you will do:\n- Write blog posts, articles, and social media content\n- Develop content strategy aligned with business goals\n- Manage editorial calendar\n- Collaborate with design team on visual content\n- Track content performance metrics\n\nRequirements:\n- 2+ years content marketing experience\n- Excellent writing skills in Greek and English\n- SEO knowledge\n- Social media management experience\n- Creative thinking and storytelling ability',
'E-Commerce Plus', 'Thessaloniki', 'Marketing',
(SELECT id FROM users WHERE email = 'recruiter2@recruiting.com'), 'ACTIVE'),

-- Sales Jobs
('Sales Representative - B2B',
'Drive revenue growth by acquiring new business clients.\n\nResponsibilities:\n- Identify and pursue new business opportunities\n- Build and maintain client relationships\n- Negotiate contracts and close deals\n- Meet and exceed sales targets\n- CRM management and reporting\n\nRequirements:\n- 2+ years B2B sales experience\n- Excellent communication and negotiation skills\n- CRM experience (Salesforce, HubSpot)\n- Self-motivated with proven track record\n- Valid driver''s license',
'RetailCorp Greece', 'Athens', 'Sales',
(SELECT id FROM users WHERE email = 'recruiter@recruiting.com'), 'ACTIVE'),

('Account Manager',
'Manage key client accounts and ensure customer satisfaction.\n\nResponsibilities:\n- Serve as the main point of contact for assigned accounts\n- Understand client needs and propose solutions\n- Upsell and cross-sell products/services\n- Prepare reports and presentations\n- Collaborate with internal teams\n\nRequirements:\n- 3+ years account management experience\n- Strong relationship-building skills\n- Problem-solving ability\n- Presentation skills\n- Fluent in Greek and English',
'GlobalTech Partners', 'Patras', 'Sales',
(SELECT id FROM users WHERE email = 'recruiter2@recruiting.com'), 'ACTIVE'),

-- HR Jobs
('HR Manager',
'Lead the Human Resources department and drive people strategy.\n\nResponsibilities:\n- Develop and implement HR policies\n- Oversee recruitment and onboarding processes\n- Manage employee relations and performance reviews\n- Design training and development programs\n- Ensure labor law compliance\n\nRequirements:\n- 5+ years HR experience, 2+ in management\n- Knowledge of Greek labor law\n- HRIS experience\n- Strong interpersonal skills\n- HR certification is a plus',
'PeopleFirst Consulting', 'Athens', 'HR',
(SELECT id FROM users WHERE email = 'recruiter@recruiting.com'), 'ACTIVE'),

('Recruitment Specialist',
'Find and attract top talent to grow our team.\n\nWhat you will do:\n- Source candidates through various channels\n- Conduct screening interviews\n- Manage the full recruitment lifecycle\n- Build talent pipelines for future needs\n- Improve employer branding\n\nRequirements:\n- 2+ years recruitment experience\n- LinkedIn Recruiter experience\n- ATS knowledge\n- Excellent communication skills\n- Tech recruitment experience is a plus',
'TalentHub Greece', 'Thessaloniki', 'HR',
(SELECT id FROM users WHERE email = 'recruiter2@recruiting.com'), 'ACTIVE'),

-- Other category
('Customer Support Team Lead',
'Lead our customer support team and ensure excellent service delivery.\n\nResponsibilities:\n- Manage a team of 5-8 support agents\n- Handle escalated customer issues\n- Develop support processes and documentation\n- Monitor KPIs (response time, satisfaction scores)\n- Train new team members\n\nRequirements:\n- 3+ years customer support experience\n- 1+ year team leadership\n- Excellent problem-solving skills\n- Experience with ticketing systems (Zendesk, Freshdesk)\n- Bilingual: Greek and English',
'ServiceHub Solutions', 'Patras', 'OTHER',
(SELECT id FROM users WHERE email = 'recruiter@recruiting.com'), 'ACTIVE'),

('Project Manager',
'Manage software development projects from inception to delivery.\n\nResponsibilities:\n- Plan and coordinate project activities\n- Manage project scope, timeline, and budget\n- Facilitate team communication and collaboration\n- Risk management and mitigation\n- Stakeholder reporting and presentations\n\nRequirements:\n- 3+ years project management experience\n- Agile/Scrum methodology knowledge\n- PMP or equivalent certification preferred\n- Strong organizational skills\n- Technical background is a plus',
'Management Pro SA', 'Athens', 'OTHER',
(SELECT id FROM users WHERE email = 'recruiter2@recruiting.com'), 'ACTIVE');

-- Insert demo applications from candidates
INSERT IGNORE INTO applications (job_id, candidate_id, status, notes)
SELECT j.id, u.id, 'PENDING', 'Very interested in this position. I have relevant experience and would love to discuss further.'
FROM jobs j, users u
WHERE j.title = 'Senior Java Developer' AND u.email = 'candidate@recruiting.com';

INSERT IGNORE INTO applications (job_id, candidate_id, status, notes)
SELECT j.id, u.id, 'REVIEWED', 'Application under review. Strong background in React development.'
FROM jobs j, users u
WHERE j.title = 'Frontend Developer (React)' AND u.email = 'candidate@recruiting.com';

INSERT IGNORE INTO applications (job_id, candidate_id, status, notes)
SELECT j.id, u.id, 'PENDING', 'Interested in DevOps role. Experienced with Docker and Kubernetes.'
FROM jobs j, users u
WHERE j.title = 'DevOps Engineer' AND u.email = 'candidate@recruiting.com';

INSERT IGNORE INTO applications (job_id, candidate_id, status, notes)
SELECT j.id, u.id, 'ACCEPTED', 'Congratulations! Moved to interview stage.'
FROM jobs j, users u
WHERE j.title = 'Digital Marketing Manager' AND u.email = 'candidate2@recruiting.com';

INSERT IGNORE INTO applications (job_id, candidate_id, status, notes)
SELECT j.id, u.id, 'PENDING', 'Looking for a new challenge in data analysis.'
FROM jobs j, users u
WHERE j.title = 'Data Analyst' AND u.email = 'candidate2@recruiting.com';

INSERT IGNORE INTO applications (job_id, candidate_id, status, notes)
SELECT j.id, u.id, 'REJECTED', 'Thank you for applying. We will keep your profile on file.'
FROM jobs j, users u
WHERE j.title = 'HR Manager' AND u.email = 'candidate2@recruiting.com';
