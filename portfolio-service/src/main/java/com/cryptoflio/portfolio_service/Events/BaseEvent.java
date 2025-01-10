package com.cryptoflio.portfolio_service.Events;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public abstract class BaseEvent {

    private final UUID eventId;
    private final LocalDateTime timestamp;
}
