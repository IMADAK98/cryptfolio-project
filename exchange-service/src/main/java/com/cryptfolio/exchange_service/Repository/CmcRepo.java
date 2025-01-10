package com.cryptfolio.exchange_service.Repository;

import com.cryptfolio.exchange_service.Entites.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CmcRepo extends JpaRepository<Coin,Integer> {

    @Query("SELECT c FROM Coin c WHERE  c.slug ILIKE %:searchInput% or c.symbol ILIKE %:searchInput%" )
    List<Coin> findByIdOrNameOrSymbol(@Param("searchInput") String searchInput);
}
