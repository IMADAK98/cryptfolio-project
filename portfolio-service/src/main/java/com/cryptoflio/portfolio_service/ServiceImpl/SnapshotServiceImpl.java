package com.cryptoflio.portfolio_service.ServiceImpl;

import com.cryptoflio.portfolio_service.Repository.SnapshotRepository;
import com.cryptoflio.portfolio_service.Entites.Asset;
import com.cryptoflio.portfolio_service.Entites.Period;
import com.cryptoflio.portfolio_service.Entites.Portfolio;
import com.cryptoflio.portfolio_service.Entites.PortfolioSnapshot;
import com.cryptoflio.portfolio_service.ServiceInterface.AssetService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SnapshotServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(SnapshotServiceImpl.class);

    private final SnapshotRepository repo;

    private final PortfolioServiceImpl portfolioService;

    private final AssetService assetService;

    @Autowired
    private Environment env;


    public List<PortfolioSnapshot> getPortfolioSnapshotsByUserEmail(String userEmail, Period period) {
        Portfolio portfolio = portfolioService.getPortfolioByUserEmail(userEmail);
        if (portfolio == null) {
            logger.error("Portfolio not found for user: {}", userEmail);
            return Collections.emptyList();
        }

        List<PortfolioSnapshot> snapshotList = portfolio.getSnapshot();

        if (snapshotList == null || snapshotList.isEmpty()) {
            logger.error("No snapshots found for portfolio: {}", portfolio.getId());
            return Collections.emptyList();

        }

        // Get current date
        Date today = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        switch (period) {
            case HOUR:
                calendar.add(Calendar.HOUR_OF_DAY , -1);
                break;
            case DAY:
                // Subtract 24 hours
                calendar.add(Calendar.HOUR_OF_DAY, -24);
                break;
            case WEEK:
                // Subtract 7 days
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                break;
            case MONTH:
                // Subtract 30 days (or use calendar.add(Calendar.MONTH, -1); for exact 1 month)
                calendar.add(Calendar.DAY_OF_YEAR, -30);
                break;
            case ALL_TIME:
                // No filtering for all time, return the complete list
                return snapshotList;
            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }

        // Get the cutoff date
        Date cutoffDate = calendar.getTime();

        // Filter the snapshot list based on the cutoff date
        List<PortfolioSnapshot> filteredSnapshots = snapshotList.stream()
                .filter(snapshot -> snapshot.getTimestamp().after(cutoffDate))
                .toList();

        return filteredSnapshots;


    }


    public void createSnapshotIfNeeded(Long portfolioId, boolean shouldUpdateAssetsAndPortfolio) {
        Date lastSnapshotTime = repo.findTopByPortfolioIdOrderByTimestampDesc(portfolioId)
                .map(PortfolioSnapshot::getTimestamp)
                .orElse(null);

        if (shouldCreateSnapshot(lastSnapshotTime)) {
            if (shouldUpdateAssetsAndPortfolio) {
                // Only update the portfolio's assets and create the snapshot once
                updatePortfolioAssetsAndCreateSnapshot(portfolioId);
            } else {
                // Create snapshot directly without updating assets
                createSnapshot(portfolioId);
            }
        }
    }

    private boolean shouldCreateSnapshot(Date lastSnapshotTime) {
        if (lastSnapshotTime == null) {
            return true;
        }
        long snapshotIntervalMinutes = Long.parseLong(env.getProperty("snapshot.interval.minutes", "30"));
        long minutesSinceLastSnapshot = (new Date().getTime() - lastSnapshotTime.getTime()) / (60 * 1000);
        return minutesSinceLastSnapshot >= snapshotIntervalMinutes;
    }


    private void updatePortfolioAssetsAndCreateSnapshot(Long portfolioId) {

        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        logger.debug("Updating assets and creating snapshot for portfolio: {}", portfolio.getId());

        List<Asset> assets = portfolio.getAssets();
        if (assets != null && !assets.isEmpty()) {
            assetService.updateAllAssets(assets);
            portfolioService.updatePortfolio(portfolio.getId());
        }
        buildNewSnapshot(portfolio);
        logger.info("Assets updated and snapshot created successfully for portfolio: {}", portfolio.getId());
    }


    public void createSnapshot(Long portfolioId) {
        logger.debug("Creating snapshot for portfolio: {}", portfolioId);
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        buildNewSnapshot(portfolio);
        logger.info("Snapshot created successfully for portfolio: {}", portfolio.getId());
    }

    private void buildNewSnapshot(Portfolio portfolio) {
        PortfolioSnapshot snapshot = PortfolioSnapshot.builder()
                .totalValue(portfolio.getTotalValue())
                .timestamp(new Date())
                .portfolio(portfolio)
                .build();
        repo.save(snapshot);
    }


    @Scheduled(cron = "${snapshot.cron.expression}")
    public void createScheduledSnapshots() {
        logger.debug("Starting scheduled snapshot creation");
        List<Portfolio> portfolios = portfolioService.getAllPortfolios();
        for (Portfolio portfolio : portfolios) {
            createSnapshotIfNeeded(portfolio.getId(), true);
        }
        logger.info("Completed scheduled snapshot creation");
    }
}
