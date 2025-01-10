package com.cryptoflio.portfolio_service.ServiceInterface;

import com.cryptoflio.portfolio_service.Entites.Asset;

public interface CalculationService {

    Double calculateWeightedAverageCost(Asset asset);

    Double calculateRealizedProfitLoss(Asset asset, Double weightedAverageCost);

    Double calculateUnrealizedProfitLoss(Asset asset, Double currentPrice, Double weightedAverageCost);

    Double calculateTotalProfitLoss(Double realizedProfitLoss, Double unrealizedProfitLoss);

    Float calculateAssetPercentage(Asset asset, double totalValue);

    Double calculateValueChanges(double initialValue, double currentValue);

    Float calculateValueChangesPercentage(double initialValue, double currentValue);
}
