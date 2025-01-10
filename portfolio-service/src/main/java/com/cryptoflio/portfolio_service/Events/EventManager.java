package com.cryptoflio.portfolio_service.Events;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventManager {

    private final ApplicationEventPublisher eventPublisher;

    public void publishEvent(Object event) {
        eventPublisher.publishEvent(event);
    }
}
