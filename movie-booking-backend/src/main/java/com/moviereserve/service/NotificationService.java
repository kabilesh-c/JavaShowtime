package com.moviereserve.service;

import com.moviereserve.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createNotification(Long userId, String title, String message, String type) {
        String sql = "INSERT INTO notifications (user_id, title, message, type, is_read) " +
                    "VALUES (?, ?, ?, ?, false)";
        jdbcTemplate.update(sql, userId, title, message, type);
    }

    public List<Notification> getUserNotifications(Long userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Notification notification = new Notification();
            notification.setId(rs.getLong("id"));
            notification.setUserId(rs.getLong("user_id"));
            notification.setTitle(rs.getString("title"));
            notification.setMessage(rs.getString("message"));
            notification.setType(rs.getString("type"));
            notification.setIsRead(rs.getBoolean("is_read"));
            notification.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return notification;
        }, userId);
    }

    public void markAsRead(Long notificationId) {
        String sql = "UPDATE notifications SET is_read = true WHERE id = ?";
        jdbcTemplate.update(sql, notificationId);
    }

    public void markAllAsRead(Long userId) {
        String sql = "UPDATE notifications SET is_read = true WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }
}
