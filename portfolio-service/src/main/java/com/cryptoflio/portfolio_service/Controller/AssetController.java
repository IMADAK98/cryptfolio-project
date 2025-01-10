package com.cryptoflio.portfolio_service.Controller;

import com.cryptoflio.portfolio_service.Entites.Asset;
import com.cryptoflio.portfolio_service.ServiceImpl.AssetServiceImpl;
import com.cryptoflio.portfolio_service.Dto.AssetResponseDto;
import com.cryptoflio.portfolio_service.Dto.BaseApiResponse;
import com.cryptoflio.portfolio_service.Dto.Mapper.AssetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AssetController {

    private final AssetServiceImpl service;

    private final AssetMapper assetMapper;
//
//
//    @GetMapping("/test")
//   public String test(@RequestHeader("loggedInUser") String header){
//
//        return  header;
//    }


    @GetMapping("/assets")
    public ResponseEntity<List<Asset>> getAssets() {
        return ResponseEntity.ok(service.getAllAssets());
    }


    @GetMapping("/assets/portfolio/{portfolioId}")
    public ResponseEntity<List<Asset>> getAssetsByPortfolioId(@PathVariable("portfolioId") Long Id) {
        return ResponseEntity.ok(service.getAllAssetsByPortfolioId(Id));
    }


    @GetMapping("/assets/transactions/{assetId}")
    public ResponseEntity<BaseApiResponse<AssetResponseDto>> getAssetTransactionsByAssetId(@PathVariable("assetId") Long assetId) {

        Optional<Asset> asset = service.getOptionalAssetById(assetId);

        if (asset.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>(null, "Asset not found"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>(assetMapper.toAssetResponseDto(asset.get()), "Asset data found"));

    }


    @PostMapping("/assets/{portfolioId}")
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) {
        if (asset == null) {
            throw new RuntimeException("error happend ?");
        }

        //Todo review logic , should look up the new asset if the same entry with the coin name exist then instead update the existing asset and add a new buy
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createAsset(asset));
    }

//    @PutMapping("/assets")
//    public ResponseEntity<Asset> updateAsset(@RequestBody Asset asset) {
//        return ResponseEntity.ok(service.updateAsset(asset));
//    }


    @DeleteMapping("/assets/{assetId}")
    public ResponseEntity<BaseApiResponse<String>> deleteAsset(@PathVariable("assetId") Long assetId) {

        service.deleteAsset(assetId);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseApiResponse<>("Deleted", " Deleted"));

    }


}
