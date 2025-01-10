package com.cryptoflio.portfolio_service.Entites;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount")
    private Double transactionAmount;

    @Column(name = "price")
    private Double price;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    private Action action;


    @ManyToOne
    @JoinColumn(name = "asset_id")
    @JsonManagedReference
    private Asset asset;


}
