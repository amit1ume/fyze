package com.samartha.fyze.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class BaseModel extends BaseClass {

	@Column(nullable = false, updatable = false, name = "created_at")
	@Setter(AccessLevel.NONE)
	private Instant createdAt;

	@Column(nullable = false, name = "updated_at")
	@Setter(AccessLevel.NONE)
	private Instant updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = Instant.now();
		updatedAt = createdAt;
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = Instant.now();
	}

}
