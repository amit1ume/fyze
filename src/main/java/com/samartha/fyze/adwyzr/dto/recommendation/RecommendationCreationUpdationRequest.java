package com.samartha.fyze.adwyzr.dto.recommendation;

import com.samartha.fyze.adwyzr.model.Recommendation;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
public class RecommendationCreationUpdationRequest {

	private String id;

	private Long stockId;

	private String stockTicker;

	private Long advisorId;

	private Recommendation.TimePeriod timePeriod;

	private Recommendation.Rating rating;

	private BigDecimal entryPrice;

	private Instant entryDate;

	private BigDecimal targetPrice;

	private Instant targetDate;

	private BigDecimal stopLoss;

	private BigDecimal closurePrice;

	private Recommendation.ClosureReason closureReason;

	private Instant closedAt;

	private String closingRecommendationId;

	private String rationale;

	private String recommendationUrl;

	private Boolean isActive = true;

	private BigDecimal absoluteReturn;

	public Recommendation toRecommendation() {
		if (this.stockId == null) {
			throw new IllegalArgumentException("stockId attribute is null");
		}
		return Recommendation.builder().id(this.id).stockId(this.stockId).advisorId(this.advisorId)
				.timePeriod(this.timePeriod).rating(this.rating).entryPrice(this.entryPrice).entryDate(this.entryDate)
				.targetPrice(this.targetPrice).targetDate(this.targetDate).stopLoss(this.stopLoss)
				.closurePrice(this.closurePrice).closureReason(this.closureReason).closedAt(this.closedAt)
				.closingRecommendationId(this.closingRecommendationId).rationale(this.rationale)
				.recommendationUrl(this.recommendationUrl).isActive(this.isActive).absoluteReturn(this.absoluteReturn)
				.build();
	}

}
