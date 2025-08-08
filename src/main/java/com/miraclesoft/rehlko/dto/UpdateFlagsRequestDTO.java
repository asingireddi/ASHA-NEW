package com.miraclesoft.rehlko.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFlagsRequestDTO {
    private List<String> correlationKey1List;
    private String type;       // "trash" or "archive"
    private boolean flagValue; // true or false
}
