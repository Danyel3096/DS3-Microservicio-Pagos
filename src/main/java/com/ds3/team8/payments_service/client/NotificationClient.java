package com.ds3.team8.payments_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.ds3.team8.payments_service.client.dtos.NotificationRequest;
import com.ds3.team8.payments_service.client.dtos.NotificationResponse;
import com.ds3.team8.payments_service.config.FeignClientInterceptor;

import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notifications-service", configuration = FeignClientInterceptor.class)
public interface NotificationClient {

    @PostMapping("/api/v1/notifications")
    NotificationResponse saveNotification(@RequestBody NotificationRequest notificationRequest);
}
