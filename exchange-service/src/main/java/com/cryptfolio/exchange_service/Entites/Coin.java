package com.cryptfolio.exchange_service.Entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "data")
public class Coin {

    @Id
    @Column(name = "id")
    private int Id;

    @Column(name = "rank")
    private int rank;

    @Column(name = "name")
    private String name;


    @Column(name = "symbol")
    private String symbol;


    @Column(name = "slug")
    private String slug;


    @Column(name = "first_historical_data" )
    private Timestamp first_historical_data ;


    @Column(name = "last_historical_data" )
    private Timestamp last_historical_data ;
}
