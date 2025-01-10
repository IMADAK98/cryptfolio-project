package com.cryptoflio.portfolio_service.Entites;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "asset")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Asset {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Column(name = "name")
    private String name;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "total_value")
    private Double totalValue;

    @Column(name = "initial_value")
    private Double initialValue;


    @Column(name = "avg_buy_price")
    private Double avgBuyPrice;

    @Column(name = "date")
    private Date purchaseDate;

    @Column(name = "coin_id")
    private String coinId;


    @Column(name = "total_quantity")
    private Double totalQuantity;

    @Column(name = "current_price")
    private Double currentPrice;

    @Column(name = "realized_P&L")
    private Double realizedProfitLossAmount;

    @Column(name = "unrealized_P&L")
    private Double unrealizedProfitLossAmount;


    @Column(name = "total_profit_loss")
    private Double totalProfitAndLossAmount;


    @Column(name = "total_profit_loss_Percentage")
    private Float totalProfitAndLossPercentage;

    @Column(name = "unrealized_profit_loss_Percentage")
    private Float unrealizedProfitLossPercentage;


    @Column(name = "realized_profit_loss_Percentage")
    private Float realizedProfitLossPercentage;





    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @OneToMany(fetch = FetchType.LAZY,
            cascade =
                    {
                            CascadeType.DETACH,
                            CascadeType.REFRESH,
                            CascadeType.REMOVE}
            , mappedBy = "asset")
    @JsonBackReference
    private List<Transaction> transactions;


    public void addTransaction(Transaction transaction) {
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
        transactions.add(transaction);
        transaction.setAsset(this);
    }


    public void removeTransaction(Transaction transaction) {
        if (transactions != null) {
            transactions.remove(transaction);
            transaction.setAsset(null);
        }
    }





}
