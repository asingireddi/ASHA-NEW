package com.miraclesoft.rehlko.dto;

import java.util.List;

import com.miraclesoft.rehlko.entity.Details820;
import com.miraclesoft.rehlko.entity.Inbox820;

import lombok.Data;

@Data
public class Inbox820WithDetailsResponse {
	private List<Inbox820> inbox820List;
	private List<Details820> details820List;
}
