package com.miraclesoft.scvp.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.DocumentRepository;
import com.miraclesoft.scvp.model.Sfg;
import com.miraclesoft.scvp.model.Sfgpartner;
import com.miraclesoft.scvp.service.SfgDocumentRepositoryService;

/**
 * The Class SfgDocumentRepositoryController.
 *
 * @author shanmukhavarma kalidindi
 */
@RestController
@RequestMapping("/sfg")
public class SfgDocumentRepositoryController {

    /** The sfg document repository service. */
    @Autowired
    private SfgDocumentRepositoryService sfgDocumentRepositoryService;

    /**
     * searchFiles. 
     *
     * @param Sfg the Sfg
     * @return the List
     */
    @PostMapping("/search")
    public CustomResponse search(@RequestBody Sfg sfg) {
        return sfgDocumentRepositoryService.search(sfg);
    }

    @GetMapping("/producer")
    public List<String> producer(@RequestParam String liveOrArchive) {
        return sfgDocumentRepositoryService.producer(liveOrArchive);
    }

    @GetMapping("/consumer")
    public List<String> consumer(@RequestParam String liveOrArchive) {
        return sfgDocumentRepositoryService.consumer(liveOrArchive);
    }

    @GetMapping("/sfgReprocess")
    public Map<String, Object> sfgReprocess(@RequestParam Long id) {
        return sfgDocumentRepositoryService.sfgReprocess(id);
    }

    /**
     * Adds the partner.
     *
     * @param partner the partner
     * @return the string
     */
    @PostMapping("/addSfgPartner")
    public String addPartner(@RequestBody final Sfgpartner sfgpartner) {
        return sfgDocumentRepositoryService.save(sfgpartner);
    }

    @PostMapping("/partnerSearch")
    public Map<String, Object> partnerSearch(@RequestBody final Sfgpartner partner) {
        return sfgDocumentRepositoryService.findAll(partner);
    }

    @PostMapping("/update")
    public String updatePartner(@RequestBody final Sfgpartner partner) {
        return sfgDocumentRepositoryService.update(partner);
    }

    /**
     * Download.
     *
     * @param searchCriteria the search criteria
     * @return the response entity
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @GetMapping(path = "/download")
    public ResponseEntity<ByteArrayResource> uploadFile(@RequestParam(value = "file") final String file) throws IOException {
        final ResponseEntity<byte[]> data = sfgDocumentRepositoryService.getSfgFileFromAmazonS3(file);
        final ByteArrayResource resource = new ByteArrayResource(data.getBody());
        return ResponseEntity.status(data.getStatusCode()).contentLength(data.getBody().length).header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + file + "\"").body(resource);
    }

    @PostMapping("/download")
    public ResponseEntity<InputStreamResource> download(@RequestBody final Sfg sfg) throws IOException {
        return sfgDocumentRepositoryService.download(sfg);
    }

    /**
     * sending files that requested by user.
     *
     * @param documentRepository the DocumentRepository
     * @return the Map
     */
    @PostMapping("/sendMail")
    public Map<String, Object> sendMail(@RequestBody final DocumentRepository documentRepository) {
        return sfgDocumentRepositoryService.sendMail(documentRepository);
    }
    
    /**
     * partnerVisibility by partnerVisibility.
     * 
     * @return the List
     */
    @GetMapping("/partnerVisibility")
    public List<String> partnerVisibility(){
        return sfgDocumentRepositoryService.partnerVisibility();
    }
    
    @PostMapping("/sfgPartnersExcelDownload")
    public ResponseEntity<InputStreamResource> download(@RequestBody final Sfgpartner partner) throws IOException {
        return sfgDocumentRepositoryService.download(partner);
    }
    
    @DeleteMapping("/partnerDelete")
    public String deletePartner(@RequestParam String partnerName) {
    	return sfgDocumentRepositoryService.deletePartner(partnerName);
    }
    

}
