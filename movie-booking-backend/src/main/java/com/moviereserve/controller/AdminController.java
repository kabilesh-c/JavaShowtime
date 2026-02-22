package com.moviereserve.controller;

import com.moviereserve.model.*;
import com.moviereserve.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private TheatreService theatreService;

    @Autowired
    private ShowtimeService showtimeService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Movie Management
    @PostMapping("/movies")
    public ResponseEntity<?> createMovie(@RequestBody Movie movie) {
        try {
            Movie created = movieService.createMovie(movie);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/movies/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        try {
            Movie updated = movieService.updateMovie(id, movie);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/movies/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        try {
            movieService.deleteMovie(id);
            return ResponseEntity.ok(Map.of("message", "Movie deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Theatre Management
    @PostMapping("/theatres")
    public ResponseEntity<?> createTheatre(@RequestBody Theatre theatre) {
        try {
            Theatre created = theatreService.createTheatre(theatre);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/theatres/{id}")
    public ResponseEntity<?> updateTheatre(@PathVariable Long id, @RequestBody Theatre theatre) {
        try {
            Theatre updated = theatreService.updateTheatre(id, theatre);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/theatres/{id}")
    public ResponseEntity<?> deleteTheatre(@PathVariable Long id) {
        try {
            theatreService.deleteTheatre(id);
            return ResponseEntity.ok(Map.of("message", "Theatre deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/theatres/{id}/screens")
    public ResponseEntity<?> createScreen(@PathVariable Long id, @RequestBody Screen screen) {
        try {
            screen.setTheatreId(id);
            Screen created = theatreService.createScreen(screen);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Showtime Management
    @PostMapping("/showtimes")
    public ResponseEntity<?> createShowtime(@RequestBody Showtime showtime) {
        try {
            Showtime created = showtimeService.createShowtime(showtime);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/showtimes/{id}")
    public ResponseEntity<?> deleteShowtime(@PathVariable Long id) {
        try {
            showtimeService.deleteShowtime(id);
            return ResponseEntity.ok(Map.of("message", "Showtime deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Booking Management
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // Dashboard Analytics
    @GetMapping("/analytics/dashboard")
    public ResponseEntity<?> getDashboardAnalytics() {
        try {
            Map<String, Object> analytics = new HashMap<>();

            // Total counts
            analytics.put("totalMovies", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM movies WHERE status = 'ACTIVE'", Integer.class));
            
            analytics.put("totalTheatres", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM theatres WHERE status = 'ACTIVE'", Integer.class));
            
            analytics.put("totalBookings", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM bookings WHERE booking_status = 'CONFIRMED'", Integer.class));
            
            analytics.put("totalUsers", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users", Integer.class));

            // Revenue
            analytics.put("totalRevenue", jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(total_amount), 0) FROM bookings WHERE payment_status = 'COMPLETED'", 
                java.math.BigDecimal.class));

            analytics.put("todayRevenue", jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(total_amount), 0) FROM bookings " +
                "WHERE payment_status = 'COMPLETED' AND DATE(created_at) = CURRENT_DATE", 
                java.math.BigDecimal.class));

            // Daily sales (last 7 days)
            String dailySalesSql = "SELECT DATE(created_at) as date, COUNT(*) as bookings, SUM(total_amount) as revenue " +
                                  "FROM bookings WHERE created_at >= CURRENT_DATE - INTERVAL '7 days' " +
                                  "AND payment_status = 'COMPLETED' GROUP BY DATE(created_at) ORDER BY date";
            
            List<Map<String, Object>> dailySales = jdbcTemplate.queryForList(dailySalesSql);
            analytics.put("dailySales", dailySales);

            // Top movies
            String topMoviesSql = "SELECT m.title, COUNT(b.id) as bookings " +
                                 "FROM movies m " +
                                 "LEFT JOIN showtimes st ON m.id = st.movie_id " +
                                 "LEFT JOIN bookings b ON st.id = b.showtime_id AND b.booking_status = 'CONFIRMED' " +
                                 "GROUP BY m.id, m.title " +
                                 "ORDER BY bookings DESC LIMIT 5";
            
            List<Map<String, Object>> topMovies = jdbcTemplate.queryForList(topMoviesSql);
            analytics.put("topMovies", topMovies);

            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // User Management
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            String sql = "SELECT id, email, full_name, phone, role, wallet_balance, created_at " +
                        "FROM users ORDER BY created_at DESC";
            List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
