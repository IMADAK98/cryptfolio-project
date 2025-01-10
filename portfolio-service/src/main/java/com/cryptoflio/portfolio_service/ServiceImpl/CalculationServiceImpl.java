package com.cryptoflio.portfolio_service.ServiceImpl;

import com.cryptoflio.portfolio_service.Entites.Asset;
import com.cryptoflio.portfolio_service.Entites.Transaction;
import com.cryptoflio.portfolio_service.ServiceInterface.CalculationService;
import com.cryptoflio.portfolio_service.ServiceInterface.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalculationServiceImpl implements CalculationService {

    private final TransactionsService transactionService;

    @Autowired
    public CalculationServiceImpl(@Lazy TransactionsService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public Double calculateWeightedAverageCost(Asset asset) {
        List<Transaction> buyTransactions = transactionService.getBuyTransactions(asset);

        if (buyTransactions.isEmpty()) {
            return 0.0; // Or handle the zero purchase case as needed
        }

        double totalCost = 0.0;
        double totalQuantity = 0.0;

        for (Transaction transaction : buyTransactions) {
            totalCost += transaction.getPrice() * transaction.getTransactionAmount();
            totalQuantity += transaction.getTransactionAmount();

        }

        double weightedAvgPrice = totalCost / totalQuantity;

        return weightedAvgPrice;
    }

    @Override
    public Double calculateRealizedProfitLoss(Asset asset, Double weightedAverageCost) {
        List<Transaction> sellTransactions = transactionService.getSellTransactions(asset);

        double realizedProfitLoss = 0.0;

        for (Transaction sellTransaction : sellTransactions) {
            double costBasisForSale = weightedAverageCost * sellTransaction.getTransactionAmount();
            double profitLossOnSale = (sellTransaction.getPrice() * sellTransaction.getTransactionAmount()) - costBasisForSale;
            realizedProfitLoss += profitLossOnSale;
        }
        return realizedProfitLoss;
    }

    @Override
    public Double calculateUnrealizedProfitLoss(Asset asset, Double currentPrice, Double weightedAverageCost) {
        double quantity = asset.getTotalQuantity() == null ? 0.0 : asset.getTotalQuantity();

        // Ensure Meaningful Data
        if (weightedAverageCost <= 0.0 || currentPrice <= 0.0 || quantity <= 0.0) {
            return 0.0;
        }

        // Calculation
        double totalCurrentValue = currentPrice * quantity;
        double totalCostBasis = weightedAverageCost * quantity;
        double unrealizedProfitLoss = totalCurrentValue - totalCostBasis;

        return unrealizedProfitLoss;
    }

    @Override
    public Double calculateTotalProfitLoss(Double realizedProfitLoss, Double unrealizedProfitLoss) {
        return realizedProfitLoss + unrealizedProfitLoss;

    }

    @Override
    public Float calculateAssetPercentage(Asset asset, double totalValue) {
        return (float) (asset.getTotalValue() / totalValue) * 100;
    }

    public Double calculateValueChanges(double initialValue, double currentValue) {
        return ((currentValue - initialValue));
    }

    public Float calculateValueChangesPercentage(double initialValue, double currentValue) {
        return (float) ((currentValue - initialValue) / initialValue * 100);
    }


}
