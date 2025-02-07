package com.samartha.fyze.adwyzr.controller;

import com.samartha.fyze.adwyzr.dto.ConsolidatedBuyRecommendation;
import com.samartha.fyze.adwyzr.dto.base.response.ApiResponse;
import com.samartha.fyze.adwyzr.dto.recommendation.RecommendationCreationUpdationRequest;
import com.samartha.fyze.adwyzr.model.Recommendation;
import com.samartha.fyze.adwyzr.service.RecommendationService;
import com.samartha.fyze.common.exception.GlobalExceptionHandler;
import com.samartha.fyze.common.service.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/recommendations")
public class RecommendationController {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	private final RecommendationService recommendationService;
	private final WebService webService;

	RecommendationController(RecommendationService recommendationService,WebService webService) {
		this.recommendationService = recommendationService;
		this.webService = webService;
	}

	@PostMapping(value = { "", "/" })
	public ResponseEntity<ApiResponse<Recommendation>> saveRecommendation(
			@RequestBody RecommendationCreationUpdationRequest recommendationCreationUpdationRequest) {
		Recommendation savedRecommendation = recommendationService
				.saveRecommendation(recommendationCreationUpdationRequest);
		return new ResponseEntity<>(ApiResponse.<Recommendation>builder().data(savedRecommendation)
				.message("Recommendation saved successfully").build(), HttpStatus.OK);
	}

	@PutMapping(value = { "", "/" })
	public ResponseEntity<ApiResponse<Recommendation>> updateRecommendation(
			@RequestBody Recommendation recommendation) {
		Recommendation savedRecommendation = recommendationService.updateRecommendation(recommendation);
		return new ResponseEntity<>(ApiResponse.<Recommendation>builder().data(savedRecommendation)
				.message("Recommendation saved successfully").build(), HttpStatus.OK);
	}

	@GetMapping("/advisor/active/consolidated")
	public ResponseEntity<ApiResponse<Map<String, List<ConsolidatedBuyRecommendation>>>> findLastKActiveStockRecommendationsByAdvisor(
			@RequestParam Long advisorId, @RequestParam(required = false, defaultValue = "10") Integer size) {
		List<ConsolidatedBuyRecommendation> recommendationList = recommendationService
				.getLastKActiveStockRecommendationsByAdvisor(advisorId, size);
		Map<String, List<ConsolidatedBuyRecommendation>> data = Map.of("recommendations", recommendationList);
		return new ResponseEntity<>(ApiResponse.<Map<String, List<ConsolidatedBuyRecommendation>>>builder().data(data)
				.message("Recommendations fetched successfully").build(), HttpStatus.OK);
	}

	@GetMapping(value = { "", "/" })
	public ResponseEntity<ApiResponse<Map<String, List<Recommendation>>>> findAllRecommendations(
			@RequestParam(required = false) Long advisorId, @RequestParam(required = false) Long stockId,
			@RequestParam(required = false, defaultValue = "true") Boolean onlyActive,
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer size) {
		List<Recommendation> recommendationList = recommendationService.getAllRecommendations(stockId, advisorId,
				onlyActive, page, size);
		Map<String, List<Recommendation>> data = Map.of("recommendations", recommendationList);
		String webResponse = webService.getDailyBasisPrices();
		log.info("asdfg inside controller webResponse="+webResponse);
		return new ResponseEntity<>(ApiResponse.<Map<String, List<Recommendation>>>builder().data(data)
				.message("Recommendations fetched successfully").build(), HttpStatus.OK);
	}

	@GetMapping("/stock/active/consolidated")
	public ResponseEntity<ApiResponse<Map<String, List<ConsolidatedBuyRecommendation>>>> findLastKAdvisorActiveStockRecommendationsForGivenStock(
			@RequestParam Long stockId, @RequestParam(required = false, defaultValue = "10") Integer size) {
		List<ConsolidatedBuyRecommendation> recommendationList = recommendationService
				.getLastKAdvisorActiveRecommendationForGivenStock(stockId, size);
		Map<String, List<ConsolidatedBuyRecommendation>> data = Map.of("recommendations", recommendationList);

		return new ResponseEntity<>(ApiResponse.<Map<String, List<ConsolidatedBuyRecommendation>>>builder().data(data)
				.message("Recommendations fetched successfully").build(), HttpStatus.OK);
	}

}
