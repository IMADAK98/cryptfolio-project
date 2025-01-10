package com.cryptoflio.portfolio_service.ServiceImpl;

import com.cryptoflio.portfolio_service.Repository.PortfolioRepo;
import com.cryptoflio.portfolio_service.ServiceInterface.PortfolioService;
import com.cryptoflio.portfolio_service.Dto.Mapper.PortfolioMapper;
import com.cryptoflio.portfolio_service.Dto.PortfolioResponseDTO;
import com.cryptoflio.portfolio_service.Dto.PortfolioStatsDto;
import com.cryptoflio.portfolio_service.Dto.Requests.PortfolioRequestDTO;
import com.cryptoflio.portfolio_service.Entites.Asset;
import com.cryptoflio.portfolio_service.Entites.Portfolio;
import com.cryptoflio.portfolio_service.Events.EventManager;
import com.cryptoflio.portfolio_service.Exception.NotFound.PortfolioNotFoundException;
import com.cryptoflio.portfolio_service.ServiceInterface.AssetService;
import com.cryptoflio.portfolio_service.ServiceInterface.CalculationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {


    private static final Logger logger = LoggerFactory.getLogger(PortfolioServiceImpl.class);

    private final PortfolioRepo repo;
    private final AssetService assetService;
    private final PortfolioMapper mapper;
    private final EventManager eventManager;
    private final CalculationService calculationService;


    public void updatePortfolio(Long id) throws PortfolioNotFoundException {
        Portfolio portfolio = repo.findById(id).orElseThrow(() -> new PortfolioNotFoundException(id.toString()));


        BigDecimal totalPortfolioValue = BigDecimal.ZERO;
        BigDecimal totalProfitLoss = BigDecimal.ZERO;
        BigDecimal totalCapitalInvested = BigDecimal.ZERO;

        List<Asset> assets = new ArrayList<>(portfolio.getAssets());

        if (assets.isEmpty()) {
            logger.warn("No assets found for portfolio with id: {}", id);
            portfolio.setTotalValue(totalPortfolioValue.doubleValue());
            portfolio.setTotalProfitAndLossAmount(totalProfitLoss.doubleValue());
            repo.save(portfolio);
            return;
        }

        for (Asset asset : assets) {
            if (asset == null) {
                logger.error("Null asset found in portfolio with id: {}", id);
                throw new IllegalStateException("Null asset in portfolio");
            }

            if (asset.getTotalValue() == null || asset.getTotalProfitAndLossAmount() == null) {
                logger.error("Asset with null total value or profit/loss in portfolio with id: {}", id);
                throw new IllegalStateException("Invalid asset data in portfolio");
            }

            totalPortfolioValue = totalPortfolioValue.add(BigDecimal.valueOf(asset.getTotalValue()));
            totalProfitLoss = totalProfitLoss.add(BigDecimal.valueOf(asset.getTotalProfitAndLossAmount()));
            totalCapitalInvested = totalCapitalInvested.add(BigDecimal.valueOf(asset.getInitialValue()));

        }



        portfolio.setTotalValue(totalPortfolioValue.doubleValue());
        portfolio.setTotalProfitAndLossAmount(totalProfitLoss.doubleValue());
        portfolio.setCapitalInvestedAmount(totalCapitalInvested.doubleValue());
        portfolio.setTotalPLPercentage(calculateTotalPlPercentage(portfolio));
        repo.save(portfolio);

        logger.info("Portfolio updated successfully. ID: {}, Total Value: {}, Total Profit/Loss: {}",
                id, totalPortfolioValue, totalProfitLoss);
    }


    private Float calculateTotalPlPercentage(Portfolio portfolio){
        return calculationService.calculateValueChangesPercentage(portfolio.getCapitalInvestedAmount(),portfolio.getTotalValue());
    }


    public List<Portfolio> getAllPortfolios() {
        return repo.findAll();
    }

    public Portfolio getPortfolioByIdAndUpdate(Long portfolioId) {
        Portfolio portfolio = repo.findById(portfolioId).orElseThrow(() -> new RuntimeException("No such portfolio with id " + portfolioId));
        if (portfolio.getAssets().isEmpty()) {
            updatePortfolio(portfolioId);
            return portfolio;
        }
        assetService.updateAllAssets(portfolio.getAssets());
        updatePortfolio(portfolioId);
        return portfolio;
    }


    public Portfolio getPortfolioById(Long portfolioId) {
        return repo.findById(portfolioId).orElseThrow(() -> new PortfolioNotFoundException("No such portfolio with id " + portfolioId));

    }



    public Optional<Portfolio> getPortfolioByUserEmailOptional(String userEmail) {
        return repo.findByUserEmail(userEmail);
    }

    public Portfolio getPortfolioByUserEmail(String userEmail) {
        return repo.findByUserEmail(userEmail).orElseThrow(() -> new PortfolioNotFoundException(userEmail));
    }


    public PortfolioResponseDTO createPortfolio(PortfolioRequestDTO portfolioReq, String userEmail) {
        if (portfolioReq == null || userEmail == null || userEmail.trim() == "") {
            throw new IllegalArgumentException("portfolio req or user email is null");
        }
        Optional<Portfolio> portfolio = repo.findByUserEmail(userEmail);

        if (portfolio.isPresent()) {
            throw new RuntimeException("cannot create portfolio with this user email because a portfolio was already created with it");
        }

        var newPortfolio = Portfolio.builder()
                .userEmail(userEmail)
                .creationDate(new Date())
                .portfolioName(portfolioReq.getPortfolioName())
                .totalValue(0.0)
                .totalProfitAndLossAmount(0.0)
                .assets(new ArrayList<>())
                .build();
        return mapper.toPortfolioDto(repo.save(newPortfolio));
    }


    public Portfolio updatePortfolio(Portfolio portfolio) {
        Portfolio existingPort = repo.findById(portfolio.getId()).orElseThrow(() -> new RuntimeException("error"));
        return repo.save(existingPort);
    }

    public void deletePortfolio(Long id) {
        Portfolio existingPort = repo.findById(id).orElseThrow(() -> new PortfolioNotFoundException(id.toString()));
        repo.delete(existingPort);
    }

    public Boolean hasPortfolio(String userEmail) {
        return repo.findByUserEmail(userEmail).isPresent();
    }

    @Override
    public PortfolioResponseDTO getAndUpdatePortfolioByUserEmail(String userEmail) {

        return getPortfolioByUserEmailOptional(userEmail)
                .map(portfolio -> {
                    List<Asset> assets = portfolio.getAssets();
                    if (assets != null && !assets.isEmpty()) {
                        assetService.updateAllAssets(assets);
                    }
                    updatePortfolio(portfolio.getId());

                    return mapper.toPortfolioDto(portfolio);
                })
                .orElse(null);
    }


    public PortfolioStatsDto getStats(String userEmail) {
        Portfolio portfolio = getPortfolioByUserEmail(userEmail);
        List<Asset> assets = portfolio.getAssets();
        if (assets.isEmpty()) {
            return null;
        }

        Asset topPerformerAsset = assetService.getTopAssetByPortfolioId(portfolio.getId());
        Asset worstPerformerAsset = assetService.getWorstAssetByPortfolioId(portfolio.getId());

        if (topPerformerAsset == null || worstPerformerAsset == null) {
            throw new RuntimeException("No assets found with id ");
        }


        Double topAssetValueChange = calculationService.calculateValueChanges(topPerformerAsset.getInitialValue(), topPerformerAsset.getTotalValue());
        Float topAssetValueChangePercentage = calculationService.calculateValueChangesPercentage(topPerformerAsset.getInitialValue(), topPerformerAsset.getTotalValue());


        Double worstAssetValueChanges = calculationService.calculateValueChanges(worstPerformerAsset.getInitialValue(), worstPerformerAsset.getTotalValue());

        Float valueAssetValueChangesPercentage = calculationService.calculateValueChangesPercentage(worstPerformerAsset.getInitialValue(), worstPerformerAsset.getTotalValue());

        PortfolioStatsDto stats = new PortfolioStatsDto();
        stats.setTopPerformerCoinId(topPerformerAsset.getCoinId());
        stats.setTopPerformerName(topPerformerAsset.getName());
        stats.setTopPerformerSymbol(topPerformerAsset.getSymbol());
        stats.setTopPerformerValueChange(topAssetValueChange);
        stats.setTopPerformerPercentChange(topAssetValueChangePercentage);
        stats.setWorstPerformerCoinId(worstPerformerAsset.getCoinId());
        stats.setWorstPerformerName(worstPerformerAsset.getName());
        stats.setWorstPerformerSymbol(worstPerformerAsset.getSymbol());
        stats.setWorstPerformerValueChange(worstAssetValueChanges);
        stats.setWorstPerformerPercentChange(valueAssetValueChangesPercentage);
        return stats;
    }




    private float calculateAssetPercentage(Asset asset, double totalValue) {
        return (float) (asset.getTotalValue() / totalValue) * 100;
    }
}
