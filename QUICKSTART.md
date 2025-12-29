# E-Library System - Quick Start Guide

## 🚀 Get Started in 5 Minutes

### Prerequisites
Make sure you have:
- ✅ Java JDK 11 or higher
- ✅ MySQL Server 8.0 or higher
- ✅ Maven 3.6 or higher

### Step 1: Download the Project
```bash
# Extract the project files to your desired location
cd elibrary-project
```

### Step 2: Setup Database
```bash
# Open MySQL command line
mysql -u root -p

# Run the schema file
mysql -u root -p < database/schema.sql

# Verify installation
USE elibrary_db;
SHOW TABLES;
EXIT;
```

You should see 5 tables: STUDENT, ADMIN, BOOK, CATEGORY, ACCESS_LOG

### Step 3: Configure Database Connection
Edit the file: `src/main/java/com/elibrary/database/DatabaseConnection.java`

Update these lines with your MySQL credentials:
```java
private static final String USERNAME = "root";      // Your MySQL username
private static final String PASSWORD = "your_pass";  // Your MySQL password
```

### Step 4: Create Library Directories
```bash
# In project root directory
mkdir -p library/books
mkdir -p library/covers
mkdir -p library/downloads
```

### Step 5: Build the Project
```bash
# Clean and build
mvn clean package

# This will create: target/elibrary-system-1.0.0.jar
```

### Step 6: Run the Application
```bash
# Option 1: Using Maven
mvn javafx:run

# Option 2: Using Java directly
java -jar target/elibrary-system-1.0.0.jar
```

## 🔐 Default Login Credentials

### Admin Account
- **Username:** admin
- **Password:** admin123

### Sample Student Account
- **LRN:** 123456789012 (Juan Dela Cruz)
- **LRN:** 123456789013 (Maria Santos)
- **LRN:** 123456789014 (Pedro Reyes)

## 📚 Adding Your First Book (Admin)

1. Login as admin
2. Click "Add Book" button
3. Fill in the details:
   - Title: e.g., "Introduction to Programming"
   - Author: e.g., "John Doe"
   - Year: e.g., "2024"
   - Category: Select from dropdown
   - Description: Brief summary
4. Upload PDF file (must be .pdf format)
5. Optionally upload cover image (.jpg, .png)
6. Click "Save"

## 📖 Downloading a Book (Student)

1. Login with your LRN
2. Browse books or use search
3. Click on a book to view details
4. Click "Download" button
5. Choose save location
6. Your LRN will be automatically added as watermark

## ⚙️ Common Issues and Solutions

### Issue: "Connection refused" error
**Solution:** Make sure MySQL is running
```bash
# Windows
net start MySQL80

# Mac
brew services start mysql

# Linux
sudo systemctl start mysql
```

### Issue: "Table doesn't exist" error
**Solution:** Run the schema.sql file again
```bash
mysql -u root -p < database/schema.sql
```

### Issue: JavaFX runtime not found
**Solution:** Make sure JavaFX is in your module path
```bash
# Run with explicit module path
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar app.jar
```

### Issue: Can't upload books
**Solution:** Check directory permissions
```bash
# Give write permissions to library directory
chmod -R 755 library/
```

## 📝 Next Steps

1. **Change Default Password**: Go to Admin Settings → Change Password
2. **Add Students**: Admin Dashboard → Manage Students → Add New
3. **Add Categories**: Admin Dashboard → Manage Categories
4. **Upload Books**: Admin Dashboard → Add Book
5. **View Statistics**: Admin Dashboard → Statistics

## 📚 Additional Resources

- **Full User Manual**: See `docs/E-Library_User_Manual.docx`
- **Technical Documentation**: See `docs/IMPLEMENTATION_GUIDE.md`
- **Database Schema**: See `database/schema.sql`
- **README**: See `README.md`

## 🆘 Getting Help

If you encounter issues:

1. Check the logs in console output
2. Verify database connection in DatabaseConnection.java
3. Ensure all dependencies are installed
4. Review the User Manual for detailed instructions

## 🎯 Testing the Installation

### Quick Test Checklist
- [ ] Application launches without errors
- [ ] Can login as admin with default credentials
- [ ] Can login as student with sample LRN
- [ ] Can view the dashboard
- [ ] Database connection is working
- [ ] Can navigate between screens

### Sample Test Scenario

1. **Test Admin Login**
   - Open application
   - Click "Admin Login"
   - Username: admin
   - Password: admin123
   - Should show admin dashboard

2. **Test Book Management**
   - Click "Add Book"
   - Fill in sample data
   - Upload a test PDF
   - Verify book appears in list

3. **Test Student Login**
   - Logout from admin
   - Click "Student Login"
   - LRN: 123456789012
   - Should show student dashboard

4. **Test Book Download**
   - Search for the book you added
   - Click "Download"
   - Check Downloads folder
   - Verify LRN watermark is present

## 💡 Tips for Success

1. **Start Simple**: Add one book first to test the system
2. **Use Test Data**: Test with sample PDFs before real books
3. **Regular Backups**: Backup your database regularly
4. **Monitor Logs**: Check console for errors
5. **Update Regularly**: Keep dependencies up to date

## 🎓 Learning Path

### For Students
1. Learn how to browse the catalog
2. Practice searching for books
3. Understand categories
4. Download and read books

### For Administrators
1. Master the dashboard
2. Learn CRUD operations for books
3. Understand statistics
4. Manage user accounts
5. Monitor system activity

## ⏱️ Time Estimates

- Initial Setup: 10-15 minutes
- First Book Upload: 2-3 minutes
- Understanding Interface: 15-20 minutes
- Full System Mastery: 1-2 hours

## 🔧 Customization Options

Want to customize? Here's what you can change:

1. **Colors**: Edit `src/main/resources/css/styles.css`
2. **Layout**: Modify FXML files in `src/main/resources/fxml/`
3. **Features**: Add controllers in `src/main/java/com/elibrary/controllers/`
4. **Database**: Extend schema in `database/schema.sql`

## ✅ Success Checklist

You're ready to use the system when you can:
- [ ] Login as both admin and student
- [ ] Add a new book
- [ ] Search for books
- [ ] Download a book with watermark
- [ ] View statistics
- [ ] Manage categories

Congratulations! You're ready to use the E-Library Management System! 🎉

---

**Need More Help?**
- 📧 Email: support@elibrary.example.com
- 📖 Full Manual: docs/E-Library_User_Manual.docx
- 🔧 Technical Guide: docs/IMPLEMENTATION_GUIDE.md
