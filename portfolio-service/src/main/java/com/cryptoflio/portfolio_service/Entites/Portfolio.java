package com.cryptoflio.portfolio_service.Entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "portfolios")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    @Column(name = "portfolio-name")
    private String portfolioName;

    //received from the token
    @Column(name = "user_email", unique = true)
    private String userEmail;

    @Column(name = "creation_date")
    private Date creationDate;

    @Column(name = "total_profit_loss")
    private Double totalProfitAndLossAmount;


    @Column(name = "capital_invested")
    private Double capitalInvestedAmount;


    @Column(name="total_pl_percentage")
    private Float totalPLPercentage;


    @Column(name = "total_value")
    private Double totalValue;


    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<PortfolioSnapshot> snapshot;



    @OneToMany(fetch = FetchType.LAZY,
            cascade =
                    {
                            CascadeType.DETACH,
                            CascadeType.REMOVE,
                            CascadeType.REFRESH,
                            CascadeType.PERSIST
                    }
            , mappedBy = "portfolio")
    private List<Asset> assets;

    public void addAsset(Asset tempAsset) {
        if (assets == null) {
            assets = new ArrayList<>();
        }
        assets.add(tempAsset);
        tempAsset.setPortfolio(this);
    }

    public void removeAsset(Asset tempAsset) {
        if (assets != null) {
            assets.remove(tempAsset);
            tempAsset.setPortfolio(null);
        }
    }


}
