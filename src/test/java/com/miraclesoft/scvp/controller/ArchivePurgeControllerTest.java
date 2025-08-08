package com.miraclesoft.scvp.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.ArchivePurge;
import com.miraclesoft.scvp.service.ArchivePurgeService;

/**
 * The Class ArchivePurgeControllerTest.
 * 
 * @author Narendar Geesidi
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ArchivePurgeControllerTest {

   @Mock
    private ArchivePurgeService archivePurgeService;

    @InjectMocks
    private ArchivePurgeController archivePurgeController = new ArchivePurgeController();

    @Test
    public void isArchivePurgeExisted() throws Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);
        final String response = archivePurge.getTransaction() + " config already existed!";

        // When
        when(archivePurgeService.save(archivePurge)).thenReturn(response);

        // Then
        assertEquals(archivePurgeController.save(archivePurge), response);
    }
    @Test
    public void shouldFindOneArchivePurge() throws Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);

        // When
        when(archivePurgeService.findOne(1)).thenReturn(archivePurge);

        // Then
        assertEquals(archivePurgeController.findOne(1),archivePurge);
    }
    
    @Test
    public void shouldFindAllArchivePurge() throws Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);
        List<ArchivePurge> listArchive = new ArrayList<>();
        listArchive.add(archivePurge);
        

        // When
        when(archivePurgeService.findAll(archivePurge.getTransaction(),archivePurge.getArchiveDays(),archivePurge.getPurgeDays())).thenReturn(listArchive);

        // Then
        assertEquals(archivePurgeController.findAll(archivePurge.getTransaction(),archivePurge.getArchiveDays(),archivePurge.getPurgeDays()), listArchive);
    }
    @Test
    public void shouldSaveArchivePurge() throws Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);
        final String response = archivePurge.getTransaction() + " archive/purge days added succesfully.";

        // When
        when(archivePurgeService.save(Mockito.any())).thenReturn(response);

        // Then
        assertEquals(archivePurgeController.save(archivePurge), response);
    }

    @Test
    public void shouldUpdateArchivePurge() throws DataAccessException, Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);
        final String response = "Updated succesfully.";

        // When
        when(archivePurgeService.update(archivePurge)).thenReturn(response);

        // Then
        assertEquals(archivePurgeController.update(archivePurge), response);
    }

   /* @Test
    public void shouldFindArchivePurge() throws Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);

        // When
        when(archivePurgeService.findOne(archivePurge.getId())).thenReturn(archivePurge);

        // Then
        assertEquals(archivePurge, archivePurgeController.findOne(archivePurge.getId()));
    }*/

  /*  @Test
    public void shouldSearchArchivePurge() throws Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);
        final List<ArchivePurge> archivePurges = Arrays.asList(archivePurge);

        // When
        when(archivePurgeService.findAll(Mockito.any(),Mockito.eq(0), Mockito.eq(0))).thenReturn(archivePurges);

        // Then
        assertEquals(archivePurgeController.findAll(archivePurge.getTransaction(),30,60), archivePurges);
    }*/
 
    private ArchivePurge buildArchivePurge(final String transaction, final int archiveDays, final int purgeDays) {
        return ArchivePurge.builder()
                           .id(1)
                           .transaction(transaction)
                           .archiveDays(archiveDays)
                           .purgeDays(purgeDays)
                           .build();
    }

}
