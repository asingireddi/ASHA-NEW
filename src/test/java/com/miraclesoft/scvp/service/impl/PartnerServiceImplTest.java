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

import javax.servlet.http.HttpServletRequest;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.Partner;
import com.miraclesoft.scvp.security.TokenAuthenticationService;
import com.miraclesoft.scvp.util.DataSourceDataProvider;

/**
 * The Class PartnerServiceImplTest.
 *
 * @author Narendar Geesidi
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings("unchecked")
public class PartnerServiceImplTest {

    @InjectMocks
    private PartnerServiceImpl partnerServiceImpl;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @MockBean
    private TokenAuthenticationService tokenAuthenticationService;

    @MockBean
    private DataSourceDataProvider dataSourceDataProvider;
    
    @Mock
    private HttpServletRequest httpServletRequest;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findAllPartnersTest() {
        // Given
        final Partner partnerOne = buildPartner("Test Id1", "Test name1");
        final Partner partnerTwo = buildPartner("Test Id2", "Test name2");

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(
                getPartnersResultset(Arrays.asList(partnerOne, partnerTwo)));
        when(httpServletRequest.getHeader(Mockito.any())).thenReturn("true");
        when(tokenAuthenticationService.getUserIdfromToken()).thenReturn("10003");
        // Then
        assertEquals(partnerServiceImpl.findAll(Partner.builder()
                                                       .build()).getData(), 1);
    }

    @Test
    public void findAllPartnersWithDataTest() {
        // Given
        final Partner actualPartner = buildPartner("Test Id", "Test name");

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(getPartnersResultset(Arrays.asList(actualPartner)));
        when(httpServletRequest.getHeader(Mockito.any())).thenReturn("true");
        when(tokenAuthenticationService.getUserIdfromToken()).thenReturn("10003");
        // Then
        final List<Partner> exptectedPartners = (List<Partner>) partnerServiceImpl.findAll(actualPartner);
        softly.assertThat(1)
              .isEqualTo(exptectedPartners.size());
		for (final Partner partner : exptectedPartners) {
			softly.assertThat(partner.getPartnerIdentifier()).isEqualTo(actualPartner.getPartnerIdentifier());
			softly.assertThat(partner.getPartnerName()).isEqualTo(actualPartner.getPartnerName());
			softly.assertThat(partner.getCountryCode()).isEqualTo(actualPartner.getCountryCode());
			softly.assertThat(partner.getStatus()).isEqualTo(actualPartner.getStatus());
		}
    }

    @Test
    public void findAllPartnersWhenAllfieldsAreNullExceptpartnerIdTest() {
        // Given
        final Partner actualPartner = buildPartner("Test Id", "Test name");

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(getPartnersResultset(Arrays.asList(actualPartner)));
        when(httpServletRequest.getHeader(Mockito.any())).thenReturn("true");
        when(tokenAuthenticationService.getUserIdfromToken()).thenReturn("10003");
      
        // Then
        final List<Partner> exptectedPartners = (List<Partner>) partnerServiceImpl.findAll(Partner.builder()
                                                                                  .partnerId("Test Id")
                                                                                  .partnerIdentifier("Test Id")
                                                                                  .build());
        softly.assertThat(1)
              .isEqualTo(exptectedPartners.size());
        for (final Partner partner : exptectedPartners) {
            softly.assertThat(partner.getPartnerIdentifier())
                  .isEqualTo(actualPartner.getPartnerIdentifier());
            softly.assertThat(partner.getPartnerName())
                  .isEqualTo(actualPartner.getPartnerName());
            softly.assertThat(partner.getCountryCode())
                  .isEqualTo(actualPartner.getCountryCode());
            softly.assertThat(partner.getStatus())
                  .isEqualTo(actualPartner.getStatus());
        }
    }

    @Test
    public void findOnePartnerTest() {
        // Given
        final Partner actualPartner = buildPartner("Test Id", "Test name");

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(getPartnersResultset(Arrays.asList(actualPartner)));

        // Then
        final Partner expectedPartner = partnerServiceImpl.findOne(actualPartner.getPartnerIdentifier());
        softly.assertThat(expectedPartner.getPartnerIdentifier())
              .isEqualTo(actualPartner.getPartnerIdentifier());
        softly.assertThat(expectedPartner.getPartnerName())
              .isEqualTo(actualPartner.getPartnerName());
        softly.assertThat(expectedPartner.getCountryCode())
              .isEqualTo(actualPartner.getCountryCode());
        softly.assertThat(expectedPartner.getStatus())
              .isEqualTo(actualPartner.getStatus());
    }

    @Test
    public void updatePartnerTest() {
        // Given
        final Partner partner = buildPartner("Test Id", "Test name");

        // When
        when(jdbcTemplate.update(anyString(), (Object) any())).thenReturn(1);

        // Then
        assertEquals(partnerServiceImpl.update(partner), "Partner updated succesfully.");
    }

    @Test
    public void savePartnerTest() {
        // Given
        final Partner partner = buildPartner("Test Id", "Test name");

        // When
        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(0);
        when(jdbcTemplate.update(anyString(), (Object) any())).thenReturn(1);

        // Then
        assertEquals(partnerServiceImpl.save(partner), "Partner added succesfully.");
    }

    @Test
    public void unSaveIfDuplication() {
        // Given
        final Partner partner = buildPartner("Test Id", "Test name");

        // When
        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(1);
        when(jdbcTemplate.update(anyString(), (Object) any())).thenReturn(1);

        // Then
        assertEquals(partnerServiceImpl.save(partner), "Partner Id already existed!");
    }

    @Test
    public void unSavePartnerTest() throws Exception {
        // Given
        final Partner partner = buildPartner("Test Id", "Test name");

        // When
        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(0);
        when(jdbcTemplate.update(anyString(), (Object) any())).thenReturn(0);

        // Then
        assertEquals(partnerServiceImpl.save(partner), "Please try again!");
    }

    private Partner buildPartner(final String partnerId, final String name) {
        return Partner.builder()
                      .partnerId(partnerId)
                      .partnerIdentifier(partnerId)
                      .partnerName(name)
                      .applicationId(name)
                      .internalIdentifier(name)
                      .countryCode("USA")
                      .status("ACTIVE")
                      .build();
    }

    private List<Map<String, Object>> getPartnersResultset(final List<Partner> partners) {
        final List<Map<String, Object>> partnersFromDB = new ArrayList<Map<String, Object>>();
        for (final Partner partner : partners) {
            final Map<String, Object> row = new HashMap<String, Object>();
            row.put("id", partner.getPartnerId());
            row.put("name", partner.getPartnerName());
            row.put("internalidentifier", partner.getInternalIdentifier());
            row.put("applicationid", partner.getApplicationId());
            row.put("state", partner.getCountryCode());
            row.put("status", partner.getStatus());
            partnersFromDB.add(row);
        }
        return partnersFromDB;
    }

}
