package com.cryptfolio.exchange_service.Controllers;


import com.cryptfolio.exchange_service.Entites.CoinDetails;
import com.cryptfolio.exchange_service.Entites.Coin;
import com.cryptfolio.exchange_service.Proxy.CmcProxy;
import com.cryptfolio.exchange_service.Service.CmcService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class CmcController {


    private final CmcService service;
    private final CmcProxy proxy;




    @GetMapping("/coins/{id}")
    public ResponseEntity<Coin> getCoinById(@PathVariable int id) {
        return ResponseEntity.ok(service.getCoinById(id));
    }


    @GetMapping("/price")
    public CoinDetails getQuote(@RequestParam String coinID) {
        return service.getQuoteById(coinID);
    }


    @GetMapping("/prices")
    public List<CoinDetails> getQuotes(@RequestParam String[] coinIDs) {
        return service.getQuotesByIds(coinIDs);
    }


    @GetMapping("/allCoins")
    @Cacheable("coins")
    public ResponseEntity<List<Coin>> getAllCoins() {
        return ResponseEntity.ok(service.getAllCoins());
    }
}
