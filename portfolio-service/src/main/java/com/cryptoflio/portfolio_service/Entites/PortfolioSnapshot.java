package com.cryptoflio.portfolio_service.Entites;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "snapshot")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PortfolioSnapshot {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "total_value")
    private Double totalValue;

    @Column(name = "timestamp")
    private Date timestamp;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;




}
