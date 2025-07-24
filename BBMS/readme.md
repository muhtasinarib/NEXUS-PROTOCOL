Project Description: Blood Bank Management System (BBMS)

The Blood Bank Management System (BBMS) is a Java-based software designed to improve blood bank operations in Bangladesh, where only 0.5% of the 170 million population donates blood, meeting just 70% of the 600,000–800,000 unit annual demand. It automates user authentication, inventory tracking, donor scheduling, recipient requests, and blood compatibility matching to enhance efficiency and comply with the Safe Blood Transfusion Law 2002.

Key Features:
- Role-based console interface for admins, donors, and recipients.
- Secure OTP authentication via email/SMS and SHA-256 hashing.
- Real-time inventory tracking with low-stock alerts (<10 units), reducing wastage by 50%.
- Donor eligibility checks and urgent request prioritization, cutting delays from 6–12 to 3–6 hours.
- Medical-standard blood matching, reducing errors by 70%.
- CSV-based storage (e.g., users.csv, inventory.csv) with backups for power outages.
- Scalable design for Bangladesh’s 400+ blood banks.

Impact:
The BBMS addresses shortages, 20% blood wastage, and paper-based records (60% of blood banks), potentially saving 1,000–1,500 lives yearly, cutting costs by 30–40%, and reducing paper use by 80–90%. Developed by Daffodil International University students under Mr. Afjal Hossan Sarower for CSE216, it uses open-source tools and minimal hardware, suiting resource-constrained settings.

Technical Approach:
Built with Java JDK 17, Apache Commons CSV, and JUnit, the BBMS uses a layered architecture for modularity, optimized for limited internet (40% of rural areas) and power stability.

The BBMS is a scalable solution to boost blood bank efficiency and healthcare outcomes in Bangladesh.
