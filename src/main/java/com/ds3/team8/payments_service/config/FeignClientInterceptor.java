package com.ds3.team8.payments_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Configuration
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String USER_ID_HEADER = "X-Authenticated-User-Id";
    private static final String USER_ROLE_HEADER = "X-Authenticated-User-Role";

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (Objects.nonNull(attributes)) {
            String userId = attributes.getRequest().getHeader(USER_ID_HEADER);
            String userRole = attributes.getRequest().getHeader(USER_ROLE_HEADER);

            if (Objects.nonNull(userId) && !userId.isEmpty()) {
                template.header(USER_ID_HEADER, userId);
            }
            if (Objects.nonNull(userRole) && !userRole.isEmpty()) {
                template.header(USER_ROLE_HEADER, userRole);
            }
        }
    }
}
