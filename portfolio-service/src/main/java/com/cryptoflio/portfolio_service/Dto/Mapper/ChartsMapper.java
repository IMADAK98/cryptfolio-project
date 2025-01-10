package com.cryptoflio.portfolio_service.Dto.Mapper;

import com.cryptoflio.portfolio_service.Entites.Asset;
import com.cryptoflio.portfolio_service.Dto.BarChartResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ChartsMapper {


    public BarChartResponseDTO mapToBarChartResponseDTO(Asset asset) {
        return new BarChartResponseDTO()
                .setAssetName(asset.getName())
                .setAssetQuantity(asset.getTotalQuantity())
                .setAssetSymbol(asset.getSymbol())
                .setCoinId(asset.getCoinId())
                .setAssetValue(asset.getTotalValue());
    }
}
