package com.moviereserve.service;

import com.moviereserve.model.Showtime;
import com.moviereserve.model.Seat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShowtimeService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Showtime> showtimeRowMapper = (rs, rowNum) -> {
        Showtime showtime = new Showtime();
        showtime.setId(rs.getLong("id"));
        showtime.setMovieId(rs.getLong("movie_id"));
        showtime.setScreenId(rs.getLong("screen_id"));
        showtime.setShowDate(rs.getDate("show_date").toLocalDate());
        showtime.setShowTime(rs.getTime("show_time").toLocalTime());
        showtime.setPrice(rs.getBigDecimal("base_price"));
        // Calculate available seats
        try {
            showtime.setAvailableSeats(rs.getInt("available_seats"));
        } catch (SQLException e) {
            showtime.setAvailableSeats(100); // default
        }
        showtime.setStatus(rs.getString("status"));
        showtime.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return showtime;
    };

    private final RowMapper<Showtime> showtimeDetailedRowMapper = (rs, rowNum) -> {
        Showtime showtime = new Showtime();
        showtime.setId(rs.getLong("id"));
        showtime.setMovieId(rs.getLong("movie_id"));
        showtime.setScreenId(rs.getLong("screen_id"));
        showtime.setShowDate(rs.getDate("show_date").toLocalDate());
        showtime.setShowTime(rs.getTime("show_time").toLocalTime());
        showtime.setPrice(rs.getBigDecimal("base_price"));
        // Calculate available seats from seat count
        try {
            showtime.setAvailableSeats(rs.getInt("available_seats"));
        } catch (SQLException e) {
            showtime.setAvailableSeats(100); // default
        }
        showtime.setStatus(rs.getString("status"));
        showtime.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        showtime.setMovieTitle(rs.getString("movie_title"));
        showtime.setTheatreName(rs.getString("theatre_name"));
        showtime.setScreenName(rs.getString("screen_name"));
        showtime.setCity(rs.getString("city"));
        return showtime;
    };

    public List<Showtime> getShowtimesByMovie(Long movieId, String city, LocalDate date) {
        StringBuilder sql = new StringBuilder(
            "SELECT st.*, m.title as movie_title, t.name as theatre_name, " +
            "sc.screen_name, t.city, sc.total_seats as available_seats " +
            "FROM showtimes st " +
            "JOIN movies m ON st.movie_id = m.id " +
            "JOIN screens sc ON st.screen_id = sc.id " +
            "JOIN theatres t ON sc.theatre_id = t.id " +
            "WHERE st.movie_id = ? AND st.status = 'ACTIVE'"
        );

        java.util.ArrayList<Object> params = new java.util.ArrayList<>();
        params.add(movieId);

        if (city != null && !city.isEmpty()) {
            sql.append(" AND t.city = ?");
            params.add(city);
        }

        if (date != null) {
            sql.append(" AND st.show_date = ?");
            params.add(date);
        } else {
            sql.append(" AND st.show_date >= CURRENT_DATE");
        }

        sql.append(" ORDER BY st.show_date, st.show_time");

        return jdbcTemplate.query(sql.toString(), showtimeDetailedRowMapper, params.toArray());
    }

    public Showtime getShowtimeById(Long id) {
        String sql = "SELECT st.*, m.title as movie_title, t.name as theatre_name, " +
                    "sc.screen_name, t.city, sc.total_seats as available_seats " +
                    "FROM showtimes st " +
                    "JOIN movies m ON st.movie_id = m.id " +
                    "JOIN screens sc ON st.screen_id = sc.id " +
                    "JOIN theatres t ON sc.theatre_id = t.id " +
                    "WHERE st.id = ?";
        
        List<Showtime> showtimes = jdbcTemplate.query(sql, showtimeDetailedRowMapper, id);
        return showtimes.isEmpty() ? null : showtimes.get(0);
    }

    public List<Seat> getAvailableSeats(Long showtimeId) {
        String sql = "SELECT s.*, " +
                    "CASE WHEN bs.seat_id IS NOT NULL THEN true ELSE false END as is_booked " +
                    "FROM seats s " +
                    "LEFT JOIN booking_seats bs ON s.id = bs.seat_id " +
                    "LEFT JOIN bookings b ON bs.booking_id = b.id AND b.booking_status != 'CANCELLED' " +
                    "WHERE s.showtime_id = ? " +
                    "ORDER BY s.row_number, s.column_number";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Seat seat = new Seat();
            seat.setId(rs.getLong("id"));
            seat.setShowtimeId(showtimeId);
            seat.setSeatNumber(rs.getString("seat_number"));
            seat.setSeatType(rs.getString("seat_type"));
            seat.setRowNumber(rs.getString("row_number"));
            seat.setColumnNumber(rs.getString("column_number"));
            seat.setPrice(rs.getBigDecimal("price"));
            seat.setStatus(rs.getString("status"));
            return seat;
        }, showtimeId);
    }

    public Showtime createShowtime(Showtime showtime) {
        // Insert showtime
        String sql = "INSERT INTO showtimes (movie_id, screen_id, show_date, show_time, base_price, status) " +
                    "VALUES (?, ?, ?, ?, ?, 'ACTIVE') RETURNING id";
        
        Long id = jdbcTemplate.queryForObject(sql, Long.class,
            showtime.getMovieId(), showtime.getScreenId(), showtime.getShowDate(),
            showtime.getShowTime(), showtime.getPrice());
        
        showtime.setId(id);
        
        // Create seats for this showtime (100 seats: 10 rows x 10 columns)
        createSeatsForShowtime(id, showtime.getPrice());
        
        return showtime;
    }
    
    private void createSeatsForShowtime(Long showtimeId, BigDecimal basePrice) {
        String sql = "INSERT INTO seats (showtime_id, seat_number, row_number, column_number, seat_type, status, price) " +
                    "VALUES (?, ?, ?, ?, ?, 'AVAILABLE', ?)";
        
        List<Object[]> batchArgs = new ArrayList<>();
        
        // Create 10 rows (A-J) with 10 seats each
        for (int i = 1; i <= 10; i++) {
            char rowChar = (char) (64 + i); // A, B, C, etc.
            String rowNumber = String.valueOf(rowChar);
            
            for (int j = 1; j <= 10; j++) {
                String seatNumber = rowChar + String.valueOf(j);
                String columnNumber = String.valueOf(j);
                
                // First 3 rows are premium (1.5x price)
                String seatType = (i <= 3) ? "PREMIUM" : "REGULAR";
                BigDecimal seatPrice = (i <= 3) ? basePrice.multiply(new BigDecimal("1.5")) : basePrice;
                
                batchArgs.add(new Object[]{
                    showtimeId, seatNumber, rowNumber, columnNumber, seatType, seatPrice
                });
            }
        }
        
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    public void updateAvailableSeats(Long showtimeId, int seatsBooked) {
        // Seats are tracked separately in the seats table
        // This method is kept for compatibility but doesn't need to update anything
    }

    public void deleteShowtime(Long id) {
        String sql = "UPDATE showtimes SET status = 'INACTIVE' WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
