package com.samartha.fyze.securities.service;

import com.samartha.fyze.securities.model.Stock;
import com.samartha.fyze.securities.repo.StockRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {

	private final StockRepo repo;

	StockService(StockRepo repo) {
		this.repo = repo;
	}

	public Stock saveStock(Stock stock) {
		return repo.save(stock);
	}

	public Optional<Stock> getStock(Long stockId) {
		return repo.findById(stockId);
	}

	public Optional<Stock> getStockByTicker(String stockTicker) {
		String[] arr = stockTicker.split(":");
		Stock.Exchange exchange = Stock.Exchange.valueOf(arr[0]);
		String symbol = arr[1];
		return repo.findByExchangeAndSymbol(exchange, symbol);
	}

	public List<Stock> getAllStocks(int page, int size) {
		return repo.findAll(PageRequest.of(page, size)).getContent();
	}

	public List<Stock> searchStock(String searchTerm) {
		return repo.searchStocks(searchTerm);
	}

}
