package com.samartha.fyze.adwyzr.dto;

import java.math.BigDecimal;
import java.time.Instant;
import com.samartha.fyze.adwyzr.model.Recommendation.TimePeriod;
import com.samartha.fyze.adwyzr.model.Recommendation.Rating;
import com.samartha.fyze.adwyzr.model.Advisor;
import com.samartha.fyze.securities.model.Stock;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConsolidatedBuyRecommendation {

	private Stock stock;

	private Advisor advisor;

	private TimePeriod timePeriod;

	private Rating rating;

	private BigDecimal minEntryPrice;

	private BigDecimal maxEntryPrice;

	private BigDecimal latestTargetPrice;

	private Instant latestTargetDate;

	private BigDecimal latestStopLoss;

	private String latestRationale;

	private Boolean isActive;

	private BigDecimal avgReturn;

}
