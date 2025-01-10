package com.cryptoflio.portfolio_service.Dto.Mapper;

import com.cryptoflio.portfolio_service.Dto.PortfolioResponseDTO;
import com.cryptoflio.portfolio_service.Entites.Portfolio;
import org.springframework.stereotype.Component;

@Component
public class PortfolioMapper {

    public PortfolioResponseDTO toPortfolioDto(Portfolio portfolio) {
        return new PortfolioResponseDTO()
                .setId(portfolio.getId())
                .setUserEmail(portfolio.getUserEmail())
                .setPortfolioName(portfolio.getPortfolioName())
                .setCreationDate(portfolio.getCreationDate())
                .setTotalValue(portfolio.getTotalValue())
                .setTotalCapitalInvested(portfolio.getCapitalInvestedAmount())
                .setTotalProfitAndLossAmount(portfolio.getTotalProfitAndLossAmount())
                .setAssets(portfolio.getAssets())
                .setTotalPlPercent(portfolio.getTotalPLPercentage());
    }

}
