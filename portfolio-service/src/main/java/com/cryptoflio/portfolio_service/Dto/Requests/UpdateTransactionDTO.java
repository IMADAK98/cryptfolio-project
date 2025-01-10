package com.cryptoflio.portfolio_service.Dto.Requests;


import com.cryptoflio.portfolio_service.Entites.Action;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.cryptoflio.portfolio_service.utils.EnumValidator;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateTransactionDTO {

    @NotNull(message = "id must not be null")
    private Long id;

    @NotNull(message = "amount must not be null")
    @DecimalMin(value = "0.0001", message = "amount must be a valid number")
    private Double transactionAmount;
    @NotNull(message = "date must not be null")
    private Timestamp timestamp;
    @NotNull(message = "price must not be null")
    private Double price;
    @NotNull(message = "action must not be null")

    @EnumValidator(enumClazz = Action.class, message = "action must be BUY or SELL ")
    private Action action;

    @NotNull(message = "assetId must not be null")
    private Long assetId;
}
