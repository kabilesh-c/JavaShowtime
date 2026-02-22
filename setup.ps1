# Movie Ticket Reservation System - Quick Start Guide

Write-Host "🎬 Movie Ticket Reservation System - Setup" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Check if .env exists in frontend
if (-Not (Test-Path "movie-booking-frontend\.env")) {
    Write-Host "⚠️  Creating .env file for frontend..." -ForegroundColor Yellow
    Copy-Item "movie-booking-frontend\.env.example" "movie-booking-frontend\.env"
    Write-Host "✅ Created .env file. Default API URL: http://localhost:8080/api" -ForegroundColor Green
} else {
    Write-Host "✅ .env file already exists" -ForegroundColor Green
}

Write-Host ""
Write-Host "📋 Pre-Flight Checklist:" -ForegroundColor Cyan
Write-Host "  1. Java 17+ installed" -ForegroundColor White
Write-Host "  2. Maven installed" -ForegroundColor White
Write-Host "  3. Node.js 18+ installed" -ForegroundColor White
Write-Host "  4. Supabase database credentials configured" -ForegroundColor Yellow
Write-Host ""

$response = Read-Host "Have you updated application.properties with Supabase credentials? (y/n)"

if ($response -ne "y") {
    Write-Host ""
    Write-Host "⚠️  Please update the following file first:" -ForegroundColor Yellow
    Write-Host "   movie-booking-backend\src\main\resources\application.properties" -ForegroundColor White
    Write-Host ""
    Write-Host "   Update these values:" -ForegroundColor White
    Write-Host "   - spring.datasource.url" -ForegroundColor White
    Write-Host "   - spring.datasource.username" -ForegroundColor White
    Write-Host "   - spring.datasource.password" -ForegroundColor White
    Write-Host ""
    exit
}

Write-Host ""
Write-Host "🚀 Starting Setup Process..." -ForegroundColor Cyan
Write-Host ""

# Frontend setup
Write-Host "📦 Installing frontend dependencies..." -ForegroundColor Cyan
Set-Location "movie-booking-frontend"

if (-Not (Test-Path "node_modules")) {
    npm install
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Frontend dependencies installed" -ForegroundColor Green
    } else {
        Write-Host "❌ Failed to install frontend dependencies" -ForegroundColor Red
        Set-Location ".."
        exit
    }
} else {
    Write-Host "✅ Frontend dependencies already installed" -ForegroundColor Green
}

Set-Location ".."

Write-Host ""
Write-Host "✅ Setup Complete!" -ForegroundColor Green
Write-Host ""
Write-Host "To start the application:" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Backend:" -ForegroundColor Yellow
Write-Host "    cd movie-booking-backend" -ForegroundColor White
Write-Host "    mvn spring-boot:run" -ForegroundColor White
Write-Host ""
Write-Host "  Frontend (in a new terminal):" -ForegroundColor Yellow
Write-Host "    cd movie-booking-frontend" -ForegroundColor White
Write-Host "    npm run dev" -ForegroundColor White
Write-Host ""
Write-Host "  Access the app at: http://localhost:5173" -ForegroundColor Green
Write-Host "  API runs at: http://localhost:8080" -ForegroundColor Green
Write-Host ""
Write-Host "📚 For detailed verification, see: VERIFICATION_REPORT.md" -ForegroundColor Cyan
Write-Host ""
