package com.cryptoflio.portfolio_service.Dto;

import com.cryptoflio.portfolio_service.Entites.Action;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class TransactionResponseDTO {

    private Long id;
    private Double price;
    private Timestamp timestamp;
    private Action action;
    private Double transactionAmount;
}
