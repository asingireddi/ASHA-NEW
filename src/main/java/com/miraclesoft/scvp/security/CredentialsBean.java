package com.miraclesoft.scvp.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class CredentialsBean.
 *
 * @author Narendar Geesidi
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CredentialsBean {
    private String loginId;
    private String password;
}
