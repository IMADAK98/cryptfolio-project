package com.cryptoflio.portfolio_service.Dto.Mapper;

import com.cryptoflio.portfolio_service.Entites.Asset;
import com.cryptoflio.portfolio_service.Dto.AssetResponseDto;
import com.cryptoflio.portfolio_service.Dto.TransactionResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class AssetMapper {

    private final TransactionMapper transactionMapper;

    public AssetResponseDto toAssetResponseDto(Asset asset) {

        List<TransactionResponseDTO> transactionResponseDTOS = asset.getTransactions().stream().map(transactionMapper::toTransactionDto).toList();

        return new AssetResponseDto()
                .setName(asset.getName())
                .setSymbol(asset.getSymbol())
                .setTotalValue(asset.getTotalValue())
                .setAvgBuyPrice(asset.getAvgBuyPrice())
                .setPurchaseDate(asset.getPurchaseDate())
                .setCoinId(asset.getCoinId())
                .setTotalQuantity(asset.getTotalQuantity())
                .setCurrentPrice(asset.getCurrentPrice())
                .setRealizedProfitLossAmount(asset.getRealizedProfitLossAmount())
                .setUnrealizedProfitLossAmount(asset.getUnrealizedProfitLossAmount())
                .setTotalProfitAndLossAmount(asset.getTotalProfitAndLossAmount())
                .setUnrealizedProfitLossPercentage(asset.getUnrealizedProfitLossPercentage())
                .setRealizedProfitLossPercentage(asset.getRealizedProfitLossPercentage())
                .setTransactions(transactionResponseDTOS)
                .setInitialValue(asset.getInitialValue())
                .setTotalPlPercentage(asset.getTotalProfitAndLossPercentage());


    }
}
