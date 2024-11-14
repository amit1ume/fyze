package com.samartha.fyze.adwyzr.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.samartha.fyze.securities.repo.StockRepo;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import com.samartha.fyze.adwyzr.dto.ConsolidatedBuyRecommendation;
import com.samartha.fyze.adwyzr.model.Advisor;
import com.samartha.fyze.adwyzr.model.Recommendation;
import com.samartha.fyze.adwyzr.model.Recommendation.Rating;
import com.samartha.fyze.adwyzr.model.Recommendation.TimePeriod;
import com.samartha.fyze.adwyzr.repo.AdvisorRepo;
import com.samartha.fyze.adwyzr.repo.RecommendationRepo;
import com.samartha.fyze.securities.model.Stock;

@Service
public class RecommendationService {
    private final RecommendationRepo recommendationRepo;
    private final AdvisorRepo advisorRepo;
    private final StockRepo stockRepo;

    RecommendationService(RecommendationRepo recommendationRepo, AdvisorRepo advisorRepo, StockRepo stockRepo) {
        this.recommendationRepo = recommendationRepo;
        this.advisorRepo = advisorRepo;
        this.stockRepo = stockRepo;
    }

    public Recommendation saveRecommendation(Recommendation recommendation){
        //If a Sell recommendation, then close existing Buy & Hold Recommendation
        Recommendation savedRecommendation = recommendationRepo.save(recommendation);
        if(Rating.SELL.equals(recommendation.getRating())){
            List<Recommendation> recommendations = recommendationRepo.findByStockIdAndAdvisorIdAndIsActiveTrueAndRatingIn(
                    recommendation.getStockId(), recommendation.getAdvisorId(), List.of(Rating.HOLD, Rating.BUY));
            for(Recommendation prevRecommendation : recommendations){
                prevRecommendation.setIsActive(false);
                prevRecommendation.setClosingRecommendationId(savedRecommendation.getId());
                prevRecommendation.setClosedAt(Instant.now());
                // Replace this with ltp of the stock
                prevRecommendation.setClosurePrice(recommendation.getTargetPrice());
                prevRecommendation.setClosureReason(Recommendation.ClosureReason.RATING_REVISED);
                recommendationRepo.save(prevRecommendation);
            }
        } else { // If Buy or Hold, then close the active Sell Recommendations
            List<Recommendation> recommendations = recommendationRepo.findByStockIdAndAdvisorIdAndIsActiveTrueAndRatingIn(
                    recommendation.getStockId(), recommendation.getAdvisorId(), List.of(Rating.SELL));
            for(Recommendation prevRecommendation: recommendations){
                prevRecommendation.setIsActive(false);
                prevRecommendation.setClosingRecommendationId(savedRecommendation.getId());
                prevRecommendation.setClosedAt(Instant.now());
                // Replace this with ltp of the stock
                // TODO: Finalize what should go here
                prevRecommendation.setClosurePrice(recommendation.getEntryPrice());
                prevRecommendation.setClosureReason(Recommendation.ClosureReason.RATING_REVISED);
                recommendationRepo.save(prevRecommendation);
            }
        }
        return savedRecommendation;
    }

    public Recommendation updateRecommendation(Recommendation recommendation){
        return recommendationRepo.save(recommendation);
    }

