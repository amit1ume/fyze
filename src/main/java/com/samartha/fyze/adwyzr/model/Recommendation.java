package com.samartha.fyze.adwyzr.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import com.samartha.fyze.common.model.BaseModel;
import com.samartha.fyze.securities.model.Stock;
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "recommendations")
public class Recommendation extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "stock_id", nullable = false)
    private Long stockId;

    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_id", nullable = false, insertable = false, updatable = false)
    private Stock stock;

    @Column(name = "advisor_id", nullable = false)
    private Long advisorId;

    // TODO: Update it to lazy loading. It will require us to separate the DTOs from models otherwise Jackson will fail to deserialize the hibernate proxy objects
    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "advisor_id", nullable = false, insertable = false, updatable = false)
    private Advisor advisor;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_period", nullable = false)
    private TimePeriod timePeriod;

    @Enumerated(EnumType.STRING)
    @Column(name = "rating")
    private Rating rating;

    @Column(name = "entry_price", precision = 10, scale = 2)
    private BigDecimal entryPrice;

    @Nullable
    @Column(name = "entry_date", nullable = true)
    private Instant entryDate;

    @Column(name = "target_price", precision = 10, scale = 2)
    private BigDecimal targetPrice;

    @Nullable
    @Column(name = "target_date", nullable = true)
    private Instant targetDate;

    @Nullable
    @Column(name = "stop_loss", precision = 10, scale = 2, nullable = true)
    private BigDecimal stopLoss;

    @Nullable
    @Column(name = "closure_price", precision = 10, scale = 2, nullable = true)
    private BigDecimal closurePrice;

    @Nullable
    @Enumerated(EnumType.STRING)
    @Column(name = "closure_reason", nullable = true)
    private ClosureReason closureReason;

    @Nullable
    @Column(name = "closed_at", nullable = true)
    private Instant closedAt;

    @Nullable
    @Column(name = "closing_recommendation_id")
    private String closingRecommendationId;

    @Setter(AccessLevel.NONE)
    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closing_recommendation_id", insertable = false, updatable = false)
    private Recommendation closingRecommendation;

    @Transient
    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "closingRecommendation", fetch = FetchType.LAZY)
    private List<Recommendation> relatedRecommendations;

    @Column(name = "rationale", columnDefinition = "TEXT")
    private String rationale;

    @Nullable
    @Column(name = "recommendation_url", nullable = true)
    private String recommendationUrl;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "absolute_return", precision = 10, scale = 2)
    private BigDecimal absoluteReturn;

    public enum TimePeriod {
        VERY_SHORT, SHORT, MEDIUM, LONG,VERY_LONG, OPEN_ENDED
    }

    public enum Rating {
        BUY, SELL, HOLD
    }

    public enum ClosureReason {
        PROFIT_TARGET_HIT, STOP_LOSS_TRIGGERED, RATING_REVISED, TARGET_DATE_REACHED
    }
}
