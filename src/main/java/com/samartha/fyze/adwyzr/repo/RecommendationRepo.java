package com.samartha.fyze.adwyzr.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.samartha.fyze.adwyzr.model.Recommendation;

@Repository
public interface RecommendationRepo extends JpaRepository<Recommendation, String> {

	@Query(value = """
			    WITH ranked_stocks AS (
			      SELECT
			          stock_id,
			          created_at,
			          ROW_NUMBER() OVER (PARTITION BY stock_id ORDER BY created_at DESC) AS rn
			      FROM recommendations
			      WHERE is_active = true
			      AND advisor_id = :advisorId
			    ),
			    latest_stocks AS (
			      SELECT stock_id
			      FROM ranked_stocks
			      WHERE rn = 1
			      ORDER BY created_at DESC
			      LIMIT :k
			   )
			  SELECT r.*
			  FROM recommendations r
			  WHERE r.stock_id IN (SELECT stock_id FROM latest_stocks)
			  AND r.advisor_id = :advisorId
			  AND r.is_active = true
			  ORDER BY r.created_at DESC;
			""", nativeQuery = true)
	List<Recommendation> findLastKActiveStockRecommendationsByAdvisorId(@Param("advisorId") Long advisorId,
			@Param("k") int k);

	@Query(value = """
			    WITH ranked_advisor AS (
			      SELECT
			          advisor_id,
			          created_at,
			          ROW_NUMBER() OVER (PARTITION BY advisor_id ORDER BY created_at DESC) AS rn
			      FROM recommendations
			      WHERE is_active = true
			      AND stock_id = :stockId
			    ),
			    latest_advisors AS (
			      SELECT advisor_id
			      FROM ranked_advisor
			      WHERE rn = 1
			      ORDER BY created_at DESC
			      LIMIT :k
			   )
			  SELECT r.*
			  FROM recommendations r
			  WHERE r.advisor_id IN (SELECT advisor_id FROM latest_advisors)
			  AND r.stock_id = :stockId
			  AND r.is_active = true
			  ORDER BY r.created_at DESC;
			""", nativeQuery = true)
	List<Recommendation> findLastKAdvisorActiveRecommendationForStockId(@Param("stockId") Long stockId,
			@Param("k") int k);

	List<Recommendation> findByStockIdAndAdvisorIdAndIsActiveTrueAndRatingIn(Long stockId, Long advisorId,
			List<Recommendation.Rating> ratings);

}
