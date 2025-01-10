package com.cryptoflio.portfolio_service.Repository;

import com.cryptoflio.portfolio_service.Entites.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByAssetId(Long assetId);
}
