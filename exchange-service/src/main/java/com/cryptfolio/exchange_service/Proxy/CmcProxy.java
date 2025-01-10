package com.cryptfolio.exchange_service.Proxy;

import com.cryptfolio.exchange_service.ApplicationConfigs.FeignConfig;
import com.cryptfolio.exchange_service.Entites.Quote;
import com.cryptfolio.exchange_service.Entites.Response;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "coinMarektCapClient", url = "https://pro-api.coinmarketcap.com", configuration = FeignConfig.class)
public interface CmcProxy {

    String API_KEY_HEADER = "X-CMC_PRO_API_KEY";

    @GetMapping("v1/cryptocurrency/map")
    Response getData(@RequestHeader(API_KEY_HEADER) String apiKey);


    @GetMapping("v2/cryptocurrency/quotes/latest")
    @Cacheable("quote")
    Quote getQuote(@RequestHeader(API_KEY_HEADER) String apiKey, @RequestParam(required = false) String id,
                   @RequestParam(required = false) String slug,
                   @RequestParam(required = false) String symbol,
                   @RequestParam(required = false) String[] convert);


    @Cacheable("quote")
    @GetMapping("v2/cryptocurrency/quotes/latest")
    Quote getQuote(@RequestHeader(API_KEY_HEADER) String apiKey, @RequestParam(required = false) String[] id,
                   @RequestParam(required = false) String slug,
                   @RequestParam(required = false) String symbol,
                   @RequestParam(required = false) String[] convert);
}
