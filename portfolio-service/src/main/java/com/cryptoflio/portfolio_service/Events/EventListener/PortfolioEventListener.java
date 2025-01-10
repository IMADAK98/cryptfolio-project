package com.cryptoflio.portfolio_service.Events.EventListener;

import com.cryptoflio.portfolio_service.Entites.Portfolio;
import com.cryptoflio.portfolio_service.Events.EventsImpl.PortfolioUpdatedEvent;
import com.cryptoflio.portfolio_service.Exception.NotFound.PortfolioNotFoundException;
import com.cryptoflio.portfolio_service.ServiceImpl.PortfolioServiceImpl;
import com.cryptoflio.portfolio_service.ServiceImpl.SnapshotServiceImpl;
import com.cryptoflio.portfolio_service.Events.EventManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PortfolioEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioEventListener.class);
    private final SnapshotServiceImpl snapshotServiceImpl;
    private final EventManager eventManager;
    private final PortfolioServiceImpl portfolioService;

    @EventListener
    public void handlePortfolioUpdatedEvent(PortfolioUpdatedEvent event) throws PortfolioNotFoundException {
        logger.info("Received portfolio updated event for portfolio");
        Long portfolioId = event.getPortfolioId();
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        logger.debug("calling snapshot");
        snapshotServiceImpl.createSnapshotIfNeeded(portfolio.getId(), false);
    }
}
