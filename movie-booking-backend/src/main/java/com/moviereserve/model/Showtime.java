package com.moviereserve.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {
    private Long id;
    private Long movieId;
    private Long screenId;
    private LocalDate showDate;
    private LocalTime showTime;
    private BigDecimal price;
    private Integer availableSeats;
    private String status;
    private LocalDateTime createdAt;
    
    // Additional fields for response
    private String movieTitle;
    private String theatreName;
    private String screenName;
    private String city;
}
