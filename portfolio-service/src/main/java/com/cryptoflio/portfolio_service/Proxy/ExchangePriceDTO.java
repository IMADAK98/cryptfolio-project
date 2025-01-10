package com.cryptoflio.portfolio_service.Proxy;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class  ExchangePriceDTO {

    private double price ;
    private double volume_24h ;
    private double percent_change_1h;
    private double percent_change_24h;
    private double percent_change_7d ;
    private double percent_change_30d;
    private Timestamp last_updated ;
    private double market_cap;
}
