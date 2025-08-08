package com.miraclesoft.rehlko.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("m_user_roles")
public class MUserRole {
	@Id
	private Long userId;
	private Integer roleId;
	private Integer priority;
	private String activatedBy;
	private LocalDateTime dateActivated;
}