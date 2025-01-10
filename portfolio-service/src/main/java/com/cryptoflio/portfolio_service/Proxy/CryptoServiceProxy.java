package com.cryptoflio.portfolio_service.Proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


//@FeignClient(name = "exchangeServiceClient", url = "${EXCHANGE_SERVICE_SERVICE_HOST:http://localhost}:8080")
@FeignClient(name = "exchangeServiceClient",url = "http://exchange-service")

public interface CryptoServiceProxy {


    @GetMapping("api/v1/price")
    CryptoQuoteDTO getCryptoQuote(@RequestParam String coinID);


    @GetMapping("api/v1/prices")
    List<CryptoQuoteDTO> getCryptoQuotes(@RequestParam String [] coinIDs);
}





