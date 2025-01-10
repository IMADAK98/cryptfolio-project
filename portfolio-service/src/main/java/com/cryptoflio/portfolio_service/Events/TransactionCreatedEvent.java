package com.cryptoflio.portfolio_service.Events;

import lombok.Getter;

@Getter
public class TransactionCreatedEvent extends TransactionEvent {

    private final Long transactionId;

    public TransactionCreatedEvent(Long transactionId) {
        this.transactionId = transactionId;
    }

}
