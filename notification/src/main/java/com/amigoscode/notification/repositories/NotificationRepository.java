package com.amigoscode.notification.repositories;

import com.amigoscode.notification.domains.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}
