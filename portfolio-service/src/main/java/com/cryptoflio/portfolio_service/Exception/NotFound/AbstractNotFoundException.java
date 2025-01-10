package com.cryptoflio.portfolio_service.Exception.NotFound;

public abstract class AbstractNotFoundException extends RuntimeException {

    private final String entityName;
    private final String identifier;

    public AbstractNotFoundException(String message, String entityName, String identifier) {
        super(message);
        this.entityName = entityName;
        this.identifier = identifier;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getIdentifier() {
        return identifier;
    }
}
