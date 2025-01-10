package com.cryptoflio.portfolio_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PortfolioStatsDto {

    private String topPerformerCoinId;
    private String topPerformerName;
    private String topPerformerSymbol;
    private Float topPerformerPercentChange;
    private Double topPerformerValueChange;

    private String worstPerformerCoinId;
    private String worstPerformerName;
    private String worstPerformerSymbol;
    private Float worstPerformerPercentChange;
    private Double worstPerformerValueChange;
}
