package com.cryptoflio.portfolio_service.Events.EventListener;

import com.cryptoflio.portfolio_service.Entites.Asset;
import com.cryptoflio.portfolio_service.Events.EventsImpl.AssetUpdatedEvent;
import com.cryptoflio.portfolio_service.Exception.NotFound.AssetNotFoundException;
import com.cryptoflio.portfolio_service.ServiceImpl.PortfolioServiceImpl;
import com.cryptoflio.portfolio_service.ServiceInterface.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssetEventListener {

    private final PortfolioServiceImpl portfolioServiceImpl;
    private final AssetService assetService;


    @EventListener
    public void handleAssetValuesChangedEvent(AssetUpdatedEvent event) throws AssetNotFoundException {
        Long assetId = event.getAssetId();

        Asset asset = assetService.getAssetById(assetId);


        portfolioServiceImpl.updatePortfolio(asset.getPortfolio().getId());
    }



}
