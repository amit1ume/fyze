package com.samartha.fyze.adwyzr.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SocialMediaProfile {

	@Enumerated(EnumType.STRING)
	@Column(name = "platform", nullable = false)
	private SocialMediaPlatform platform;

	@Column(name = "followers")
	private Long followers;

	@Column(name = "handle")
	private String handle;

	public enum SocialMediaPlatform {

		TWITTER, FACEBOOK, INSTAGRAM, LINKEDIN, TELEGRAM, WHATSAPP, WEBSITE, YOUTUBE

	}

}
