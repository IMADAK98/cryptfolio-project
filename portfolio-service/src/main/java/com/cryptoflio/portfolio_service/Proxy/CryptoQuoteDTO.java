package com.cryptoflio.portfolio_service.Proxy;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

@Data
public class CryptoQuoteDTO {

    private String id;
    private String name;
    private String symbol;
    private String slug;
    private Map<String, ExchangePriceDTO> quote;
    private Timestamp lastUpdated;


}

