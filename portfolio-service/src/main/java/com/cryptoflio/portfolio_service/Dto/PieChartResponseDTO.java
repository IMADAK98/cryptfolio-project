package com.cryptoflio.portfolio_service.Dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PieChartResponseDTO {

    private String coinName;
    private Float piPercentage;
}
