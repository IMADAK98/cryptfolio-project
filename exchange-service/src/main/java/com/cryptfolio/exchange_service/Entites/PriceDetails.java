package com.cryptfolio.exchange_service.Entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceDetails{

    private double price ;
    private double volume_24h ;
    private double percent_change_1h;
    private double percent_change_24h;
    private double percent_change_7d ;
    private double percent_change_30d;
    private Timestamp last_updated ;
    private double market_cap;
}
