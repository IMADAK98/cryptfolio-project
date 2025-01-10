package com.cryptfolio.exchange_service.Entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinDetails {

    private String id ;
    private String name ;
    private String symbol ;
    private String slug;
    private Map<String,PriceDetails> quote;
    private Timestamp last_updated;
}


