# 🎬 Movie Ticket Reservation System

A modern, full stack movie ticket booking platform built with React, Spring Boot, and Supabase PostgreSQL.

**This project was created to test and demonstrate my knowledge in Java, Spring Boot, and the associated technology stack. It serves as a practical implementation for learning and practicing these technologies, utilizing strictly the tech stack mentioned below to build a functional, feature rich application.**

## ✨ Features

### 🎟️ User Features
- Browse and search movies by genre, language, and city
- View detailed movie information with cast, ratings, and showtimes
- Interactive seat selection with real-time availability
- Multiple payment options (UPI, Card, Wallet)
- Booking history and QR code tickets
- Cancel bookings with automatic refunds to wallet
- Email notifications for booking confirmations
- Dark/Light mode toggle
- Responsive mobile-first design

### 🛠️ Admin Features
- Comprehensive dashboard with analytics
- Manage movies (add, edit, delete)
- Manage theatres and screens
- Create and manage showtimes
- View all bookings and user data
- Daily sales reports and revenue charts
- User management

## 🚀 Tech Stack

### Frontend
- **React 18** - Modern UI library
- **Tailwind CSS** - Utility-first CSS framework
- **Framer Motion** - Animation library
- **React Router** - Client-side routing
- **Axios** - HTTP client
- **React Hot Toast** - Toast notifications
- **Recharts** - Analytics charts
- **Vite** - Build tool

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.2** - Application framework
- **Spring Security** - Authentication & authorization
- **JDBC Template** - Database connectivity
- **JWT** - Token-based authentication
- **Maven** - Build tool

### Database
- **Supabase PostgreSQL** - Cloud database
- **Transaction Pooler** (port 6543)
- SSL required connection

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.8+**
- **Node.js 18+** and npm
- **Supabase account** (free tier works)
- **Git**

## 🔧 Setup Instructions

### 1. Clone the Repository

```powershell
git clone https://github.com/kabilesh-c/JavaShowtime.git
cd "Movie Ticket Reservation System"
```

### 2. Database Setup (Supabase)

1. Create a free account at [supabase.com](https://supabase.com)
2. Create a new project
3. Note down:
   - Project URL (e.g., `db.abcdefg.supabase.co`)
   - Database password
   - Project reference ID

The database tables will be **automatically created** when you first run the backend!

### 3. Backend Setup

Navigate to backend directory:

```powershell
cd movie-booking-backend
```

Update `src/main/resources/application.properties`:

```properties
# Replace with your Supabase details
spring.datasource.url=jdbc:postgresql://db.YOUR_PROJECT_REF.supabase.co:6543/postgres?sslmode=require
spring.datasource.username=postgres
spring.datasource.password=YOUR_DB_PASSWORD

# Update JWT secret (use a strong 256-bit key)
jwt.secret=your-super-secret-jwt-key-change-this-in-production

# Configure email (optional, for booking confirmations)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

Build and run:

```powershell
mvn clean install
mvn spring-boot:run
```

Backend will start on `http://localhost:8080`

### 4. Frontend Setup

Open a new terminal and navigate to frontend directory:

```powershell
cd movie-booking-frontend
```

Install dependencies:

```powershell
npm install
```

Create `.env` file in `movie-booking-frontend` directory:

```env
VITE_API_URL=http://localhost:8080/api
```

Start development server:

```powershell
npm run dev
```

Frontend will start on `http://localhost:3000`

## 🎯 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `GET /api/auth/me` - Get current user

### Movies
- `GET /api/movies` - Get all movies
- `GET /api/movies/{id}` - Get movie by ID
- `GET /api/movies/search` - Search movies
- `GET /api/movies/genres` - Get all genres
- `GET /api/movies/languages` - Get all languages

### Theatres
- `GET /api/theatres` - Get all theatres
- `GET /api/theatres/{id}` - Get theatre by ID
- `GET /api/theatres/city/{city}` - Get theatres by city
- `GET /api/theatres/cities` - Get all cities

### Showtimes
- `GET /api/showtimes/movie/{movieId}` - Get showtimes for movie
- `GET /api/showtimes/{id}` - Get showtime by ID
- `GET /api/showtimes/{id}/seats` - Get available seats

### Bookings (Protected)
- `POST /api/bookings` - Create booking
- `GET /api/bookings/user` - Get user bookings
- `GET /api/bookings/{id}` - Get booking by ID
- `POST /api/bookings/{id}/cancel` - Cancel booking

### Admin (Admin Only)
- `POST /api/admin/movies` - Create movie
- `PUT /api/admin/movies/{id}` - Update movie
- `DELETE /api/admin/movies/{id}` - Delete movie
- `GET /api/admin/analytics/dashboard` - Get dashboard analytics
- `GET /api/admin/bookings` - Get all bookings
- `GET /api/admin/users` - Get all users

## 🎨 Default Theme

- **Background**: `#0D1117` (deep charcoal)
- **Primary**: `#FF3C78` (vibrant magenta pink)
- **Secondary**: `#FFD166` (gold accent)
- **Card**: `#161B22`
- **Text**: `#E6EDF3`

## 🧪 Testing the Application

### Create Admin User

After starting the backend, insert an admin user in Supabase SQL Editor:

```sql
INSERT INTO users (email, password, full_name, phone, role, wallet_balance)
VALUES (
  'admin@cinema.com',
  -- Password: admin123 (hashed with BCrypt)
  '$2a$10$xyzAbcDef...', 
  'Admin User',
  '1234567890',
  'ADMIN',
  0.00
);
```

Or use the registration endpoint and manually update the role in database.

### Test Workflow

1. **Register** a new user at `/register`
2. **Login** with credentials
3. **Browse** movies on home page
4. Click a movie to see **details and showtimes**
5. Select a showtime and **choose seats**
6. Complete **payment** (simulated)
7. View **booking confirmation** with QR code
8. Check **My Bookings** page

## 🚢 Deployment

### Backend (Railway/Render)

1. Push code to GitHub
2. Connect repository to Railway/Render
3. Set environment variables:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_PASSWORD`
   - `JWT_SECRET`
4. Deploy

### Frontend (Vercel)

1. Push frontend code to GitHub
2. Import project in Vercel
3. Set build command: `npm run build`
4. Set output directory: `dist`
5. Add environment variable: `VITE_API_URL=your-backend-url`
6. Deploy

## 📦 Project Structure

```
Movie Ticket Reservation System/
├── movie-booking-backend/
│   ├── src/main/java/com/moviereserve/
│   │   ├── config/          # Security & DB config
│   │   ├── controller/      # REST controllers
│   │   ├── model/          # Entity models
│   │   ├── service/        # Business logic
│   │   └── security/       # JWT authentication
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
└── movie-booking-frontend/
    ├── src/
    │   ├── components/     # Reusable components
    │   ├── pages/         # Page components
    │   ├── context/       # React context
    │   ├── utils/         # API & utilities
    │   └── App.jsx
    ├── index.html
    ├── tailwind.config.js
    └── package.json
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👨‍💻 Author

**Kabilesh C**
📧 [kabileshc.dev@gmail.com](mailto:kabileshc.dev@gmail.com)

## 🙏 Acknowledgments

- Supabase for awesome database hosting
- Tailwind CSS for beautiful styling
- Framer Motion for smooth animations
- Spring Boot community

---

**Happy Coding! 🎬🍿**
