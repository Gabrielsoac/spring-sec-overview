package com.gabrielsoac.overview_security.config;

import lombok.Builder;

@Builder
public record JWTUserData(
    long userId,
    String email
) {}
