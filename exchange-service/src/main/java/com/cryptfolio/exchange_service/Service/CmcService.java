package com.cryptfolio.exchange_service.Service;

import com.cryptfolio.exchange_service.Entites.CoinDetails;
import com.cryptfolio.exchange_service.Proxy.CmcProxy;
import com.cryptfolio.exchange_service.Entites.Coin;
import com.cryptfolio.exchange_service.Entites.Quote;
import com.cryptfolio.exchange_service.Repository.CmcRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CmcService {

    private static final Logger logger = LoggerFactory.getLogger(CmcService.class);

    private final CmcProxy proxy;
    private final CmcRepo repo;


    @Value("${api.key}")
    private String apiKey;


    public void initList() {
        try {
            var response = proxy.getData(apiKey);
            repo.saveAll(response.getData());
            logger.info("Table successfully cleared and repopulated.");
        } catch (Exception e) {
            logger.error("Error initializing data: " + e.getMessage(), e);
        }
    }


    public Coin getCoinById(int id) throws RuntimeException {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }


    public CoinDetails getQuoteById(String coinId) throws IllegalArgumentException {
        if (coinId == null || coinId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid Input");
        }
        Quote response = null;
        try {
            response = proxy.getQuote(apiKey, coinId, null, null, null);
            if (response != null && response.getData() != null) {
                return response.getData().getOrDefault(coinId, null);
            } else {
                throw new RuntimeException("Invalid Response");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public List<CoinDetails> getQuotesByIds(String[] coinIds) {
        if (coinIds == null || coinIds.length == 0) {
            throw new IllegalArgumentException("Invalid Input");
        }

        List<CoinDetails> coinDetailsList = null;
        try {
            Quote response = proxy.getQuote(apiKey, coinIds, null, null, null);
            if (response != null && response.getData() != null) {
                Map<String, CoinDetails> dataMap = response.getData();
                coinDetailsList = Arrays.stream(coinIds)
                        .filter(dataMap::containsKey)
                        .map(dataMap::get)
                        .collect(Collectors.toList());


            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return coinDetailsList;
    }


    public List<Coin> getAllCoins() {
        return repo.findAll();
    }
}
