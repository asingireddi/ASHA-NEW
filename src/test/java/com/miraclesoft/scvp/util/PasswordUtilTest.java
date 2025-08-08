package com.miraclesoft.scvp.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * The Class PasswordUtilTest.
 *
 * @author Narendar Geesidi
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class PasswordUtilTest {

    private static final int THREE = 3;

    @Test
    public void encryptPasswordTest() {
        // Given
        final String password = "password";

        // When
        final String encryptedPassword = PasswordUtil.encryptPassword(password);

        // Then
        assertThat(encryptedPassword).isEqualTo(encryptPassword(password));
    }

    @Test
    public void decryptPasswordTest() {
        // Given
        final String password = "#345#300#354#354#366#342#351#309";

        // When
        final String decryptedPassword = PasswordUtil.decryptPassword(password);

        // Then
        assertThat(decryptedPassword).isEqualTo(decryptPassword(password));
    }

    private String encryptPassword(final String sourcePassword) {
        final char[] asciiArray = sourcePassword.toCharArray();
        final int[] encryAsciiArray = new int[sourcePassword.length()];
        for (int i = 0; i < encryAsciiArray.length; i++) {
            encryAsciiArray[i] = ((int) asciiArray[i] + THREE) * THREE;
        }
        final StringBuilder encryptedPassword = new StringBuilder();
        for (int j = 0; j < encryAsciiArray.length; j++) {
            encryptedPassword.append("#" + encryAsciiArray[j]);
        }
        return encryptedPassword.toString();
    }

    private String decryptPassword(final String encryptedPassword) {
        final String[] encryptedPasswordArray = encryptedPassword.split("#");
        final StringBuilder decryptedPwd = new StringBuilder();
        for (int i = 0; i < encryptedPasswordArray.length; i++) {
            if (!encryptedPasswordArray[i].equalsIgnoreCase("")) {
                final int devideBy3 = Integer.parseInt(encryptedPasswordArray[i]) / THREE;
                final int minus3 = devideBy3 - THREE;
                decryptedPwd.append((char) minus3);
            }
        }
        return decryptedPwd.toString();
    }

}
