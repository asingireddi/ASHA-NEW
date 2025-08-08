package com.miraclesoft.scvp.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.Partner;
import com.miraclesoft.scvp.service.impl.PartnerServiceImpl;

/**
 * The Class PartnerService.
 *
 * @author Priyanka Kolla
 */
@Service
public class PartnerService {

    /** The partner service impl. */
    @Autowired
    private PartnerServiceImpl partnerServiceImpl;

    /**
     * Find all.
     *
     * @param partner the partner
     * @return the list
     */
    public CustomResponse findAll(final Partner partner) {
        return partnerServiceImpl.findAll(partner);
    }

    /**
     * Find one.
     *
     * @param partnerId the partner id
     * @return the partner
     */
    public Partner findOne(final String partnerId) {
        return partnerServiceImpl.findOne(partnerId);
    }

    /**
     * Update.
     *
     * @param partner the partner
     * @return the string
     * @throws DataAccessException the data access exception
     */
    public String update(final Partner partner) throws DataAccessException {
        return partnerServiceImpl.update(partner);
    }

    /**
     * Save.
     *
     * @param partner the partner
     * @return the string
     */
    public String save(final Partner partner) {
        return partnerServiceImpl.save(partner);
    }

    /**
     * Partners.
     *
     * @return the map
     * @throws Exception the exception
     */
    public List<Map<String, String>> partners() throws Exception {
        return partnerServiceImpl.allPartners();
    }

	public ResponseEntity<InputStreamResource> download(Partner partner) throws IOException {
		return partnerServiceImpl.download(partner);
	}

	public String deletePartner(String partnerId) {
		return partnerServiceImpl.deletePartner(partnerId);
	}

}
