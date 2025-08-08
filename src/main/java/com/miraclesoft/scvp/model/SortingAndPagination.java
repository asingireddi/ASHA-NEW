package com.miraclesoft.scvp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SortingAndPagination {
	private String sortField;
	private String sortOrder;
	private int limit;
	private int offSet;
	private Boolean countFlag;
}
