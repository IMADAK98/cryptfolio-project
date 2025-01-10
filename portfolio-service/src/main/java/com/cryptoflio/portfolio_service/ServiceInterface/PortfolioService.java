package com.cryptoflio.portfolio_service.ServiceInterface;

import com.cryptoflio.portfolio_service.Dto.PortfolioResponseDTO;
import com.cryptoflio.portfolio_service.Dto.Requests.PortfolioRequestDTO;
import com.cryptoflio.portfolio_service.Entites.Portfolio;
import com.cryptoflio.portfolio_service.Exception.NotFound.PortfolioNotFoundException;

import java.util.List;
import java.util.Optional;

public interface PortfolioService {

    void updatePortfolio(Long id) throws PortfolioNotFoundException;

    List<Portfolio> getAllPortfolios();

    Portfolio getPortfolioByIdAndUpdate(Long portfolioId);

    Optional<Portfolio> getPortfolioByUserEmailOptional(String userEmail);

    Portfolio getPortfolioByUserEmail(String userEmail) throws PortfolioNotFoundException ;

    PortfolioResponseDTO createPortfolio(PortfolioRequestDTO portfolioReq, String userEmail);

    Portfolio updatePortfolio(Portfolio portfolio);

    void deletePortfolio(Long id);

    Boolean hasPortfolio(String userEmail);


    PortfolioResponseDTO getAndUpdatePortfolioByUserEmail (String userEmail);
}
