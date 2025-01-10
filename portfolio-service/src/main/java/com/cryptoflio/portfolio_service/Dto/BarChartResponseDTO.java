package com.cryptoflio.portfolio_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class BarChartResponseDTO {

    private String assetName;
    private Double assetValue;
    private Double assetQuantity;
    private String coinId;
    private String assetSymbol;
}
