package com.miraclesoft.scvp.util;

import java.io.File;
import java.io.FileInputStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * The Class FileUtility.
 *
 * @author Narendar Geesidi
 */
public class FileUtility {

    /** The logger. */
    private static Logger logger = LogManager.getLogger(FileUtility.class.getName());

    /**
     * Gets the input stream resource.
     *
     * @param file the file
     * @return the input stream resource
     */
    public static ResponseEntity<InputStreamResource> getInputStreamResource(final File file) {
        ResponseEntity<InputStreamResource> response = null;
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            response = ResponseEntity.ok()
                                     .headers(getHttpHeaders())
                                     .contentLength(file.length())
                                     .contentType(MediaType.parseMediaType("application/octet-stream"))
                                     .body(resource);
        }
        catch (Exception exception) {
            logger.log(Level.ERROR, " getInputStreamResource :: " + exception.getMessage());
        }
        return response;
    }

    /**
     * Checks if is file exist.
     *
     * @param path the path
     * @return true, if is file exist
     */
    public static boolean isFileExist(final String path) {
        return new File(path).exists() && new File(path).isFile();
    }

    /**
     * Gets the http headers.
     *
     * @return the http headers
     */
    private static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return headers;
    }
}
