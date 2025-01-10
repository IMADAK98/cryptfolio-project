package com.cryptoflio.portfolio_service.Controller;

import com.cryptoflio.portfolio_service.ServiceImpl.PortfolioServiceImpl;
import com.cryptoflio.portfolio_service.Dto.BaseApiResponse;
import com.cryptoflio.portfolio_service.Dto.PortfolioResponseDTO;
import com.cryptoflio.portfolio_service.Dto.PortfolioStatsDto;
import com.cryptoflio.portfolio_service.Dto.Requests.PortfolioRequestDTO;
import com.cryptoflio.portfolio_service.Entites.Portfolio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PortfolioController {
    private static final Logger logger = LoggerFactory.getLogger(PortfolioController.class);

    private final PortfolioServiceImpl service;

    @GetMapping("/portfolio/{id}")
    public ResponseEntity<Portfolio> getPortfolioById(@PathVariable Long id) {

        return ResponseEntity.ok(service.getPortfolioByIdAndUpdate(id));
    }

    @GetMapping("/portfolios")
    public ResponseEntity<List<Portfolio>> getPortfolios() {
        return ResponseEntity.ok(service.getAllPortfolios());
    }


    @GetMapping("/portfolio-by-email")
    public ResponseEntity<BaseApiResponse<PortfolioResponseDTO>> getPortfolioByUserEmail(@RequestHeader("loggedInUser") String userEmail) {
        try {
            PortfolioResponseDTO portfolioResponse = service.getAndUpdatePortfolioByUserEmail(userEmail);

            if (portfolioResponse != null) {
                return ResponseEntity.ok(new BaseApiResponse<>(portfolioResponse, "Portfolio found successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseApiResponse<>(null, "Portfolio not found for email: " + userEmail));
            }
        } catch (Exception e) {
            logger.error("Error processing portfolio update for user: {}", userEmail, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseApiResponse<>(null, "Error processing portfolio update"));
        }
    }

    @GetMapping("/portfolio/statistics")
    public ResponseEntity<BaseApiResponse<PortfolioStatsDto>> getPortfolioStatsByUserEmail(@RequestHeader("loggedInUser") String userEmail) {

        return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>(service.getStats(userEmail), "found"));
    }


    @GetMapping("/has-portfolio")
    public ResponseEntity<BaseApiResponse<Boolean>> hasPortfolio(@RequestHeader("loggedInUser") String userEmail) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("user email cannot be null");
        }
        return ResponseEntity.ok(new BaseApiResponse<>(service.hasPortfolio(userEmail), ""));
    }


    @PostMapping("/portfolio")
    public ResponseEntity<BaseApiResponse<PortfolioResponseDTO>> createPortfolio(@RequestBody @Valid PortfolioRequestDTO portfolioReq, @RequestHeader("loggedInUser") String userEmail) {
        return ResponseEntity.ok(new BaseApiResponse<>(service.createPortfolio(portfolioReq, userEmail), "Created successfully"));
    }

    @PutMapping("/portfolio")
    public ResponseEntity<Portfolio> updatePortfolio(@RequestBody Portfolio portfolio) {
        return ResponseEntity.ok(service.updatePortfolio(portfolio));
    }

    @DeleteMapping("/portfolio/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long id) {
        service.deletePortfolio(id);
        return ResponseEntity.ok().build();
    }


}
