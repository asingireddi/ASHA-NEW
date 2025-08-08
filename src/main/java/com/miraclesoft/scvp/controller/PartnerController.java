package com.miraclesoft.scvp.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.Partner;
import com.miraclesoft.scvp.service.PartnerService;

/**
 * The Class PartnerController.
 *
 * @author Priyanka Kolla
 */
@RestController
@RequestMapping("/partner")
public class PartnerController {

    /** The partner service. */
    @Autowired
    private PartnerService partnerService;

    /**
     * Partner search.
     *
     * @param partner the partner
     * @return the list
     */
    @PostMapping("/search")
    public CustomResponse partnerSearch(@RequestBody final Partner partner) {
        return partnerService.findAll(partner);
    }

    /**
     * Partner info.
     *
     * @param partnerId the partner id
     * @return the partner
     */
    @GetMapping("/info/{partnerId}")
    public Partner partnerInfo(@PathVariable final String partnerId) {
        return partnerService.findOne(partnerId);
    }

    /**
     * Update partner.
     *
     * @param partner the partner
     * @return the string
     */
    @PostMapping("/update")
    public String updatePartner(@RequestBody final Partner partner) {
        return partnerService.update(partner);
    }

    /**
     * Adds the partner.
     *
     * @param partner the partner
     * @return the string
     */
    @PostMapping("/add")
    public String addPartner(@RequestBody final Partner partner) {
        return partnerService.save(partner);
    }

    /**
     * Partners.
     *
     * @return the map
     * @throws Exception the exception
     */
    @GetMapping("/all")
    public List<Map<String, String>> partners() throws Exception {
        return partnerService.partners();
    }
    
    @PostMapping("/partnersExcelDownload")
    public ResponseEntity<InputStreamResource> download(@RequestBody final Partner partner) throws IOException {
        return partnerService.download(partner);
    }
    
    @DeleteMapping("/delete")
    public String deletePartner(@RequestParam String partnerId) {
    	return partnerService.deletePartner(partnerId);
    }

}
