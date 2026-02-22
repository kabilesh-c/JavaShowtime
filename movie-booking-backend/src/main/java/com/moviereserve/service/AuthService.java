package com.moviereserve.service;

import com.moviereserve.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private final RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setFullName(rs.getString("full_name"));
            user.setPhone(rs.getString("phone"));
            user.setRole(rs.getString("role"));
            user.setWalletBalance(rs.getBigDecimal("wallet_balance"));
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return user;
        }
    };

    public Map<String, Object> register(String email, String password, String fullName, String phone, String role) {
        try {
            // Check if user already exists
            String checkSql = "SELECT COUNT(*) FROM users WHERE email = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, email);
            
            if (count > 0) {
                throw new RuntimeException("Email already registered");
            }

            // Hash password
            String hashedPassword = passwordEncoder.encode(password);

            // Use provided role or default to 'USER'
            String userRole = (role != null && !role.isEmpty()) ? role : "USER";

            // Insert user
            String insertSql = "INSERT INTO users (email, password, full_name, phone, role, wallet_balance) " +
                             "VALUES (?, ?, ?, ?, ?, 0.00) RETURNING id";
            
            Long userId = jdbcTemplate.queryForObject(insertSql, Long.class, 
                email, hashedPassword, fullName, phone, userRole);

            // Generate JWT token
            String token = jwtService.generateToken(email, userRole, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", userId);
            response.put("email", email);
            response.put("fullName", fullName);
            response.put("role", userRole);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    public Map<String, Object> login(String email, String password) {
        try {
            String sql = "SELECT * FROM users WHERE email = ?";
            List<User> users = jdbcTemplate.query(sql, userRowMapper, email);

            if (users.isEmpty()) {
                throw new RuntimeException("Invalid email or password");
            }

            User user = users.get(0);

            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("Invalid email or password");
            }

            String token = jwtService.generateToken(user.getEmail(), user.getRole(), user.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("fullName", user.getFullName());
            response.put("role", user.getRole());
            response.put("walletBalance", user.getWalletBalance());

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    public User getUserById(Long userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, userId);
        return users.isEmpty() ? null : users.get(0);
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, email);
        return users.isEmpty() ? null : users.get(0);
    }

    public void updateWalletBalance(Long userId, BigDecimal amount) {
        String sql = "UPDATE users SET wallet_balance = wallet_balance + ? WHERE id = ?";
        jdbcTemplate.update(sql, amount, userId);
    }
}
