package com.cryptfolio.exchange_service.Entites;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data

public class Quote {

    private Map<String ,CoinDetails> data;
    private Status status;

}
