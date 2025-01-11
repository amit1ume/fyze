package com.samartha.fyze.securities.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Screener {

	private String link;

	private ScreenerPlatform platform;

	public enum ScreenerPlatform {

		TradingView, Screener

	}

}
