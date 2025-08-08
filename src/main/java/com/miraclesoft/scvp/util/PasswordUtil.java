package com.miraclesoft.scvp.util;

/**
 * The Class PasswordUtil.
 *
 * @author Narendar Geesidi
 */
public final class PasswordUtil {

    /** The Constant THREE. */
    private static final int THREE = 3;

    /**
     * Encrypt password.
     *
     * @param sourcePassword the source password
     * @return the string
     */
    public static String encryptPassword(final String sourcePassword) {
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

    /**
     * Decrypt password.
     *
     * @param encryptedPassword the encrypted password
     * @return the string
     */
    public static String decryptPassword(final String encryptedPassword) {
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
