package com.cryptoflio.portfolio_service.ServiceInterface;

import com.cryptoflio.portfolio_service.Entites.Asset;
import com.cryptoflio.portfolio_service.Dto.Requests.CreateTransactionDto;
import com.cryptoflio.portfolio_service.Entites.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionsService {

    Transaction createTransaction(CreateTransactionDto request, String userEmail);

    List<Transaction> getTransactionsByAssetId(Long assetId);

    List<Transaction> getTransactions();

    Transaction getTransactionById(Long id);

    Optional<Transaction> getOptionalTransactionById(Long id);

    Transaction updateTransaction(Transaction transaction);



    void deleteTransaction(Long id);

    List<Transaction> getBuyTransactions(Asset asset);

    List<Transaction> getSellTransactions(Asset asset);
}