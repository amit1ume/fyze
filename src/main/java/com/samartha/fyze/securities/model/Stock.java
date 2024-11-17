package com.samartha.fyze.securities.model;

import com.samartha.fyze.common.model.BaseModel;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "stocks")
public class Stock extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "exchange", nullable = false)
    private Exchange exchange;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Transient
    public String getTicker() {
        return this.exchange + ":" + this.symbol;
    }

    @Column(name = "isin", nullable = false)
    private String isin;

    @Column(name = "short_name", nullable = false)
    private String shortName;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "market_cap", nullable = false)
    private MarketCap marketCap;

    @Enumerated(EnumType.STRING)
    @Column(name = "sector", nullable = false)
    private Sector sector;

    public enum Exchange {
        NSE, BSE, NYSE, NASDAQ
    }

    public enum MarketCap {
        MICRO, SMALL, MID, LARGE
    }

    public enum Sector {
        IT, HEALTHCARE, FINANCE,FMCG, ENERGY, INDUSTRIALS, UTILITIES, MATERIALS, REAL_ESTATE, COMMUNICATION_SERVICES,BANKING,CAPITAL_GOODS,STEEL,
        ENTERTAINMENT,AEROSPACE_DEFENCE,RETAIL,RENEWABLE,INFRASTRUCTURE,MISCELLANEOUS,AUTOMOBILE,BREWERIES_DISTILLERIES,PAINTS
    }
}
