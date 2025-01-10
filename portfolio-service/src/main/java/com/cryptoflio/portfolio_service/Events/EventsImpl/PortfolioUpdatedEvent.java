package com.cryptoflio.portfolio_service.Events.EventsImpl;

import com.cryptoflio.portfolio_service.Events.PortfolioEvent;
import lombok.Getter;

@Getter
public class PortfolioUpdatedEvent extends PortfolioEvent {
    private final Long portfolioId;

    public PortfolioUpdatedEvent(Long portfolioId) {
        this.portfolioId = portfolioId;
    }
}
