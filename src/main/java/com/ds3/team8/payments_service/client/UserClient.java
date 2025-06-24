package com.ds3.team8.payments_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ds3.team8.payments_service.client.dtos.UserResponse;
import com.ds3.team8.payments_service.config.FeignClientInterceptor;

@FeignClient(name = "users-service", configuration = FeignClientInterceptor.class)
public interface UserClient {

    @GetMapping("/api/v1/users/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);
}
