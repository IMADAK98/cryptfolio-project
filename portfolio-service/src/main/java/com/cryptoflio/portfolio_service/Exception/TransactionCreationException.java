package com.cryptoflio.portfolio_service.Exception;

public class TransactionCreationException extends RuntimeException {
    
    public TransactionCreationException(String message) {
        super(message);
    }
}
