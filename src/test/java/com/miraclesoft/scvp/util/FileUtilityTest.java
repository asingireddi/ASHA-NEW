package com.miraclesoft.scvp.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * The Class FileUtilityTest.
 *
 * @author Narendar Geesidi
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class FileUtilityTest {

    @Test
    public void fileExistsTest() {
        // Given
        final String filePath = "src/test/resources/TestFile.txt";

        // When
        final boolean result = FileUtility.isFileExist(filePath);

        // Then
        assertThat(result).isEqualTo(isFileExist(filePath));
    }

    @Test
    public void fileNotExistsTest() {
        // Given
        final String filePath = "src/test/resources/test.txt";

        // When
        final boolean result = FileUtility.isFileExist(filePath);

        // Then
        assertThat(result).isEqualTo(isFileExist(filePath));
    }

    private boolean isFileExist(final String path) {
        return new File(path).exists() && new File(path).isFile();
    }
}