    public List<ConsolidatedBuyRecommendation> getLastKActiveStockRecommendationsByAdvisor(Long advisorId, int k) {
        Advisor advisor = advisorRepo.findById(advisorId).orElseThrow(() -> new RuntimeException("Advisor not found"));
        List<Recommendation> recommendations = recommendationRepo.findLastKActiveStockRecommendationsByAdvisorId(advisorId, k);
        Map<Stock, List<Recommendation>> stockRecommendationsMap = recommendations.stream()
                .collect(Collectors.groupingBy(Recommendation::getStock));

        List<ConsolidatedBuyRecommendation> consolidatedRecommendations = new ArrayList<>();
        for (Map.Entry<Stock, List<Recommendation>> entry : stockRecommendationsMap.entrySet()) {
            Stock stock = entry.getKey();
            List<Recommendation> recommendationsForStock = entry.getValue();
            BigDecimal minEntryPrice = null;
            BigDecimal maxEntryPrice = null;
            BigDecimal latestTargetPrice = null;
            Instant earliestEntryDate = null;
            Instant latestTargetDate = null;
            String latestRationale = null;
            BigDecimal latestStopLoss = null;
            BigDecimal avgReturn = null;
            TimePeriod latestTimePeriod = null;
            Recommendation latestRecommendation = null;
            Rating latestRating = null;
            int enteredRecommendationCount = 0;


            for (Recommendation rec : recommendationsForStock) {
                if (rec.getEntryDate()!= null && rec.getEntryDate().isAfter(Instant.now())) {
                    enteredRecommendationCount++;
                    avgReturn = avgReturn.add(rec.getAbsoluteReturn());
                }
                if(rec.getAbsoluteReturn() != null){
                    avgReturn = avgReturn.add(rec.getAbsoluteReturn());
                    enteredRecommendationCount++;
                }

                if (minEntryPrice== null || minEntryPrice.compareTo(rec.getEntryPrice()) > 0) {
                    minEntryPrice = rec.getEntryPrice();
                }
                if (maxEntryPrice == null || maxEntryPrice.compareTo(rec.getEntryPrice()) < 0) {
                    maxEntryPrice = rec.getEntryPrice();
                }
                if (earliestEntryDate == null || earliestEntryDate.isAfter(Objects.requireNonNull(rec.getEntryDate()))) {
                    earliestEntryDate = rec.getEntryDate();
                }
                if (latestRecommendation == null || latestRecommendation.getCreatedAt().isBefore(rec.getCreatedAt())) {
                    latestRecommendation = rec;
                    latestTargetPrice = rec.getTargetPrice();
                    latestTargetDate = rec.getCreatedAt();
                    latestRationale = rec.getRationale();
                    latestTimePeriod = rec.getTimePeriod();
                    latestStopLoss = rec.getStopLoss();
                    latestRating = rec.getRating();
                }

            }
            if(enteredRecommendationCount > 0) {
                avgReturn = avgReturn.divide(new BigDecimal(enteredRecommendationCount), RoundingMode.HALF_UP);
            }

            ConsolidatedBuyRecommendation consolidatedRecommendation = ConsolidatedBuyRecommendation.builder()
                    .stock(stock)
                    .advisor(advisor)
                    .timePeriod(latestTimePeriod)
                    .rating(latestRating)
                    .minEntryPrice(minEntryPrice)
                    .maxEntryPrice(maxEntryPrice)
                    .latestTargetPrice(latestTargetPrice)
                    .latestTargetDate(latestTargetDate)
                    .latestStopLoss(latestStopLoss)
                    .latestRationale(latestRationale)
                    .isActive(true)
                    .avgReturn(avgReturn)
                    .build();

            consolidatedRecommendations.add(consolidatedRecommendation);
        }

        return consolidatedRecommendations;
    }

//    Note: We don't want to user this function. Since we don't want only buy recommendations to be shown for given stock but also the sell recommendations.
    public List<ConsolidatedBuyRecommendation> getLastKAdvisorActiveRecommendationForGivenStock(Long stockId, int k){
        Stock stock = stockRepo.findById(stockId).orElseThrow(() -> new RuntimeException("Stock not found"));
        List<Recommendation> recommendations = recommendationRepo.findLastKAdvisorActiveRecommendationForStockId(stockId, k);
        Map<Advisor, List<Recommendation>> stockRecommendationsMap = recommendations.stream()
                .collect(Collectors.groupingBy(Recommendation::getAdvisor));

        List<ConsolidatedBuyRecommendation> consolidatedRecommendations = new ArrayList<>();
        for (Map.Entry<Advisor, List<Recommendation>> entry : stockRecommendationsMap.entrySet()) {
            Advisor advisor = entry.getKey();
            List<Recommendation> recommendationsForStock = entry.getValue();
            BigDecimal minEntryPrice = null;
            BigDecimal maxEntryPrice = null;
            BigDecimal latestTargetPrice = null;
            Instant earliestEntryDate = null;
            Instant latestTargetDate = null;
            String latestRationale = null;
            BigDecimal latestStopLoss = null;
            BigDecimal avgReturn = null;
            TimePeriod latestTimePeriod = null;
            Recommendation latestRecommendation = null;
            Rating latestRating = null;
            int enteredRecommendationCount = 0;


            for (Recommendation rec : recommendationsForStock) {
                if (rec.getEntryDate()!= null && rec.getEntryDate().isAfter(Instant.now())) {
                    enteredRecommendationCount++;
                    avgReturn = avgReturn.add(rec.getAbsoluteReturn());
                }
                if(rec.getAbsoluteReturn() != null){
                    avgReturn = avgReturn.add(rec.getAbsoluteReturn());
                    enteredRecommendationCount++;
                }

                if (minEntryPrice== null || minEntryPrice.compareTo(rec.getEntryPrice()) > 0) {
                    minEntryPrice = rec.getEntryPrice();
                }
                if (maxEntryPrice == null || maxEntryPrice.compareTo(rec.getEntryPrice()) < 0) {
                    maxEntryPrice = rec.getEntryPrice();
                }
                if (earliestEntryDate == null || earliestEntryDate.isAfter(Objects.requireNonNull(rec.getEntryDate()))) {
                    earliestEntryDate = rec.getEntryDate();
                }
                if (latestRecommendation == null || latestRecommendation.getCreatedAt().isBefore(rec.getCreatedAt())) {
                    latestRecommendation = rec;
                    latestTargetPrice = rec.getTargetPrice();
                    latestTargetDate = rec.getCreatedAt();
                    latestRationale = rec.getRationale();
                    latestTimePeriod = rec.getTimePeriod();
                    latestStopLoss = rec.getStopLoss();
                    latestRating = rec.getRating();
                }

            }
            if(enteredRecommendationCount > 0) {
                avgReturn = avgReturn.divide(new BigDecimal(enteredRecommendationCount), RoundingMode.HALF_UP);
            }

            ConsolidatedBuyRecommendation consolidatedRecommendation = ConsolidatedBuyRecommendation.builder()
                    .stock(stock)
                    .advisor(advisor)
                    .timePeriod(latestTimePeriod)
                    .rating(latestRating)
                    .minEntryPrice(minEntryPrice)
                    .maxEntryPrice(maxEntryPrice)
                    .latestTargetPrice(latestTargetPrice)
                    .latestTargetDate(latestTargetDate)
                    .latestStopLoss(latestStopLoss)
                    .latestRationale(latestRationale)
                    .isActive(true)
                    .avgReturn(avgReturn)
                    .build();

            consolidatedRecommendations.add(consolidatedRecommendation);
        }
        return consolidatedRecommendations;
    }

    public List<Recommendation> getAllRecommendations(@Nullable Long stockId, @Nullable Long advisorId, @Nullable Boolean onlyActive, Integer page, Integer size){
        Recommendation r = new Recommendation();
        if(Boolean.FALSE.equals(onlyActive)){
            r.setIsActive(null);
        }
        r.setAdvisorId(advisorId);
        r.setStockId(stockId);
        return recommendationRepo.findAll(Example.of(r), PageRequest.of(page, size)).getContent();
    }
}
