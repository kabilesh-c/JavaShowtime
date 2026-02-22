# 🎬 Movie Ticket Reservation System - Complete Setup Guide

Welcome to the Movie Ticket Reservation System! This guide will walk you through the complete installation and setup process.

## 📋 Table of Contents
1. [Prerequisites](#prerequisites)
2. [Technology Stack](#technology-stack)
3. [Installation Methods](#installation-methods)
4. [Database Setup](#database-setup)
5. [Backend Setup](#backend-setup)
6. [Frontend Setup](#frontend-setup)
7. [Running the Application](#running-the-application)
8. [Default Admin Credentials](#default-admin-credentials)
9. [Troubleshooting](#troubleshooting)

---

## 🔧 Prerequisites

Before you begin, ensure you have the following installed on your system:

### Required Software
- **Java JDK 17 or higher** - [Download](https://www.oracle.com/java/technologies/downloads/)
- **Node.js 18+ and npm** - [Download](https://nodejs.org/)
- **PostgreSQL Database** (or use Supabase)
- **Git** - [Download](https://git-scm.com/)

### Optional Tools
- **Maven 3.6+** (if not using Maven wrapper)
- **VS Code** or any code editor
- **Postman** for API testing

---

## 🛠 Technology Stack

### Backend
- **Spring Boot 3.2.0** - Java framework
- **PostgreSQL** - Database
- **JWT** - Authentication
- **Maven** - Build tool

### Frontend
- **React 18** - UI framework
- **Vite** - Build tool
- **Tailwind CSS** - Styling
- **Framer Motion** - Animations
- **React Router** - Navigation
- **Axios** - HTTP client

---

## 📦 Installation Methods

### Method 1: Using Package Managers (Recommended)

#### Windows

**Install Java:**
```powershell
# Using Chocolatey
choco install openjdk17

# Or download from Oracle and add to PATH
```

**Install Node.js:**
```powershell
# Using Chocolatey
choco install nodejs-lts

# Or download installer from nodejs.org
```

**Install Maven (Optional):**
```powershell
# Using Chocolatey
choco install maven

# Or use the included Maven wrapper (mvnw)
```

#### macOS

```bash
# Install Homebrew if not already installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Java
brew install openjdk@17

# Install Node.js
brew install node

# Install Maven (Optional)
brew install maven
```

#### Linux (Ubuntu/Debian)

```bash
# Update package list
sudo apt update

# Install Java
sudo apt install openjdk-17-jdk

# Install Node.js
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs

# Install Maven (Optional)
sudo apt install maven
```

### Method 2: Manual Installation

1. **Java JDK 17+**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [AdoptOpenJDK](https://adoptopenjdk.net/)
   - Install and add to system PATH
   - Verify: `java -version`

2. **Node.js 18+**
   - Download from [nodejs.org](https://nodejs.org/)
   - Install using the installer
   - Verify: `node -v` and `npm -v`

3. **Maven** (Optional - project includes Maven wrapper)
   - Download from [maven.apache.org](https://maven.apache.org/download.cgi)
   - Extract and add `bin` folder to PATH
   - Verify: `mvn -v`

---

## 🗄️ Database Setup

### Option 1: Using Supabase (Cloud - Recommended for Quick Start)

1. **Create a Supabase Account**
   - Go to [supabase.com](https://supabase.com)
   - Sign up for a free account
   - Create a new project

2. **Get Database Credentials**
   - Navigate to Project Settings → Database
   - Copy the connection string (JDBC format)
   - Copy the database password you set during project creation

3. **Run Database Migration**
   - Go to SQL Editor in Supabase dashboard
   - Copy the contents of `movie-booking-backend/src/main/resources/db/init.sql`
   - Execute the SQL script
   - Optionally, run `sample-data.sql` for demo data

4. **Update Backend Configuration**
   - Open `movie-booking-backend/src/main/resources/application.properties`
   - Update the following:
   ```properties
   spring.datasource.url=jdbc:postgresql://[YOUR-SUPABASE-HOST]:5432/postgres
   spring.datasource.username=postgres
   spring.datasource.password=[YOUR-PASSWORD]
   ```

### Option 2: Local PostgreSQL Installation

#### Windows

1. **Download PostgreSQL**
   - Download from [postgresql.org](https://www.postgresql.org/download/windows/)
   - Run the installer
   - Set a password for the `postgres` user
   - Default port: 5432

2. **Create Database**
   ```powershell
   # Open PowerShell and connect to PostgreSQL
   psql -U postgres
   
   # Create database
   CREATE DATABASE movie_booking;
   
   # Exit psql
   \q
   ```

3. **Run Migration Scripts**
   ```powershell
   # Navigate to project directory
   cd "movie-booking-backend/src/main/resources/db"
   
   # Run init script
   psql -U postgres -d movie_booking -f init.sql
   
   # Run sample data (optional)
   psql -U postgres -d movie_booking -f sample-data.sql
   ```

#### macOS

```bash
# Install PostgreSQL
brew install postgresql@14

# Start PostgreSQL service
brew services start postgresql@14

# Create database
createdb movie_booking

# Run migration scripts
psql -d movie_booking -f movie-booking-backend/src/main/resources/db/init.sql
psql -d movie_booking -f movie-booking-backend/src/main/resources/db/sample-data.sql
```

#### Linux

```bash
# Install PostgreSQL
sudo apt install postgresql postgresql-contrib

# Start PostgreSQL service
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Switch to postgres user
sudo -i -u postgres

# Create database
createdb movie_booking

# Exit postgres user
exit

# Run migration scripts
psql -U postgres -d movie_booking -f movie-booking-backend/src/main/resources/db/init.sql
psql -U postgres -d movie_booking -f movie-booking-backend/src/main/resources/db/sample-data.sql
```

4. **Update Backend Configuration**
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/movie_booking
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   ```

---

## ⚙️ Backend Setup

### Step 1: Navigate to Backend Directory

```bash
cd "movie-booking-backend"
```

### Step 2: Configure Application Properties

Open `src/main/resources/application.properties` and verify/update:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:postgresql://your-database-host:5432/your-database-name
spring.datasource.username=your-username
spring.datasource.password=your-password
spring.datasource.driver-class-name=org.postgresql.Driver

# JWT Configuration
jwt.secret=your-secret-key-minimum-32-characters-long
jwt.expiration=86400000

# Logging
logging.level.com.moviereserve=DEBUG
```

### Step 3: Install Dependencies

#### Using Maven Wrapper (Recommended - No Maven Installation Required)

**Windows:**
```powershell
.\mvnw clean install
```

**macOS/Linux:**
```bash
./mvnw clean install
```

#### Using Installed Maven

```bash
mvn clean install
```

### Step 4: Verify Build

You should see:
```
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 🎨 Frontend Setup

### Step 1: Navigate to Frontend Directory

```bash
cd "../movie-booking-frontend"
```

### Step 2: Install Dependencies

#### Using npm (Recommended)

```bash
npm install
```

#### Using yarn (Alternative)

```bash
# Install yarn if not already installed
npm install -g yarn

# Install dependencies
yarn install
```

#### Using pnpm (Alternative)

```bash
# Install pnpm if not already installed
npm install -g pnpm

# Install dependencies
pnpm install
```

### Step 3: Configure Environment Variables (Optional)

Create a `.env` file in the `movie-booking-frontend` directory:

```env
VITE_API_URL=http://localhost:8080/api
```

**Note:** If you don't create this file, the app will default to `http://localhost:8080/api`

### Step 4: Verify Installation

Check that `node_modules` folder is created and dependencies are installed.

---

## 🚀 Running the Application

### Step 1: Start the Backend Server

Open a terminal and navigate to the backend directory:

**Windows (PowerShell):**
```powershell
cd "d:\projects\Movie Ticket Reservation System\movie-booking-backend"

# Using Maven Wrapper
.\mvnw spring-boot:run

# OR using installed Maven
mvn spring-boot:run
```

**macOS/Linux:**
```bash
cd "/path/to/movie-booking-backend"

# Using Maven Wrapper
./mvnw spring-boot:run

# OR using installed Maven
mvn spring-boot:run
```

**Expected Output:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

...
Tomcat started on port 8080 (http)
Started MovieBookingApplication in X.XXX seconds
```

**Backend will be running at:** http://localhost:8080

### Step 2: Start the Frontend Development Server

Open a **new terminal** (keep backend running) and navigate to frontend directory:

**Windows/macOS/Linux:**
```bash
cd "movie-booking-frontend"

# Using npm
npm run dev

# OR using yarn
yarn dev

# OR using pnpm
pnpm dev
```

**Expected Output:**
```
  VITE v5.4.21  ready in XXX ms

  ➜  Local:   http://localhost:3000/
  ➜  Network: use --host to expose
```

**Frontend will be running at:** http://localhost:3000

### Step 3: Access the Application

Open your web browser and navigate to:
- **Frontend:** http://localhost:3000
- **Backend API:** http://localhost:8080/api/health

---

## 🔐 Default Admin Credentials

After running the sample data script, you can login with:

### Admin Account
- **Email:** `admin@moviereserve.com`
- **Password:** `admin123`

### Regular User Account
- **Email:** `john.doe@example.com`
- **Password:** `password123`

**Note:** Change these credentials in production!

---

## 📱 Application Features

### User Features
- ✅ Browse movies and theatres
- ✅ View movie details, ratings, and trailers
- ✅ Search and filter movies by genre, language
- ✅ Select seats and book tickets
- ✅ View booking history
- ✅ Cancel bookings
- ✅ User authentication (Register/Login)

### Admin Features
- ✅ Dashboard with analytics
- ✅ Add/Edit/Delete Movies
- ✅ Add/Edit/Delete Theatres
- ✅ Add/Delete Showtimes (auto-creates 100 seats)
- ✅ View all bookings
- ✅ Manage users
- ✅ Real-time data updates

---

## 🐛 Troubleshooting

### Backend Issues

#### Port 8080 Already in Use
```powershell
# Windows - Kill the process
taskkill /F /IM java.exe

# macOS/Linux
lsof -ti:8080 | xargs kill -9
```

#### Database Connection Failed
- Verify PostgreSQL is running
- Check database credentials in `application.properties`
- Ensure database `movie_booking` exists
- Check firewall settings

#### Build Failures
```bash
# Clean and rebuild
mvn clean install -U

# Skip tests if needed
mvn clean install -DskipTests
```

### Frontend Issues

#### Dependencies Installation Failed
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and package-lock.json
rm -rf node_modules package-lock.json

# Reinstall
npm install
```

#### Port 3000 Already in Use
- The Vite dev server will automatically use the next available port (3001, 3002, etc.)
- Or specify a different port in `vite.config.js`:
```javascript
export default defineConfig({
  server: {
    port: 3001
  }
})
```

#### CORS Errors
- Ensure backend is running on port 8080
- Check CORS configuration in `SecurityConfig.java`
- Verify `VITE_API_URL` in `.env` file

### Common Issues

#### "Cannot find module" errors
```bash
# Reinstall dependencies
cd movie-booking-frontend
npm install
```

#### Database migration errors
```bash
# Drop and recreate database
psql -U postgres
DROP DATABASE movie_booking;
CREATE DATABASE movie_booking;
\q

# Run init script again
psql -U postgres -d movie_booking -f movie-booking-backend/src/main/resources/db/init.sql
```

#### JWT Token Errors
- Check `jwt.secret` in `application.properties` (minimum 32 characters)
- Clear browser localStorage and login again

---

## 🔄 Development Workflow

### Making Changes

1. **Backend Changes:**
   - Edit Java files
   - Restart the Spring Boot server (Ctrl+C then rerun)
   - Or use Spring DevTools for auto-reload

2. **Frontend Changes:**
   - Edit React files
   - Vite will auto-reload (Hot Module Replacement)
   - No server restart needed

### Building for Production

**Backend:**
```bash
cd movie-booking-backend
mvn clean package
# JAR file will be in target/ folder
```

**Frontend:**
```bash
cd movie-booking-frontend
npm run build
# Production files will be in dist/ folder
```

---

## 📚 Additional Resources

- **Spring Boot Documentation:** https://spring.io/projects/spring-boot
- **React Documentation:** https://react.dev
- **Vite Documentation:** https://vitejs.dev
- **PostgreSQL Documentation:** https://www.postgresql.org/docs/
- **Tailwind CSS Documentation:** https://tailwindcss.com/docs

---

## 🆘 Need Help?

If you encounter any issues:

1. Check the [Troubleshooting](#troubleshooting) section
2. Verify all prerequisites are installed correctly
3. Ensure database is running and configured properly
4. Check terminal logs for detailed error messages
5. Verify both frontend and backend are running on correct ports

---

## 🎉 Success!

If everything is working correctly, you should see:
- ✅ Backend running on http://localhost:8080
- ✅ Frontend running on http://localhost:3000
- ✅ Able to login with admin credentials
- ✅ Can browse movies and make bookings

**Enjoy your Movie Ticket Reservation System!** 🎬🍿

---

**Created:** October 2025  
**Version:** 1.0.0  
**License:** MIT
