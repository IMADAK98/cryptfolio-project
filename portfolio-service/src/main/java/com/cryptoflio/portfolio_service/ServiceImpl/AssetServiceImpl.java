package com.cryptoflio.portfolio_service.ServiceImpl;

import com.cryptoflio.portfolio_service.Repository.AssetRepo;
import com.cryptoflio.portfolio_service.Dto.Requests.CreateTransactionDto;
import com.cryptoflio.portfolio_service.Entites.Action;
import com.cryptoflio.portfolio_service.Entites.Asset;
import com.cryptoflio.portfolio_service.Entites.Portfolio;
import com.cryptoflio.portfolio_service.Entites.Transaction;
import com.cryptoflio.portfolio_service.Exception.AssetCreationException;
import com.cryptoflio.portfolio_service.Exception.NotFound.AssetNotFoundException;
import com.cryptoflio.portfolio_service.Proxy.CryptoQuoteDTO;
import com.cryptoflio.portfolio_service.Proxy.CryptoServiceProxy;
import com.cryptoflio.portfolio_service.ServiceInterface.AssetService;
import com.cryptoflio.portfolio_service.ServiceInterface.CalculationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {
    private static final Logger logger = LoggerFactory.getLogger(AssetServiceImpl.class);

    private final AssetRepo assetRepo;
    private final CryptoServiceProxy proxy;
    private final CalculationService calculationService;


    public List<Asset> getAllAssets() {
        return assetRepo.findAll();
    }

    public Asset getAssetById(Long id) throws AssetNotFoundException {
        return assetRepo.findById(id).orElseThrow(() -> new AssetNotFoundException(id.toString()));
    }

    @Override
    public Optional<Asset> getOptionalAssetById(Long id) {
        return assetRepo.findById(id);
    }

    public Asset createAsset(Asset asset) {
        return assetRepo.save(asset);
    }


    public Asset createOrRetrieveAsset(CreateTransactionDto requestDto, Portfolio portfolio) {
        Optional<Asset> existingAsset = assetRepo.findByCoinIdAndPortfolioId(requestDto.getCoinId(), portfolio.getId());
        //what if I trigger the event of fetching the asset price here ??
        return existingAsset.orElseGet(() -> createAssetUsingTransactionDto(requestDto, portfolio));
    }


    public Asset getAssetByCoinIdAndPortfolioId(String coinId, Long portfolioId) {
        Optional<Asset> existingAsset = assetRepo.findByCoinIdAndPortfolioId(coinId, portfolioId);
        return existingAsset.orElse(null);
    }


    public Asset createAssetUsingTransactionDto(CreateTransactionDto requestDto, Portfolio portfolio) throws AssetCreationException {
        try {

            var newAsset = Asset.builder()
                    .name(requestDto.getCoinName())
                    .purchaseDate(requestDto.getDate())
                    .coinId(requestDto.getCoinId())
                    .symbol(requestDto.getCoinSymbol())
                    .currentPrice(requestDto.getPrice())
                    .initialValue(requestDto.getAmount() * requestDto.getPrice())
//                .totalQuantity(requestDto.getAmount())
                    .build();

            linkAssetToPortfolio(portfolio, newAsset);
            return assetRepo.save(newAsset);

        } catch (Exception e) {
            throw new AssetCreationException("Failed to create asset for transaction" + e.getMessage());
        }
    }

    public void linkAssetToPortfolio(Portfolio portfolio, Asset asset) {
        portfolio.addAsset(asset);
    }


    public void deleteAsset(Long id) {
        Asset existingAsset = assetRepo.findById(id).orElseThrow(() -> new RuntimeException("error"));

        assetRepo.delete(existingAsset);
    }

    public List<Asset> getAllAssetsByPortfolioId(Long portfolioId) {
        return assetRepo.findAllByPortfolioId(portfolioId);
    }

    public Asset findByCoinIdAndPortfolioId(String coinId, Long portfolioId) {
        if (coinId == null || portfolioId == null) {
            throw new IllegalArgumentException("coinId and portfolioId cannot be null");
        }
        var asset = assetRepo.findByCoinIdAndPortfolioId(coinId, portfolioId);
        return asset.orElse(null);
    }




    public Asset updateAsset(Asset asset) {
        double currentPrice = getCryptoQuotePrice(asset.getCoinId());
        setAssetValues(asset, currentPrice);
        return assetRepo.save(asset);
    }



    public List<String> getIds (List<Asset> assets){
       return   assets.stream()
                .map(Asset::getCoinId)
                .toList();
    }



    public void updateAllAssets(List<Asset> assets) {
        if (assets == null || assets.isEmpty()) {
            throw new IllegalArgumentException("Invalid parameters assets are empty");
        }


        List<String> coinIds = getIds(assets);

        //TODO when proxy is unavailable a fallback mechanism should be used
        try {
            logger.debug("calling the api");
            // Fetch quotes for all coinIds in a single API call
            List<CryptoQuoteDTO> quotes = proxy.getCryptoQuotes(coinIds.toArray(new String[0]));
            logger.debug("finally a result is returned");

            List<Asset> updatedAssets = assets.stream()
                    .map(asset -> {
                        CryptoQuoteDTO quote = quotes.stream().filter(q-> asset.getCoinId().equals(q.getId())).findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Quote not found for coinId: " + asset.getCoinId()));

                        double currentPrice = quote.getQuote().get("USD").getPrice();
                        asset.setCurrentPrice(currentPrice);
                        asset.setTotalValue(currentPrice * asset.getTotalQuantity());
                        setAssetValues(asset, currentPrice);
                        return asset;
                    })
                    .collect(Collectors.toList());

            assetRepo.saveAll(updatedAssets);
            logger.info("successfully updated assets");
        } catch (Exception e) {
            logger.error("Error updating assets", e);
        }
    }


    @Async
    public void setAssetValues(Asset asset, double currentPrice) {
        logger.debug("Setting asset values");

        try {



            Double weightedAvgCost = calculationService.calculateWeightedAverageCost(asset);

            Double realizedProfitLoss = calculationService.calculateRealizedProfitLoss(asset, weightedAvgCost);

            Double unrealizedProfitLoss = calculationService.calculateUnrealizedProfitLoss(asset, currentPrice, weightedAvgCost);

            Double totalProfitLoss = calculationService.calculateTotalProfitLoss(realizedProfitLoss, unrealizedProfitLoss);

            float totalPlPercentage = calculationService.calculateValueChangesPercentage(asset.getInitialValue() ,currentPrice * asset.getTotalQuantity());
            asset.setAvgBuyPrice(weightedAvgCost);
            asset.setUnrealizedProfitLossAmount(unrealizedProfitLoss);
            asset.setRealizedProfitLossAmount(realizedProfitLoss);
            asset.setTotalProfitAndLossAmount(totalProfitLoss);
            asset.setTotalValue(asset.getTotalQuantity() * currentPrice);
            asset.setCurrentPrice(currentPrice);
            asset.setTotalProfitAndLossPercentage(totalPlPercentage);



        } catch (Exception e) {
            logger.error("Error setting asset values", e);
            throw new RuntimeException("Error setting asset values", e);
        }


    }


    // Existing method simplified to focus on price
    public double getCryptoQuotePrice(String coinId) {
        if (coinId == null || coinId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid coinId: " + coinId);
        }

        CryptoQuoteDTO quote = getCryptoQuoteFromApi(coinId); // Uses cache
        if (quote == null) {
            throw new RuntimeException("quote is null");
        }
        double price = quote.getQuote().get("USD").getPrice();
        return price;
    }

    public CryptoQuoteDTO getCryptoQuoteFromApi(String coinId) {
        if (coinId == null || coinId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid coinId: " + coinId);
        }
        CryptoQuoteDTO quote = null;
        try {
            quote = proxy.getCryptoQuote(coinId);
            return quote;
        } catch (Exception e) {
            logger.error("Exception happend while getting quote from proxt", e);
            throw new RuntimeException("Error getting quote from proxy", e);
        }
    }


    public void addTransaction(Asset asset, Transaction transaction) {
        logger.debug("Adding transaction");
        asset.addTransaction(transaction);
        updateTotalQuantity(asset, transaction);
    }


    @Override
    public void updateTotalQuantity(Asset parentAsset,Transaction childTransaction) {

        Action transactionAction = childTransaction.getAction();
        Double transactionAmount = childTransaction.getTransactionAmount();

        logger.debug("Updating quantity");
        if (parentAsset.getTotalQuantity() == null) {
            parentAsset.setTotalQuantity(0.0);
        }
        if (transactionAction == Action.BUY) {
            parentAsset.setTotalQuantity(parentAsset.getTotalQuantity() + transactionAmount);
        } else if (transactionAction == Action.SELL) {
            parentAsset.setTotalQuantity(parentAsset.getTotalQuantity() - transactionAmount);
        }
        assetRepo.save(parentAsset);
    }

    @Override
    public void updateTotalQuantity(Asset parentAsset, Double amountDifference) {
        logger.debug("Updating quantity");

        if (parentAsset.getTotalQuantity() == null) {
            parentAsset.setTotalQuantity(0.0);
        }

        // Since the action won't change, simply adjust the quantity by the difference
        parentAsset.setTotalQuantity(parentAsset.getTotalQuantity() + amountDifference);

        // Save the updated asset
        assetRepo.save(parentAsset);
    }

    @Override
    public void updateTotalQuantity(Asset asset){
        logger.debug("Updating quantity after deletion");
        if (asset != null){
          double new_total = asset.getTransactions().stream().mapToDouble(Transaction::getTransactionAmount)
                    .sum();
          asset.setTotalQuantity(new_total);
        }
    }




//    public void editTransaction(Asset asset) {
////        Optional<Transaction> existingTransaction = asset.getTransactions().stream()
////                .filter(t -> t.getId().equals(incomingTransaction.getId()))
////                .findFirst();
//
////        if (existingTransaction.isPresent()) {
////            Transaction existing = existingTransaction.get();
////            Action previousAction = existing.getAction();
////            Double previousAmount = existing.getTransactionAmount();
//
//////            existing.setAction(incomingTransaction.getAction());
////            existing.setTransactionAmount(incomingTransaction.getTransactionAmount());
////            existing.setPrice(incomingTransaction.getPrice());
////            existing.setTimestamp(incomingTransaction.getTimestamp());
//
//
////            updateTotalQuantity(asset, previousAction, -previousAmount);  // Revert previous amount
//            updateTotalQuantity(asset, incomingTransaction.getAction(), incomingTransaction.getTransactionAmount());  // Apply new amount
//
//            transactionRepo.save(existing);
//
//    }

    public Asset getTopAssetByPortfolioId(Long portfolioId) {
        Pageable topOne = PageRequest.of(0, 1);
        List<Asset> topAssets = assetRepo.findTopByPortfolioIdOrderByTotalProfitAndLossAmountDesc(portfolioId, topOne);
        return topAssets.isEmpty() ? null : topAssets.get(0);
    }


    public Asset getWorstAssetByPortfolioId(Long portfolioId) {
        Pageable topOne = PageRequest.of(0, 1);
        List<Asset> topAssets = assetRepo.findTopByPortfolioIdOrderByTotalProfitAndLossAmountAsc(portfolioId, topOne);
        return topAssets.isEmpty() ? null : topAssets.get(0);
    }

}


