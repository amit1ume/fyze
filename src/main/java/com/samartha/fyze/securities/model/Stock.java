package com.samartha.fyze.securities.model;

import com.samartha.fyze.common.model.BaseModel;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "stocks", uniqueConstraints = @UniqueConstraint(columnNames = { "exchange", "symbol" }))
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

	@Nullable
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "screeners", columnDefinition = "jsonb")
	private List<Screener> screeners;

	public enum Exchange {

		NSE, BSE, NYSE, NASDAQ

	}

	public enum MarketCap {

		MICRO, SMALL, MID, LARGE

	}

	public enum Sector {

		IT, HEALTHCARE, FINANCE, FMCG, ENERGY, CONSUMER_DURABLES, UTILITIES, MATERIALS, REAL_ESTATE,
		COMMUNICATION_SERVICES, BANKING, CAPITAL_GOODS, STEEL, ENTERTAINMENT, AEROSPACE_DEFENCE, RETAIL, RENEWABLE,
		INFRASTRUCTURE, MISCELLANEOUS, AUTOMOBILE, BREWERIES_DISTILLERIES, PAINTS, REFINERIES, CHEMICAL, TELECOM,
		CEMENT, POWER, OIL_GAS, GEMS_JEWELLERY, TRADING, MARINE_PORTS, MINING, RAILWAY, TRANSPORT, TEXTILE, HOTEL,
		ECOMMERCE, ELECTRONICS, CABLES, EDIBLE_OIL, FERTILIZER, CASTINGS_FORGINGS, BEARINGS, AGRICULTURE, TYRES,
		GAS_DISTRIBUTION, LOGISTICS, INFRA_INVESTMENT_TRUST, METAL, REALSTATE_INVESTMENT_TRUST, BATTERY, LEATHER,
		INSURANCE, BROKER,ENGINEERING, CERAMICS,GLASS,PRINT_STATIONARY,SUGAR,SHIPPING,PETROCHEMICAL

	}
}
