package com.cryptfolio.exchange_service.Entites;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data

public class Response {

    private Status status;
    private List<Coin> data;
}
