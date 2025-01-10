package com.cryptoflio.portfolio_service.Events.EventsImpl;

import com.cryptoflio.portfolio_service.Events.AssetEvent;
import lombok.Getter;

@Getter
public class AssetUpdatedEvent extends AssetEvent {

    private final Long assetId;

    public AssetUpdatedEvent(Long assetId) {
        this.assetId = assetId;
    }
}
