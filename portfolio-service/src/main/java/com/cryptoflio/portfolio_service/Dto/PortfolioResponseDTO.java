package com.cryptoflio.portfolio_service.Dto;

import com.cryptoflio.portfolio_service.Entites.Asset;
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
//@JsonInclude(value = JsonInclude.Include.NON_NULL)
//@JsonIgnoreProperties(ignoreUnknown = true)
public class PortfolioResponseDTO {

    private Long id;

    private String portfolioName;

    private String userEmail;

    private Date creationDate;

    private Double totalProfitAndLossAmount;

    private Double totalCapitalInvested;

    private Double totalValue;
    private Float totalPlPercent;

    private List<Asset> assets;

}
