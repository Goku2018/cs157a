# cs157a - Library Management System
Project Overview:
A desktop app for library staff to manage books, members, checkouts/returns, fines, and payments.

Setup Instructions:
1. Download MySQL.
2. Create user and password.
3. Create database.
4. Run create_schema.sql and then initialize_data.sql to create tables and sample data within database (see instructions at the beginning of both files).
5. Run LoginFrame.java to start application.

Dependencies and Required Software:
- Java 21
- Maven
- MySQL
- MySQL Connector/J JDBC driver
- IntelliJ IDEA or another Java IDE

Additional configuration steps needed to connect to the database:
1. Open src/main/resources/db.properties.
2. Set db.url to match your database name.
3. Set db.user and db.password to your MySQL username and password.
4. Make sure the USE statement at the top of create_schema.sql and initialize_data.sql matches the same database.
