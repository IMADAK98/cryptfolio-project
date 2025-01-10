package com.cryptoflio.portfolio_service.Dto.Requests;

import com.cryptoflio.portfolio_service.Entites.Action;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.cryptoflio.portfolio_service.utils.EnumValidator;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateTransactionDto {

    @NotNull(message = "amount must no be null")
    @DecimalMin(value = "0.0001", message = "amount must be a valid number")
    private Double amount;
    @NotBlank(message = "coinId must not be null")
    private String coinId;
    @NotBlank(message = "coinName must not be null")
    private String coinName;
    @NotBlank(message = "coinSymbol must not be null")
    private String coinSymbol;
    @NotNull(message = "date must not be null")
    private Date date;
    @NotNull(message = "price must not be null")
    private Double price;
    @NotNull(message = "action must not be null")

    @EnumValidator(enumClazz = Action.class, message = "action must be BUY or SELL ")
    private Action action;
}
