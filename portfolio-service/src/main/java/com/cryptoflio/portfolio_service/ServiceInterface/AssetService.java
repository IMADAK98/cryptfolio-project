package com.cryptoflio.portfolio_service.ServiceInterface;

import com.cryptoflio.portfolio_service.Dto.Requests.CreateTransactionDto;
import com.cryptoflio.portfolio_service.Entites.Asset;
import com.cryptoflio.portfolio_service.Entites.Portfolio;
import com.cryptoflio.portfolio_service.Entites.Transaction;
import com.cryptoflio.portfolio_service.Exception.AssetCreationException;
import com.cryptoflio.portfolio_service.Proxy.CryptoQuoteDTO;

import java.util.List;
import java.util.Optional;

public interface AssetService {

    List<Asset> getAllAssets();

    Asset getAssetById(Long id);

    Optional<Asset> getOptionalAssetById(Long id);

    Asset createAsset(Asset asset);

    Asset createOrRetrieveAsset(CreateTransactionDto requestDto, Portfolio portfolio);

    Asset getAssetByCoinIdAndPortfolioId(String coinId, Long portfolioId);

    Asset createAssetUsingTransactionDto(CreateTransactionDto requestDto, Portfolio portfolio) throws AssetCreationException;

    void linkAssetToPortfolio(Portfolio portfolio, Asset asset);

    void deleteAsset(Long id);

    List<Asset> getAllAssetsByPortfolioId(Long portfolioId);

    Asset findByCoinIdAndPortfolioId(String coinId, Long portfolioId);

    Asset updateAsset(Asset asset);

    void updateAllAssets(List<Asset> assets);

    void setAssetValues(Asset asset, double currentPrice);

    double getCryptoQuotePrice(String coinId);

    CryptoQuoteDTO getCryptoQuoteFromApi(String coinId);

    void addTransaction(Asset asset, Transaction transaction);

//    void editTransaction(Asset asset, Transaction transaction);



    void updateTotalQuantity(Asset asset,Transaction transaction);

    void updateTotalQuantity(Asset parentAsset, Double amountDifference);
    void updateTotalQuantity(Asset asset);

    Asset getTopAssetByPortfolioId(Long portfolioId);

    Asset getWorstAssetByPortfolioId(Long portfolioId);
}
