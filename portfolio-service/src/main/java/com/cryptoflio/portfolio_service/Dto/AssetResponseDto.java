package com.cryptoflio.portfolio_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AssetResponseDto {
    
    private String name;
    private String symbol;
    private Double totalValue;
    private Double avgBuyPrice;
    private Date purchaseDate;
    private String coinId;
    private Double totalQuantity;
    private Double currentPrice;
    private Double realizedProfitLossAmount;
    private Double unrealizedProfitLossAmount;
    private Double totalProfitAndLossAmount;
    private Float unrealizedProfitLossPercentage;
    private Float realizedProfitLossPercentage;
    private Double initialValue;
    private Float totalPlPercentage;
    private List<TransactionResponseDTO> transactions;


}
