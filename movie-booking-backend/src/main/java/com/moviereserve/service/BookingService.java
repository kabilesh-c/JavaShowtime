package com.moviereserve.service;

import com.moviereserve.model.Booking;
import com.moviereserve.model.Seat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BookingService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ShowtimeService showtimeService;

    @Autowired
    private AuthService authService;

    @Autowired
    private NotificationService notificationService;

    private final RowMapper<Booking> bookingRowMapper = (rs, rowNum) -> {
        Booking booking = new Booking();
        booking.setId(rs.getLong("id"));
        booking.setUserId(rs.getLong("user_id"));
        booking.setShowtimeId(rs.getLong("showtime_id"));
        booking.setBookingCode(rs.getString("booking_code"));
        booking.setTotalAmount(rs.getBigDecimal("total_amount"));
        booking.setPaymentStatus(rs.getString("payment_status"));
        booking.setPaymentMethod(rs.getString("payment_method"));
        booking.setBookingStatus(rs.getString("booking_status"));
        booking.setQrCode(rs.getString("qr_code"));
        booking.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        booking.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return booking;
    };

    @Transactional
    public Booking createBooking(Long userId, Long showtimeId, List<Long> seatIds, String paymentMethod) {
        try {
            // Verify all seats are available first
            String checkSeatsSql = "SELECT COUNT(*) FROM seats WHERE id = ANY(?) AND status = 'AVAILABLE'";
            Long[] seatArray = seatIds.toArray(new Long[0]);
            Integer availableCount = jdbcTemplate.queryForObject(checkSeatsSql, Integer.class, (Object) seatArray);
            
            if (availableCount == null || availableCount != seatIds.size()) {
                throw new RuntimeException("One or more selected seats are not available");
            }
            
            // Calculate total amount
            String priceSql = "SELECT SUM(price) FROM seats WHERE id = ANY(?)";
            BigDecimal totalAmount = jdbcTemplate.queryForObject(priceSql, BigDecimal.class, (Object) seatArray);

            // Generate unique booking code
            String bookingCode = generateBookingCode();

            // Create booking record
            String bookingSql = "INSERT INTO bookings (user_id, showtime_id, booking_code, total_amount, " +
                              "payment_status, payment_method, booking_status) " +
                              "VALUES (?, ?, ?, ?, 'COMPLETED', ?, 'CONFIRMED') RETURNING id";
            
            Long bookingId = jdbcTemplate.queryForObject(bookingSql, Long.class,
                userId, showtimeId, bookingCode, totalAmount, paymentMethod);

            // Add seats to booking and update seat status in one go
            for (Long seatId : seatIds) {
                // Insert into booking_seats junction table
                String seatSql = "INSERT INTO booking_seats (booking_id, seat_id) VALUES (?, ?)";
                jdbcTemplate.update(seatSql, bookingId, seatId);
            }
            
            // Update all seat statuses to BOOKED
            String updateSeatsSql = "UPDATE seats SET status = 'BOOKED' WHERE id = ANY(?)";
            jdbcTemplate.update(updateSeatsSql, (Object) seatArray);

            // Update available seats
            showtimeService.updateAvailableSeats(showtimeId, seatIds.size());

            // Create transaction record
            createTransaction(userId, bookingId, "PAYMENT", totalAmount, "SUCCESS");

            // Send notification
            notificationService.createNotification(userId, "Booking Confirmed", 
                "Your booking " + bookingCode + " has been confirmed!", "SUCCESS");

            return getBookingById(bookingId);
        } catch (Exception e) {
            throw new RuntimeException("Booking failed: " + e.getMessage());
        }
    }

    private String generateBookingCode() {
        return "BK" + System.currentTimeMillis() + new Random().nextInt(1000);
    }

    public Booking getBookingById(Long id) {
        String sql = "SELECT b.*, m.title as movie_title, t.name as theatre_name, " +
                    "st.show_date, st.show_time, u.email as user_email " +
                    "FROM bookings b " +
                    "JOIN showtimes st ON b.showtime_id = st.id " +
                    "JOIN movies m ON st.movie_id = m.id " +
                    "JOIN screens sc ON st.screen_id = sc.id " +
                    "JOIN theatres t ON sc.theatre_id = t.id " +
                    "JOIN users u ON b.user_id = u.id " +
                    "WHERE b.id = ?";
        
        List<Booking> bookings = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Booking booking = new Booking();
            booking.setId(rs.getLong("id"));
            booking.setUserId(rs.getLong("user_id"));
            booking.setShowtimeId(rs.getLong("showtime_id"));
            booking.setBookingCode(rs.getString("booking_code"));
            booking.setTotalAmount(rs.getBigDecimal("total_amount"));
            booking.setPaymentStatus(rs.getString("payment_status"));
            booking.setPaymentMethod(rs.getString("payment_method"));
            booking.setBookingStatus(rs.getString("booking_status"));
            booking.setQrCode(rs.getString("qr_code"));
            booking.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            booking.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            booking.setMovieTitle(rs.getString("movie_title"));
            booking.setTheatreName(rs.getString("theatre_name"));
            booking.setShowDate(rs.getDate("show_date").toString());
            booking.setShowTime(rs.getTime("show_time").toString());
            booking.setUserEmail(rs.getString("user_email"));
            
            // Get seat numbers
            String seatSql = "SELECT s.seat_number FROM booking_seats bs " +
                           "JOIN seats s ON bs.seat_id = s.id WHERE bs.booking_id = ?";
            List<String> seats = jdbcTemplate.queryForList(seatSql, String.class, booking.getId());
            booking.setSeatNumbers(seats);
            
            return booking;
        }, id);
        
        return bookings.isEmpty() ? null : bookings.get(0);
    }

    public List<Booking> getUserBookings(Long userId) {
        String sql = "SELECT b.*, m.title as movie_title, t.name as theatre_name, " +
                    "st.show_date, st.show_time " +
                    "FROM bookings b " +
                    "JOIN showtimes st ON b.showtime_id = st.id " +
                    "JOIN movies m ON st.movie_id = m.id " +
                    "JOIN screens sc ON st.screen_id = sc.id " +
                    "JOIN theatres t ON sc.theatre_id = t.id " +
                    "WHERE b.user_id = ? " +
                    "ORDER BY b.created_at DESC";
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Booking booking = new Booking();
            booking.setId(rs.getLong("id"));
            booking.setUserId(rs.getLong("user_id"));
            booking.setShowtimeId(rs.getLong("showtime_id"));
            booking.setBookingCode(rs.getString("booking_code"));
            booking.setTotalAmount(rs.getBigDecimal("total_amount"));
            booking.setPaymentStatus(rs.getString("payment_status"));
            booking.setPaymentMethod(rs.getString("payment_method"));
            booking.setBookingStatus(rs.getString("booking_status"));
            booking.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            booking.setMovieTitle(rs.getString("movie_title"));
            booking.setTheatreName(rs.getString("theatre_name"));
            booking.setShowDate(rs.getDate("show_date").toString());
            booking.setShowTime(rs.getTime("show_time").toString());
            
            // Get seat numbers
            String seatSql = "SELECT s.seat_number FROM booking_seats bs " +
                           "JOIN seats s ON bs.seat_id = s.id WHERE bs.booking_id = ?";
            List<String> seats = jdbcTemplate.queryForList(seatSql, String.class, booking.getId());
            booking.setSeatNumbers(seats);
            
            return booking;
        }, userId);
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);
        
        if (booking == null || !booking.getUserId().equals(userId)) {
            throw new RuntimeException("Booking not found or unauthorized");
        }

        if (!"CONFIRMED".equals(booking.getBookingStatus())) {
            throw new RuntimeException("Booking cannot be cancelled");
        }

        // Update booking status
        String sql = "UPDATE bookings SET booking_status = 'CANCELLED', updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        jdbcTemplate.update(sql, bookingId);

        // Restore seat status to AVAILABLE
        String restoreSeatsSql = "UPDATE seats SET status = 'AVAILABLE' WHERE id IN " +
                                "(SELECT seat_id FROM booking_seats WHERE booking_id = ?)";
        jdbcTemplate.update(restoreSeatsSql, bookingId);

        // Restore available seats count
        String countSql = "SELECT COUNT(*) FROM booking_seats WHERE booking_id = ?";
        Integer seatCount = jdbcTemplate.queryForObject(countSql, Integer.class, bookingId);
        showtimeService.updateAvailableSeats(booking.getShowtimeId(), -seatCount);

        // Refund to wallet
        authService.updateWalletBalance(userId, booking.getTotalAmount());

        // Create refund transaction
        createTransaction(userId, bookingId, "REFUND", booking.getTotalAmount(), "SUCCESS");

        // Send notification
        notificationService.createNotification(userId, "Booking Cancelled", 
            "Your booking " + booking.getBookingCode() + " has been cancelled. Amount refunded to wallet.", "INFO");
    }

    private void createTransaction(Long userId, Long bookingId, String type, BigDecimal amount, String status) {
        String sql = "INSERT INTO transactions (user_id, booking_id, transaction_type, amount, status) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql, userId, bookingId, type, amount, status);
    }

    public List<Booking> getAllBookings() {
        String sql = "SELECT b.*, m.title as movie_title, t.name as theatre_name, " +
                    "st.show_date, st.show_time, u.email as user_email " +
                    "FROM bookings b " +
                    "JOIN showtimes st ON b.showtime_id = st.id " +
                    "JOIN movies m ON st.movie_id = m.id " +
                    "JOIN screens sc ON st.screen_id = sc.id " +
                    "JOIN theatres t ON sc.theatre_id = t.id " +
                    "JOIN users u ON b.user_id = u.id " +
                    "ORDER BY b.created_at DESC";
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Booking booking = new Booking();
            booking.setId(rs.getLong("id"));
            booking.setUserId(rs.getLong("user_id"));
            booking.setShowtimeId(rs.getLong("showtime_id"));
            booking.setBookingCode(rs.getString("booking_code"));
            booking.setTotalAmount(rs.getBigDecimal("total_amount"));
            booking.setPaymentStatus(rs.getString("payment_status"));
            booking.setPaymentMethod(rs.getString("payment_method"));
            booking.setBookingStatus(rs.getString("booking_status"));
            booking.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            booking.setMovieTitle(rs.getString("movie_title"));
            booking.setTheatreName(rs.getString("theatre_name"));
            booking.setShowDate(rs.getDate("show_date").toString());
            booking.setShowTime(rs.getTime("show_time").toString());
            booking.setUserEmail(rs.getString("user_email"));
            
            String seatSql = "SELECT s.seat_number FROM booking_seats bs " +
                           "JOIN seats s ON bs.seat_id = s.id WHERE bs.booking_id = ?";
            List<String> seats = jdbcTemplate.queryForList(seatSql, String.class, booking.getId());
            booking.setSeatNumbers(seats);
            
            return booking;
        });
    }
}
