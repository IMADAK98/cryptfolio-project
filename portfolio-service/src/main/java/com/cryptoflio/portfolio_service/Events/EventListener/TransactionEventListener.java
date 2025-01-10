package com.cryptoflio.portfolio_service.Events.EventListener;

import com.cryptoflio.portfolio_service.Entites.Asset;
import com.cryptoflio.portfolio_service.Events.EventsImpl.AssetUpdatedEvent;
import com.cryptoflio.portfolio_service.Events.TransactionCreatedEvent;
import com.cryptoflio.portfolio_service.Exception.NotFound.AssetNotFoundException;
import com.cryptoflio.portfolio_service.ServiceImpl.AssetServiceImpl;
import com.cryptoflio.portfolio_service.ServiceInterface.TransactionsService;
import com.cryptoflio.portfolio_service.Entites.Transaction;
import com.cryptoflio.portfolio_service.Events.EventManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionEventListener {
    private static final Logger logger = LoggerFactory.getLogger(AssetEventListener.class);

    private final AssetServiceImpl assetServiceImpl;
    private final EventManager eventManager;
    private final TransactionsService transactionService;

    @EventListener
    public void handleTransactionCreatedEvent(TransactionCreatedEvent event) {
        logger.debug("handleTransactionCreatedEvent initiated");
        Long transactionId = event.getTransactionId();

        Transaction transaction = transactionService.getTransactionById(transactionId);


        Asset asset = transaction.getAsset();
        if (asset == null) {
            throw new AssetNotFoundException("Asset is null");
        }

        Asset updatedAsset = assetServiceImpl.updateAsset(asset);
        logger.debug("handleTransactionCreatedEvent completed, asset updated");

        eventManager.publishEvent(new AssetUpdatedEvent(updatedAsset.getId()));
    }


}
