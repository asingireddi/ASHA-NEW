package com.miraclesoft.scvp.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.ArchivePurge;

/**
 * The Class ArchivePurgeServiceImplTest.
 *
 * @author Narendar Geesidi
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings("unchecked")
public class ArchivePurgeServiceImplTest {

    @Autowired
    private ArchivePurgeServiceImpl archivePurgeServiceImpl;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void findAllArchivePurge() throws Exception {
        // Given
        final ArchivePurge archivePurgeOne = buildArchivePurge("850", 90, 180);
        final ArchivePurge archivePurgeTwo = buildArchivePurge("856", 30, 60);

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(
                getArchivePurgesResultset(Arrays.asList(archivePurgeOne, archivePurgeTwo)));

        // Then
        assertEquals(archivePurgeServiceImpl.findAll(null,0,0)
                                            .size(),
                2);
    }

    
    @Test
    public void findAllArchivePurgeByTransaction() throws Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(getArchivePurgesResultset(Arrays.asList(archivePurge)));

        // Then
        final List<ArchivePurge> exptectedPartners = archivePurgeServiceImpl.findAll(archivePurge.getTransaction(),30,60);
        softly.assertThat(1)
              .isEqualTo(exptectedPartners.size());
        for (final ArchivePurge ap : exptectedPartners) {
            softly.assertThat(ap.getId())
                  .isEqualTo(archivePurge.getId());
            softly.assertThat(ap.getTransaction())
                  .isEqualTo(archivePurge.getTransaction());
            softly.assertThat(ap.getArchiveDays())
                  .isEqualTo(archivePurge.getArchiveDays());
            softly.assertThat(ap.getPurgeDays())
                  .isEqualTo(archivePurge.getPurgeDays());
        }
    }

    @Test
    public void findAllArchivePurgeWhenSearchFieldIsNull() throws Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(getArchivePurgesResultset(Arrays.asList(archivePurge)));

        // Then
        final List<ArchivePurge> exptectedPartners = archivePurgeServiceImpl.findAll(archivePurge.getTransaction(),30,60);
        softly.assertThat(1)
              .isEqualTo(exptectedPartners.size());
        for (final ArchivePurge ap : exptectedPartners) {
            softly.assertThat(ap.getId())
                  .isEqualTo(archivePurge.getId());
            softly.assertThat(ap.getTransaction())
                  .isEqualTo(archivePurge.getTransaction());
            softly.assertThat(ap.getArchiveDays())
                  .isEqualTo(archivePurge.getArchiveDays());
            softly.assertThat(ap.getPurgeDays())
                  .isEqualTo(archivePurge.getPurgeDays());
        }
    }

    @Test
    public void findOneArchivePurge() throws Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(getArchivePurgesResultset(Arrays.asList(archivePurge)));

        // Then
        final ArchivePurge ap = archivePurgeServiceImpl.findOne(archivePurge.getId());
        softly.assertThat(ap.getId())
              .isEqualTo(archivePurge.getId());
        softly.assertThat(ap.getTransaction())
              .isEqualTo(archivePurge.getTransaction());
        softly.assertThat(ap.getArchiveDays())
              .isEqualTo(archivePurge.getArchiveDays());
        softly.assertThat(ap.getPurgeDays())
              .isEqualTo(archivePurge.getPurgeDays());
    }

    @Test
    public void updateArchivePurge() throws DataAccessException, Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);

        // When
        when(jdbcTemplate.update(anyString(), (Object) any())).thenReturn(1);

        // Then
        assertEquals(archivePurgeServiceImpl.update(archivePurge), "Updated succesfully.");
    }

    @Test
    public void saveArchivePurge() throws Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);

        // When
        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(0);
        when(jdbcTemplate.update(anyString(), (Object) any())).thenReturn(1);

        // Then
        assertEquals(archivePurgeServiceImpl.save(archivePurge),
                archivePurge.getTransaction() + " archive/purge days added succesfully.");
    }

    @Test
    public void unSaveArchivePurgeIfDuplication() throws Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);

        // When
        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(1);

        // Then
        assertEquals(archivePurgeServiceImpl.save(archivePurge),
                archivePurge.getTransaction() + " config already existed!");
    }

    @Test
    public void unSaveArchivePurgeTest() throws Exception {
        // Given
        final ArchivePurge archivePurge = buildArchivePurge("850", 90, 180);

        // When
        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(0);

        // Then
        assertEquals(archivePurgeServiceImpl.save(archivePurge), "Please try again!");
    }

    private ArchivePurge buildArchivePurge(final String transaction, final int archiveDays, final int purgeDays) {
        return ArchivePurge.builder()
                           .id(1)
                           .transaction(transaction)
                           .archiveDays(archiveDays)
                           .purgeDays(purgeDays)
                           .build();
    }

    private List<Map<String, Object>> getArchivePurgesResultset(final List<ArchivePurge> archivePurges) {
        final List<Map<String, Object>> archivePurgesFromDB = new ArrayList<Map<String, Object>>();
        for (final ArchivePurge archivePurge : archivePurges) {
            final Map<String, Object> row = new HashMap<String, Object>();
            row.put("id", archivePurge.getId());
            row.put("transaction", archivePurge.getTransaction());
            row.put("archive_days", archivePurge.getArchiveDays());
            row.put("purge_days", archivePurge.getPurgeDays());
            archivePurgesFromDB.add(row);
        }
        return archivePurgesFromDB;
    }
    
    

}
