package com.cryptoflio.portfolio_service.Dto.Mapper;

import com.cryptoflio.portfolio_service.Entites.PortfolioSnapshot;
import com.cryptoflio.portfolio_service.Dto.SnapshotDTO;
import org.springframework.stereotype.Component;

@Component
public class SnapshotMapper {

    public SnapshotDTO toSnapshotDto(PortfolioSnapshot snapshot) {

        return SnapshotDTO.builder()
                .totalValue(snapshot.getTotalValue())
                .snapshotDate(snapshot.getTimestamp())
                .build();
    }
}
