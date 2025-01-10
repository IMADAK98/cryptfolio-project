package com.cryptoflio.portfolio_service.Exception.NotFound;

public class TransactionNotFoundException extends AbstractNotFoundException {

    public TransactionNotFoundException(String identifier) {
        super("Transaction", identifier, String.format("Transaction not found for Id %s", identifier));
    }
}
