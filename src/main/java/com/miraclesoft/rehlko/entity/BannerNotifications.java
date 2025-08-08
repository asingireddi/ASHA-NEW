package com.miraclesoft.rehlko.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BannerNotifications {

	@Column("id")
	@Id
	private int id;

	@Column("message")
	private String message;

	@Column("created_at")
	private LocalDateTime createdAt;

	@Column("updated_at")
	private LocalDateTime updatedAt;

	@Column("created_by")
	private String createdBy;

	@Column("modified_by")
	private String modifiedBy;

	@JsonProperty("isactive")
	@Column("is_active")
	private boolean isActive;

}
