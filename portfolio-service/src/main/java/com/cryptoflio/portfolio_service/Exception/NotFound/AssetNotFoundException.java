package com.cryptoflio.portfolio_service.Exception.NotFound;

public class AssetNotFoundException extends AbstractNotFoundException {


    public AssetNotFoundException(String identifier) {
        super("Asset", identifier, String.format("asset with Id %s not found", identifier));
    }
}
