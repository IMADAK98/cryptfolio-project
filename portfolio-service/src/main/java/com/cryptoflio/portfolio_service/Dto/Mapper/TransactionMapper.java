package com.cryptoflio.portfolio_service.Dto.Mapper;

import com.cryptoflio.portfolio_service.Entites.Transaction;
import com.cryptoflio.portfolio_service.Dto.Requests.UpdateTransactionDTO;
import com.cryptoflio.portfolio_service.Dto.TransactionResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponseDTO toTransactionDto(Transaction transaction) {
        return new TransactionResponseDTO()
                .setId(transaction.getId())
                .setTransactionAmount(transaction.getTransactionAmount())
                .setPrice(transaction.getPrice())
                .setTimestamp(transaction.getTimestamp())
                .setAction(transaction.getAction());

    }


    public Transaction toTransaction(UpdateTransactionDTO dto) {
        return Transaction.builder()
                .id(dto.getId())
                .transactionAmount(dto.getTransactionAmount())
                .price(dto.getPrice())
                .timestamp(dto.getTimestamp())
                .action(dto.getAction())
                .build();
    }


}
