package com.cryptoflio.portfolio_service.ServiceImpl;

import com.cryptoflio.portfolio_service.Repository.AssetRepo;
import com.cryptoflio.portfolio_service.Repository.TransactionRepo;
import com.cryptoflio.portfolio_service.ServiceInterface.PortfolioService;
import com.cryptoflio.portfolio_service.Dto.Requests.CreateTransactionDto;
import com.cryptoflio.portfolio_service.Entites.Action;
import com.cryptoflio.portfolio_service.Entites.Asset;
import com.cryptoflio.portfolio_service.Entites.Portfolio;
import com.cryptoflio.portfolio_service.Entites.Transaction;
import com.cryptoflio.portfolio_service.Events.EventManager;
import com.cryptoflio.portfolio_service.Events.TransactionCreatedEvent;
import com.cryptoflio.portfolio_service.Exception.NotFound.AssetNotFoundException;
import com.cryptoflio.portfolio_service.Exception.NotFound.TransactionNotFoundException;
import com.cryptoflio.portfolio_service.Exception.TransactionCreationException;
import com.cryptoflio.portfolio_service.ServiceInterface.AssetService;
import com.cryptoflio.portfolio_service.ServiceInterface.TransactionsService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionsService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepo transactionRepo;
    private final AssetRepo assetRepo;
    private final PortfolioService portfolioService;
    private final AssetService assetService;
    private final EventManager eventManager;


    public List<Transaction> getTransactions() {
        return transactionRepo.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepo.findById(id).orElseThrow(() -> new TransactionNotFoundException(id.toString()));
    }

    @Override
    public Optional<Transaction> getOptionalTransactionById(Long id) {
        return transactionRepo.findById(id);
    }


    public Transaction createTransaction(CreateTransactionDto requestDto, String userEmail) {
        Portfolio relatedPortfolio = null;
        Asset relatedAsset = null;

        relatedPortfolio = portfolioService.getPortfolioByUserEmail(userEmail);
        relatedAsset = assetService.getAssetByCoinIdAndPortfolioId(requestDto.getCoinId(), relatedPortfolio.getId());

        if (relatedAsset != null){
            //if the related asset is not null then reset the initial value
            relatedAsset.setInitialValue(relatedAsset.getInitialValue() + requestDto.getAmount() * requestDto.getPrice());
        }

        if (relatedAsset == null) {
            relatedAsset = assetService.createAssetUsingTransactionDto(requestDto, relatedPortfolio);
        }
        Transaction newTransaction = buildNewTransaction(requestDto);
        assetService.addTransaction(relatedAsset, newTransaction);

        logger.debug("new transaction created");
        // Persist the new transaction
        Transaction savedTransaction = transactionRepo.save(newTransaction);
        if (savedTransaction.getId() == null) {
            logger.error("Failed to save transaction");
            throw new RuntimeException("Failed to save transaction");
        }

        eventManager.publishEvent(new TransactionCreatedEvent(savedTransaction.getId()));
        logger.debug("service method finished");
        return newTransaction;
    }

    private Transaction buildNewTransaction(CreateTransactionDto requestDto) {
        try {
            return Transaction.builder()
                    .action(requestDto.getAction())
                    .transactionAmount(requestDto.getAmount())
                    .price(requestDto.getPrice())
                    .timestamp(new Timestamp(System.currentTimeMillis()))
                    .build();
        } catch (Exception e) {
            logger.error("Failed to build transaction", e);
            throw new TransactionCreationException("Failed to build transaction " + e.getMessage());
        }
    }

    @Override
    public List<Transaction> getTransactionsByAssetId(Long assetId) {
        return transactionRepo.findAllByAssetId(assetId);
    }


    public Transaction updateTransaction(Transaction newTransaction) {
        Optional<Transaction> transactionOptional  = transactionRepo.findById(newTransaction.getId());

        if (transactionOptional.isPresent()){

            Transaction existingTransaction = transactionOptional.get();

            // Fetch the associated asset
            Asset parentAsset = assetService.getAssetById(existingTransaction.getAsset().getId());

            // Calculate the difference between the old and new transaction values
            double difference = calculateDifference(existingTransaction, newTransaction);

            // Update the asset's initial value
            updateAssetInitialValue(parentAsset, difference);


            Double amountDifference = newTransaction.getTransactionAmount() - existingTransaction.getTransactionAmount();

          existingTransaction.setTransactionAmount(newTransaction.getTransactionAmount());
          existingTransaction.setPrice(newTransaction.getPrice());
          existingTransaction.setAction(newTransaction.getAction()); // even though the action is not being changed
          existingTransaction.setTimestamp(newTransaction.getTimestamp());

          Transaction updatedTransaction = transactionRepo.save(existingTransaction);

          assetService.updateTotalQuantity(parentAsset,  amountDifference);

          eventManager.publishEvent(new TransactionCreatedEvent(updatedTransaction.getId()));

          return updatedTransaction;
        }else {
            throw new RuntimeException("Transaction not found");
        }


    }


    /**
     * Calculates the difference between the old and new transaction values.
     */
    private double calculateDifference(Transaction oldTransaction, Transaction newTransaction) {
        double oldInvestment = oldTransaction.getPrice() * oldTransaction.getTransactionAmount();
        double newInvestment = newTransaction.getPrice() * newTransaction.getTransactionAmount();
        return newInvestment - oldInvestment;
    }
    /**
     * Updates the asset's initial value based on the given difference.
     */
    private void updateAssetInitialValue(Asset asset, double difference) {
        double newInitialValue = asset.getInitialValue() + difference;
        asset.setInitialValue(newInitialValue);
    }




    public void deleteTransaction(Long id) throws TransactionNotFoundException {
        var tempTrans = transactionRepo.findById(id).orElseThrow(() -> new TransactionNotFoundException(id.toString()));
        Asset asset = tempTrans.getAsset();
        if (asset == null) {
            throw new AssetNotFoundException("Asset not found");
        }
        if (asset.getTransactions().size() > 1) {
            asset.removeTransaction(tempTrans);
            transactionRepo.delete(tempTrans);
            assetService.updateTotalQuantity(asset);
            asset.setInitialValue(asset.getInitialValue() - tempTrans.getTransactionAmount() * tempTrans.getPrice());
//            assetService.updateInitialInvestedAmount(asset);
            assetService.updateAsset(asset);

        } else {
            assetService.deleteAsset(asset.getId());
        }
    }


    public List<Transaction> getBuyTransactions(Asset asset) {
        if (asset == null || asset.getTransactions().isEmpty()) {
            throw new IllegalArgumentException("Asset cannot be null");
        }
        return asset.getTransactions().stream()
                .filter(tx -> tx.getAction() == Action.BUY)
                .collect(Collectors.toList());
    }


    public List<Transaction> getSellTransactions(Asset asset) {
        if (asset == null || asset.getTransactions().isEmpty()) {
            throw new IllegalArgumentException("Asset cannot be null");
        }
        return asset.getTransactions().stream()
                .filter(tx -> tx.getAction() == Action.SELL)
                .collect(Collectors.toList());
    }


}
