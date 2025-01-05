package com.samartha.fyze.adwyzr.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.samartha.fyze.adwyzr.model.Advisor;

@Repository
public interface AdvisorRepo extends JpaRepository<Advisor, Long> {

	Page<Advisor> findByIsActive(Boolean isActive, Pageable pageable);

	Page<Advisor> findByIsActiveAndNameContainingIgnoreCase(Boolean isActive, String searchTerm, Pageable pageable);

}
