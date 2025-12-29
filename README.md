# E-Library Management System

A comprehensive JavaFX-based digital library management system for educational institutions with separate interfaces for students and administrators.

## 📋 Table of Contents
- [Features](#features)
- [System Requirements](#system-requirements)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Building and Running](#building-and-running)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Documentation](#documentation)
- [License](#license)

## ✨ Features

### Student Side
- 🔐 Secure login using LRN (Learner Reference Number)
- 📚 Browse PDF books by category
- 🔍 Search books by title, author, or keywords
- 👁️ View detailed book information
- ⬇️ Download books with automatic LRN watermarking
- 📊 Track personal reading history

### Admin Side
- 🔒 Secure username/password authentication
- ➕ Add new books with PDF upload
- ✏️ Edit existing book information
- 🗑️ Delete books from library
- 📁 Manage book categories
- 📈 View statistics (views, downloads)
- 📋 Track student access logs

## 🖥️ System Requirements

### Hardware
- Processor: Intel Core i3 or equivalent (minimum)
- RAM: 4GB (minimum), 8GB (recommended)
- Storage: 500MB for application + space for book library
- Display: 1024x768 resolution (minimum)

### Software
- **Java JDK**: 11 or higher
- **JavaFX SDK**: 17 or higher
- **MySQL Server**: 8.0 or higher
- **Maven**: 3.6 or higher (for building from source)
- **Operating System**: Windows 10/11, macOS 10.14+, or Linux

## 🚀 Installation

### 1. Install Java JDK
```bash
# Download from Oracle or OpenJDK
# Verify installation
java -version
```

### 2. Install MySQL
```bash
# Download MySQL Community Server from mysql.com
# Install and configure with root password
# Start MySQL service
```

### 3. Install Maven
```bash
# Download from maven.apache.org
# Add to PATH
# Verify installation
mvn -version
```

## 🗄️ Database Setup

### Create Database
```bash
# Navigate to project directory
cd elibrary-project

# Run schema script
mysql -u root -p < database/schema.sql
```

### Verify Database
```sql
mysql -u root -p
USE elibrary_db;
SHOW TABLES;
```

### Default Credentials

**Admin Login:**
- Username: `admin`
- Password: `admin123`

**Sample Student:**
- LRN: `123456789012`

⚠️ **Important:** Change the default admin password immediately after first login!

### Configure Database Connection

Edit `src/main/java/com/elibrary/database/DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/elibrary_db";
private static final String USERNAME = "root";  // Your MySQL username
private static final String PASSWORD = "";      // Your MySQL password
```

## 🔨 Building and Running

### Build with Maven
```bash
# Clean and build
mvn clean package

# The JAR file will be in target/ directory
```

### Run the Application

**Using Maven:**
```bash
mvn javafx:run
```

**Or run the JAR directly:**
```bash
java -jar target/elibrary-system-1.0.0.jar
```

## 📁 Project Structure

```
elibrary-project/
├── src/
│   └── main/
│       ├── java/com/elibrary/
│       │   ├── MainApp.java
│       │   ├── controllers/
│       │   │   ├── StudentLoginController.java
│       │   │   ├── AdminLoginController.java
│       │   │   ├── StudentDashboardController.java
│       │   │   └── AdminDashboardController.java
│       │   ├── models/
│       │   │   ├── Student.java
│       │   │   ├── Admin.java
│       │   │   ├── Book.java
│       │   │   ├── Category.java
│       │   │   └── AccessLog.java
│       │   ├── database/
│       │   │   ├── DatabaseConnection.java
│       │   │   ├── StudentDAO.java
│       │   │   ├── AdminDAO.java
│       │   │   ├── BookDAO.java
│       │   │   ├── CategoryDAO.java
│       │   │   └── AccessLogDAO.java
│       │   └── utils/
│       │       ├── PasswordUtil.java
│       │       ├── PDFWatermarkUtil.java
│       │       └── SessionManager.java
│       └── resources/
│           ├── fxml/
│           ├── css/
│           └── images/
├── database/
│   └── schema.sql
├── docs/
│   └── E-Library_User_Manual.docx
├── pom.xml
└── README.md
```

## 🛠️ Technologies Used

- **Frontend:** JavaFX 17
- **Backend:** Java 11
- **Database:** MySQL 8.0
- **PDF Processing:** Apache PDFBox 2.0
- **Build Tool:** Maven 3.6+
- **Password Hashing:** SHA-256

## 📚 Documentation

Complete documentation is available in:
- **User Manual:** `docs/E-Library_User_Manual.docx`
- **Database Schema:** `database/schema.sql`
- **API Documentation:** Javadoc comments in source code

### Database Schema

The system uses 5 main tables:

1. **STUDENT** - Student information and LRN
2. **CATEGORY** - Book categories/genres
3. **BOOK** - Book information and file paths
4. **ACCESS_LOG** - Tracks views and downloads
5. **ADMIN** - Administrator credentials

## 🔧 Configuration

### File Storage
By default, uploaded books and cover images are stored in:
- Books: `library/books/`
- Covers: `library/covers/`

Create these directories in the project root before adding books.

### PDF Watermarking
When students download a book, their LRN is automatically added as a watermark on the first page. This is done using Apache PDFBox and the `PDFWatermarkUtil` class.

## 📝 Development Notes

### Adding New Features
1. Create model classes in `models/` package
2. Add DAO classes in `database/` package for database operations
3. Create FXML files in `resources/fxml/`
4. Implement controllers in `controllers/` package
5. Update CSS in `resources/css/styles.css`

### Testing
```bash
# Run tests
mvn test
```

## 🐛 Troubleshooting

### Application won't start
- Verify Java 11+ is installed
- Check JavaFX is properly configured
- Ensure all dependencies are in classpath

### Database connection error
- Verify MySQL is running
- Check database credentials
- Ensure `elibrary_db` database exists
- Verify MySQL port (default: 3306) is accessible

### Cannot download books
- Check file permissions on book directory
- Verify PDF files exist at specified paths
- Ensure write permissions for download directory

### Watermark not appearing
- Verify Apache PDFBox dependency is included
- Check PDF file is not corrupted
- Ensure sufficient disk space for temporary files

## 📞 Support

For technical support or questions:
- Email: support@elibrary.example.com
- Documentation: https://docs.elibrary.example.com

## 📄 License

© 2025 E-Library Management System. All rights reserved.

## 🙏 Acknowledgments

- JavaFX Community
- Apache PDFBox Team
- MySQL Team

---

**Version:** 1.0.0  
**Last Updated:** December 2025
