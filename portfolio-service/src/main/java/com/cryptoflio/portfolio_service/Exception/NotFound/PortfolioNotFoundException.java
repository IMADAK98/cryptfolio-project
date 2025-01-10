package com.cryptoflio.portfolio_service.Exception.NotFound;

public class PortfolioNotFoundException extends AbstractNotFoundException {


    public PortfolioNotFoundException(String identifier) {
        super(String.format("Portfolio not found for Id: %s", identifier), identifier, "Portfolio");
    }
}
