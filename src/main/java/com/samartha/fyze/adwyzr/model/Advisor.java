package com.samartha.fyze.adwyzr.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import com.samartha.fyze.common.model.BaseModel;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "advisors")
public class Advisor extends BaseModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "advisor_type", nullable = false)
	private AdvisorType type;

	@Column(name = "is_sebi_registered", nullable = false)
	private Boolean isSebiRegistered;

	@Column(name = "is_nism_certified", nullable = false)
	private Boolean isNismCertified;

	@Nullable
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "social_handles", columnDefinition = "jsonb")
	private List<SocialMediaProfile> socialHandles;

	@Column(name = "emails", columnDefinition = "text[]")
	private List<String> emails = new ArrayList<>();

	@Column(name = "phones", columnDefinition = "text[]")
	private List<String> phones = new ArrayList<>();

	@Builder.Default
	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;

	public enum AdvisorType {

		FIRM, MEDIA, INFLUENCER

	}

}
