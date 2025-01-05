package com.samartha.fyze.securities.controller;

import com.samartha.fyze.adwyzr.dto.base.response.ApiResponse;
import com.samartha.fyze.securities.model.Stock;
import com.samartha.fyze.securities.service.StockService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/stocks")
public class StockController {

	private final StockService stockService;

	StockController(StockService stockService) {
		this.stockService = stockService;
	}

	@PostMapping(value = { "", "/" })
	public ResponseEntity<ApiResponse<Stock>> saveStock(@RequestBody Stock stock) {
		Stock savedStock = stockService.saveStock(stock);
		return new ResponseEntity<>(
				ApiResponse.<Stock>builder().message("Stock saved successfully").data(savedStock).build(),
				HttpStatus.OK);
	}

	@GetMapping(value = { "", "/" })
	public ResponseEntity<ApiResponse<Map<String, List<Stock>>>> getAllStocks(
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer size,
			@RequestParam(required = false) String search) {
		List<Stock> stocks = Strings.isBlank(search) ? stockService.getAllStocks(page, size)
				: stockService.searchStock(search);
		Map<String, List<Stock>> data = Map.of("stocks", stocks);
		return new ResponseEntity<>(
				ApiResponse.<Map<String, List<Stock>>>builder().message("Stock saved successfully").data(data).build(),
				HttpStatus.OK);
	}

	@GetMapping(value = { "/{stockTicker}" })
	public ResponseEntity<ApiResponse<Stock>> getStockById(@PathVariable String stockTicker) {
		Optional<Stock> stock = stockService.getStockByTicker(stockTicker);
		if (stock.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found");
		}
		return new ResponseEntity<>(
				ApiResponse.<Stock>builder().message("Stock fetched successfully").data(stock.get()).build(),
				HttpStatus.OK);
	}

}
