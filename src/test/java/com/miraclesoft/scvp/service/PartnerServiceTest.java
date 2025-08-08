package com.miraclesoft.scvp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.Partner;
import com.miraclesoft.scvp.service.impl.PartnerServiceImpl;

/**
 * The Test PartnerServiceTest
 * 
 * @author Narendar Geesidi
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class PartnerServiceTest {

    @InjectMocks
    private PartnerService partnerService;

    @Mock
    private PartnerServiceImpl partnerServiceImpl;

    @Test
    public void shouldSearchPartners() {
        // Given
        final Partner partner = buildPartner();
        final List<Partner> expectedPartners = Arrays.asList(partner);

        // When
        doReturn(expectedPartners).when(partnerServiceImpl)
                                  .findAll(partner);

        // Then
        assertThat(partnerService.findAll(partner)).isEqualTo(expectedPartners);
    }

    @Test
    public void shouldFindPartners() {
        // Given
        final Partner partner = buildPartner();

        // When
        doReturn(partner).when(partnerServiceImpl)
                         .findOne(partner.getPartnerId());

        // Then
        assertThat(partnerService.findOne(partner.getPartnerId())).isEqualTo(partner);
    }

    @Test
    public void shouldAddPartner() {
        // Given
        final Partner partner = buildPartner();

        // When
        doReturn("Partner added succesfully.").when(partnerServiceImpl)
                                              .save(partner);

        // Then
        assertThat(partnerService.save(partner)).isEqualTo("Partner added succesfully.");
    }

    @Test
    public void shouldUpdatePartner() {
        // Given
        Partner partner = buildPartner();

        // When
        doReturn("Partner updated succesfully.").when(partnerServiceImpl)
                                                .update(partner);

        // Then
        assertThat(partnerService.update(partner)).isEqualTo("Partner updated succesfully.");
    }

    @Test
    public void shouldFindAllPartners() throws Exception {
        // Given
        final Map<String, String> partnersMap = new TreeMap<String, String>();
        partnersMap.put("id1", "name1");
        partnersMap.put("id2", "name2");

        // When
        doReturn(partnersMap).when(partnerServiceImpl)
                             .allPartners();

        // Then
        assertEquals(partnerService.partners(), partnersMap);
    }

    private Partner buildPartner() {
        return Partner.builder()
                      .partnerId("P1")
                      .partnerName("Test")
                      .internalIdentifier("One")
                      .partnerIdentifier("PI")
                      .build();
    }

}
