package com.cryptoflio.portfolio_service.Repository;

import com.cryptoflio.portfolio_service.Entites.Asset;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepo extends JpaRepository<Asset, Long> {

    Optional<Asset> findByNameIgnoreCase(String name);

    Optional<Asset> findAssetByCoinId(String id);

    List<Asset> findAllByPortfolioId(Long portfolioId);

    Optional<Asset> findByCoinIdAndPortfolioId(String CoinId, Long PortfolioId);

    @Query("SELECT a FROM Asset a WHERE a.portfolio.id = :portfolioId ORDER BY a.totalProfitAndLossAmount DESC")
    List<Asset> findTopByPortfolioIdOrderByTotalProfitAndLossAmountDesc(@Param("portfolioId") Long portfolioId, Pageable pageable);


    @Query("SELECT a FROM Asset a WHERE a.portfolio.id = :portfolioId ORDER BY a.totalProfitAndLossAmount asc ")
    List<Asset> findTopByPortfolioIdOrderByTotalProfitAndLossAmountAsc(@Param("portfolioId") Long portfolioId, Pageable pageable);
}
