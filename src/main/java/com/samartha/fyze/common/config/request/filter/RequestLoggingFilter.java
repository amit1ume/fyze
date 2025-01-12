package com.samartha.fyze.common.config.request.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samartha.fyze.common.constants.APIConstants;
import com.samartha.fyze.common.constants.LogConstants;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Configuration
public class RequestLoggingFilter {

	private static final int MAX_BODY_BYTES = 15 * 1024; // 15Kb

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private static final List<String> SUPPORTED_CONTENT_TYPES = Arrays.asList(MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE, MediaType.TEXT_HTML_VALUE,
			MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

	@Bean
	public FilterRegistrationBean<Filter> loggingFilterRegistration() {
		FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();

		registrationBean.setFilter(new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
					throws IOException, ServletException {
				long startTime = System.currentTimeMillis();
				try {
					ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
					ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

					// Setting requestId, userId header and mdc keys
					String requestId = Optional.ofNullable(request.getHeader(APIConstants.RequestHeaders.REQUEST_ID))
							.orElse(UUID.randomUUID().toString());
					MDC.put(LogConstants.MdcKeys.REQUEST_ID, requestId);
					response.setHeader(APIConstants.RequestHeaders.REQUEST_ID, requestId); // Can
																							// be
																							// used
																							// by
																							// frontend
																							// while
																							// raising
																							// clevertap
																							// or
																							// similar
																							// events

					log.atInfo().setMessage("Incoming request")
							.addKeyValue(LogConstants.KVPFields.METHOD, requestWrapper.getMethod())
							.addKeyValue(LogConstants.KVPFields.URL, requestWrapper.getRequestURI()).log();

					chain.doFilter(requestWrapper, responseWrapper); // Continue with the
																		// request-response
																		// chain

					if (responseWrapper.getStatus() >= 400) {
						logErrorDetails(requestWrapper, responseWrapper);
					}
					responseWrapper.copyBodyToResponse();
				}
				catch (Exception e) {
					log.atWarn().setMessage("Error in RequestLoggingFilter : " + e.getMessage()).setCause(e).log();
				}
				finally {
					log.atInfo().setMessage("Outgoing response")
							.addKeyValue(LogConstants.KVPFields.METHOD, request.getMethod())
							.addKeyValue(LogConstants.KVPFields.URL, request.getRequestURI())
							.addKeyValue(LogConstants.KVPFields.RESPONSE_STATUS, response.getStatus())
							.addKeyValue(LogConstants.KVPFields.DURATION, System.currentTimeMillis() - startTime).log();
					MDC.clear(); // Clean up MDC after request processing
				}
			}

			private Map<String, List<String>> extractHeaders(HttpServletRequest request) {
				Map<String, List<String>> headers = new HashMap<>();
				Enumeration<String> headerNames = request.getHeaderNames();
				while (headerNames.hasMoreElements()) {
					String headerName = headerNames.nextElement();
					headers.put(headerName, Collections.list(request.getHeaders(headerName)));
				}
				return headers;
			}

			private JsonNode getBody(byte[] bodyBytes, String contentType, Charset charset) {
				if (bodyBytes == null || bodyBytes.length == 0) {
					return null;
				}
				if (!isSupportedContentType(contentType)) {
					return objectMapper.valueToTree("LoggingNotSupported contentType:" + contentType);
				}
				try {
					if (MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType)
							&& bodyBytes.length < MAX_BODY_BYTES) {
						return objectMapper
								.readTree(new String(bodyBytes, charset != null ? charset : StandardCharsets.UTF_8));
					}
					else {
						byte[] truncatedBytes = bodyBytes.length <= MAX_BODY_BYTES ? bodyBytes
								: Arrays.copyOf(bodyBytes, MAX_BODY_BYTES);
						String truncatedStr = new String(truncatedBytes,
								charset != null ? charset : StandardCharsets.UTF_8);
						return objectMapper
								.valueToTree(truncatedStr + "...[TRUNCATED(" + bodyBytes.length + " bytes)]");
					}
				}
				catch (Exception e) {
					log.warn("RequestFilterConfig : Unable to read body", e);
					return null;
				}
			}

			private boolean isSupportedContentType(String contentType) {
				return contentType != null && SUPPORTED_CONTENT_TYPES.stream().anyMatch(contentType::contains);
			}

			private void logErrorDetails(ContentCachingRequestWrapper requestWrapper,
					ContentCachingResponseWrapper responseWrapper) {
				Object requestBody = getBody(requestWrapper.getContentAsByteArray(), requestWrapper.getContentType(),
						Charset.forName(requestWrapper.getCharacterEncoding()));
				Object responseBody = getBody(responseWrapper.getContentAsByteArray(), responseWrapper.getContentType(),
						Charset.forName(responseWrapper.getCharacterEncoding()));
				// Pls note : If request and response were never read by Spring, those
				// values will be logged as null due to ContentCachingResponseWrapper
				// behavior.
				log.atWarn().setMessage("API Error")
						.addKeyValue(LogConstants.KVPFields.URL, requestWrapper.getRequestURI())
						.addKeyValue(LogConstants.KVPFields.REQUEST_PARAMS, requestWrapper.getQueryString())
						.addKeyValue(LogConstants.KVPFields.REQUEST_BODY, requestBody)
						.addKeyValue(LogConstants.KVPFields.REQUEST_HEADERS, extractHeaders(requestWrapper))
						.addKeyValue(LogConstants.KVPFields.RESPONSE_STATUS, responseWrapper.getStatus())
						.addKeyValue(LogConstants.KVPFields.RESPONSE_BODY, responseBody).log();
			}
		});

		registrationBean.addUrlPatterns("/*"); // Apply to all URLs or specify patterns
		registrationBean.setOrder(1); // Set filter order if needed
		return registrationBean;
	}

}
