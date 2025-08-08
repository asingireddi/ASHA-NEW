package com.miraclesoft.scvp.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.Partner;
import com.miraclesoft.scvp.service.PartnerService;

/**
 * The Class PartnerControllerTest.java
 * 
 * @author Priyanka Kolla
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class PartnerControllerTest {

    @Mock
    private PartnerService partnerService;

    @InjectMocks
    private PartnerController partnerController = new PartnerController();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldSearchPartners() {
        // Given
        final Partner partner = buildPartner();
        final List<Partner> partners = Arrays.asList(partner);
        int count = 0;
        final CustomResponse customResponse = new CustomResponse(partners,count);

        // When
        when(partnerService.findAll(partner)).thenReturn(customResponse);

        // Then
        assertEquals(customResponse, partnerController.partnerSearch(partner));
    }

    @Test
    public void shouldFindPartner() {
        // Given
        final Partner partner = buildPartner();

        // When
        when(partnerService.findOne(partner.getPartnerId())).thenReturn(partner);

        // Then
        assertEquals(partner, partnerController.partnerInfo(partner.getPartnerId()));
    }

    @Test
    public void shouldAddPartner() {
        // Given
        final Partner partner = buildPartner();
        final String response = "Partner added succesfully.";

        // When
        when(partnerService.save(partner)).thenReturn(response);

        // Then
        assertEquals(response, partnerController.addPartner(partner));
    }

    @Test
    public void shouldUpdatePartner() {
        // Given
        final Partner partner = buildPartner();
        final String response = "Partner updated succesfully.";

        // When
        when(partnerService.update(partner)).thenReturn(response);

        // Then
        assertEquals(partnerController.updatePartner(partner), response);
    }

    @Test
    public void shouldFindAllPartnersMap() throws Exception {
        // Given
        List<Map<String,String>> response = new ArrayList<>();
        final Map<String, String> partnersMap = new TreeMap<String, String>();
        partnersMap.put("id1", "name1");
        partnersMap.put("id2", "name2");
        response.add(partnersMap);
        // When
        when(partnerService.partners()).thenReturn(response);

        // Then
        assertEquals(partnerController.partners(), response);
    }

    private Partner buildPartner() {
        return Partner.builder()
                      .partnerId("T1")
                      .partnerIdentifier("T1")
                      .partnerName("TEST")
                      .build();
    }
}
