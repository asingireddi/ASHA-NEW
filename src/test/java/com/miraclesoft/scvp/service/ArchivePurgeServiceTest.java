package com.miraclesoft.scvp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.ArchivePurge;
import com.miraclesoft.scvp.service.impl.ArchivePurgeServiceImpl;

/**
 * The Test ArchivePurgeServiceTest
 * 
 * @author Narendar Geesidi
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ArchivePurgeServiceTest {

    @InjectMocks
    private ArchivePurgeService archivePurgeService;

    @Mock
    private ArchivePurgeServiceImpl archivePurgeServiceImpl;

    @Test
    public void shouldSaveArchivePurge() throws Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);
        final String actual = archivePurge.getTransaction() + " archive/purge days added succesfully.";

        // When
        doReturn(actual).when(archivePurgeServiceImpl)
                        .save(archivePurge);

        // Then
        assertThat(archivePurgeService.save(archivePurge)).isEqualTo(actual);
    }

    @Test
    public void shouldUpdateArchivePurge() throws Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);
        final String actual = "Updated succesfully.";

        // When
        doReturn(actual).when(archivePurgeServiceImpl)
                        .update(archivePurge);

        // Then
        assertThat(archivePurgeService.update(archivePurge)).isEqualTo(actual);
    }

    @Test
    public void shouldFindArchivePurge() throws Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);

        // When
        doReturn(archivePurge).when(archivePurgeServiceImpl)
                              .findOne(archivePurge.getId());

        // Then
        assertEquals(archivePurgeService.findOne(archivePurge.getId()), archivePurge);
    }

    @Test
    public void shouldSearchArchivePurge() throws Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);
        final List<ArchivePurge> archivePurges = Arrays.asList(archivePurge);

        // When
        doReturn(archivePurges).when(archivePurgeServiceImpl)
                               .findAll(archivePurge.getTransaction(),30,60);

        // Then
        assertThat(archivePurgeService.findAll(archivePurge.getTransaction(),30,60)).isEqualTo(archivePurges);
    }

    private ArchivePurge buildArchivePurge(final String transaction, final int archiveDays, final int purgeDays) {
        return ArchivePurge.builder()
                           .id(1)
                           .transaction(transaction)
                           .archiveDays(archiveDays)
                           .purgeDays(purgeDays)
                           .build();
    }

}
