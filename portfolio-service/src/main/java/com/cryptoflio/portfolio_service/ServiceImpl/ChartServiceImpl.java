package com.cryptoflio.portfolio_service.ServiceImpl;

import com.cryptoflio.portfolio_service.ServiceInterface.PortfolioService;
import com.cryptoflio.portfolio_service.Dto.BarChartResponseDTO;
import com.cryptoflio.portfolio_service.Dto.Mapper.ChartsMapper;
import com.cryptoflio.portfolio_service.Dto.PieChartResponseDTO;
import com.cryptoflio.portfolio_service.Entites.Asset;
import com.cryptoflio.portfolio_service.Entites.ChartType;
import com.cryptoflio.portfolio_service.Entites.Portfolio;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChartServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ChartServiceImpl.class);

    private final PortfolioService portfolioService;
    private final CalculationServiceImpl calculationService;
    private final ChartsMapper chartsMapper;

    public List<PieChartResponseDTO> getPortfolioAssetsForPieChart(String userEmail) {
        Portfolio portfolio = portfolioService.getPortfolioByUserEmail(userEmail);
        if (portfolio.getAssets() == null || portfolio.getAssets().isEmpty()) {
            logger.error("empty assets for portfolio");
            return Collections.emptyList();
        }

        List<PieChartResponseDTO> response = createPieResponses(portfolio);

        return response;
    }

    private List<PieChartResponseDTO> createPieResponses(Portfolio portfolio) {
        double portfolioTotalVal = portfolio.getAssets().stream()
                .mapToDouble(Asset::getTotalValue)
                .sum();


        return portfolio.getAssets().stream()
                .map(asset -> new PieChartResponseDTO(asset.getName(), calculationService.calculateAssetPercentage(asset, portfolioTotalVal)))
                .collect(Collectors.toList());
    }

    public List<BarChartResponseDTO> getBarChartData(String userEmail, ChartType chartType) {
        Portfolio portfolio = portfolioService.getPortfolioByUserEmail(userEmail);
        if (portfolio.getAssets() == null || portfolio.getAssets().isEmpty()) {
            logger.error("empty assets for portfolio");
            return Collections.emptyList();
        }


        return switch (chartType) {
            case VALUE -> getBarChartDataByValue(portfolio);
            case QUANTITY -> getBarChartDataByQuantity(portfolio);
        };


    }

    private List<BarChartResponseDTO> getBarChartDataByValue(Portfolio portfolio) {
        return portfolio.getAssets().stream()
                .sorted(Comparator.comparingDouble(Asset::getTotalValue).reversed())
                .map(chartsMapper::mapToBarChartResponseDTO)
                .toList();
    }


    private List<BarChartResponseDTO> getBarChartDataByQuantity(Portfolio portfolio) {
        return portfolio.getAssets().stream()
                .sorted(Comparator.comparingDouble(Asset::getTotalQuantity).reversed())
                .map(chartsMapper::mapToBarChartResponseDTO)
                .toList();
    }


}
