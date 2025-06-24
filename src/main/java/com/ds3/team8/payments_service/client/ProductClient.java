package com.ds3.team8.payments_service.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.ds3.team8.payments_service.client.dtos.StockRequest;
import com.ds3.team8.payments_service.config.FeignClientInterceptor;


@FeignClient(name = "products-service", configuration = FeignClientInterceptor.class)
public interface ProductClient {

    @PostMapping("/api/v1/products/validate-stock")
    Boolean validateStock(@RequestBody List<StockRequest> requests);

    @PostMapping("/api/v1/products/update-stock")
    void updateStock(@RequestBody List<StockRequest> requests);

}