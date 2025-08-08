package com.miraclesoft.scvp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class Correlation.
 *
 * @author Priyanka Kolla
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Correlation {
    private String transaction;
    private String value;
}
