package com.cryptoflio.portfolio_service.Repository;

import com.cryptoflio.portfolio_service.Entites.PortfolioSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SnapshotRepository extends JpaRepository<PortfolioSnapshot, Long> {


    Optional<PortfolioSnapshot> findTopByPortfolioIdOrderByTimestampDesc(Long portfolioId);
}
