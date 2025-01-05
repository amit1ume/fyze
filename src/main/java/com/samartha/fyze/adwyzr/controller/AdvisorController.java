package com.samartha.fyze.adwyzr.controller;

import com.samartha.fyze.adwyzr.dto.base.response.ApiResponse;
import com.samartha.fyze.adwyzr.dto.base.response.ErrorDetails;
import com.samartha.fyze.adwyzr.model.Advisor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.samartha.fyze.adwyzr.service.AdvisorService;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/advisors")
public class AdvisorController {

	private final AdvisorService advisorService;

	AdvisorController(AdvisorService advisorService) {
		this.advisorService = advisorService;
	}

	@PostMapping(value = { "", "/" })
	public ResponseEntity<ApiResponse<Advisor>> saveAdvisor(@RequestBody Advisor advisor) {
		Advisor ad = advisorService.saveOrUpdateAdvisor(advisor);
		return new ResponseEntity<>(
				ApiResponse.<Advisor>builder().data(ad).message("Advisor saved successfully").build(), HttpStatus.OK);
	}

	@GetMapping(value = { "", "/" })
	public ResponseEntity<ApiResponse<Map<String, List<Advisor>>>> getAdvisors(
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer size,
			@RequestParam(required = false) String search) {
		List<Advisor> advisors = null;
		if (Strings.isBlank(search)) {
			advisors = advisorService.getAllActiveAdvisors(page, size);
		}
		else {
			advisors = advisorService.findAllActiveAdvisors(search, page, size);
		}

		Map<String, List<Advisor>> data = Map.of("advisors", advisors);
		return new ResponseEntity<>(ApiResponse.<Map<String, List<Advisor>>>builder().data(data)
				.message("Advisors fetched successfully").build(), HttpStatus.OK);
	}

	@GetMapping("/{advisorId}")
	public ResponseEntity<ApiResponse<Advisor>> getAdvisorById(@PathVariable Long advisorId) {
		Optional<Advisor> advisor = advisorService.findAdvisorById(advisorId);

		if (advisor.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Advisor not found");
		}
		return new ResponseEntity<>(
				ApiResponse.<Advisor>builder().data(advisor.get()).message("Advisor fetched successfully").build(),
				HttpStatus.OK);
	}

}
