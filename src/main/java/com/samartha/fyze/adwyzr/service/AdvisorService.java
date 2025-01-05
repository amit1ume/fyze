package com.samartha.fyze.adwyzr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.samartha.fyze.adwyzr.model.Advisor;
import com.samartha.fyze.adwyzr.repo.AdvisorRepo;

import jakarta.annotation.Nullable;

@Service
public class AdvisorService {

	private final AdvisorRepo advisorRepo;

	AdvisorService(AdvisorRepo advisorRepo) {
		this.advisorRepo = advisorRepo;
	}

	public List<Advisor> getAllActiveAdvisors(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Advisor> advisors = advisorRepo.findByIsActive(true, pageable);

		return advisors.getContent();
	}

	public List<Advisor> findAllActiveAdvisors(String searchTerm, int page, int size) {
		return advisorRepo.findByIsActiveAndNameContainingIgnoreCase(true, searchTerm, PageRequest.of(page, size))
				.getContent();
	}

	@Nullable
	public Optional<Advisor> findAdvisorById(Long advisorId) {
		return advisorRepo.findById(advisorId);
	}

	public Advisor saveOrUpdateAdvisor(Advisor advisor) {
		return advisorRepo.save(advisor);
	}

}
