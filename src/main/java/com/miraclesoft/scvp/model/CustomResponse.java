package com.miraclesoft.scvp.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class CustomResponse {
	private List<?> data;
	private int totalRecordsCount;
}
