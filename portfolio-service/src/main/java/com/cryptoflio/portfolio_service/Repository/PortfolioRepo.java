package com.cryptoflio.portfolio_service.Repository;

import com.cryptoflio.portfolio_service.Entites.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioRepo extends JpaRepository<Portfolio, Long> {

    Optional<Portfolio> findByUserEmail(String user_email);


}
