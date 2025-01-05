package com.samartha.fyze.securities.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.samartha.fyze.securities.model.Stock;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepo extends JpaRepository<Stock, Long> {

	@Query("SELECT s FROM Stock s WHERE " + "LOWER(s.symbol) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(s.shortName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(s.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	List<Stock> searchStocks(@Param("searchTerm") String searchTerm);

	Optional<Stock> findByExchangeAndSymbol(Stock.Exchange exchange, String symbol);

}
