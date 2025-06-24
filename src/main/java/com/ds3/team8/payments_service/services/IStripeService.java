package com.ds3.team8.payments_service.services;

import com.ds3.team8.payments_service.dtos.StripeRequest;
import com.ds3.team8.payments_service.dtos.StripeResponse;

public interface IStripeService {
    StripeResponse createCheckoutSession(StripeRequest stripeRequest, Long userId);
}
