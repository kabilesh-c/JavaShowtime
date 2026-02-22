package com.moviereserve.controller;

import com.moviereserve.model.Showtime;
import com.moviereserve.model.Seat;
import com.moviereserve.service.ShowtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@CrossOrigin(origins = "*")
public class ShowtimeController {

    @Autowired
    private ShowtimeService showtimeService;

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Showtime>> getShowtimesByMovie(
            @PathVariable Long movieId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<Showtime> showtimes = showtimeService.getShowtimesByMovie(movieId, city, date);
        return ResponseEntity.ok(showtimes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getShowtimeById(@PathVariable Long id) {
        Showtime showtime = showtimeService.getShowtimeById(id);
        if (showtime == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(showtime);
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<Seat>> getAvailableSeats(@PathVariable Long id) {
        List<Seat> seats = showtimeService.getAvailableSeats(id);
        return ResponseEntity.ok(seats);
    }
}
