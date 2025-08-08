package com.miraclesoft.scvp.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.DocumentRepository;
import com.miraclesoft.scvp.model.Sfg;
import com.miraclesoft.scvp.model.Sfgpartner;
import com.miraclesoft.scvp.service.impl.SfgDocumentRepositoryServiceImpl;

/**
 * The Class PartnerService.
 *
 * @author shanmukhavarma kalidindi
 */
@Service
public class SfgDocumentRepositoryService {

    @Autowired
    private SfgDocumentRepositoryServiceImpl sfgDocumentRepositoryServiceImpl;

    /* *//**
          * search. 
          *
          * @param startDate    the startDate
          * @param endDate      the endDate
          * @param partnersName the partnersName
          * @return the list
          *//*
             * public List<String> search1(final String startDate, final String endDate,
             * final List<String> partnersName) { return
             * sfgDocumentRepositoryServiceImpl.search1(startDate, endDate, partnersName); }
             */

    /**
     * search.
     *
     * @param sfg the sfg
     * @return the list
     */
    public CustomResponse search(final Sfg sfg) {
        return sfgDocumentRepositoryServiceImpl.search(sfg);
    }

    public List<String> producer(String liveOrArchive) {
        return sfgDocumentRepositoryServiceImpl.producer(liveOrArchive);
    }

    public List<String> consumer(String liveOrArchive) {
        return sfgDocumentRepositoryServiceImpl.consumer(liveOrArchive);
    }

    public Map<String, Object> sfgReprocess(Long id) {
        return sfgDocumentRepositoryServiceImpl.sfgReprocess(id);
    }

    public String save(Sfgpartner sfgpartner) {
        return sfgDocumentRepositoryServiceImpl.save(sfgpartner);
    }

    public Map<String, Object> findAll(Sfgpartner sfgPartner) {
        return sfgDocumentRepositoryServiceImpl.findAll(sfgPartner);
    }

    public String update(Sfgpartner sfgPartner) {
        return sfgDocumentRepositoryServiceImpl.update(sfgPartner);
    }

    /**
     * Pre post file.
     *
     * @param filePath the file path
     * @return the response entity
     */
    public ResponseEntity<byte[]> getSfgFileFromAmazonS3(final String file) throws IOException {
        return sfgDocumentRepositoryServiceImpl.getSfgFileFromAmazonS3(file);
    }

    public ResponseEntity<InputStreamResource> download(final Sfg sfg) throws IOException {
        return sfgDocumentRepositoryServiceImpl.download(sfg);
    }

    /**
     * sendAttachment by sendAttachment.
     *
     * @param documentRepository the DocumentRepository
     * @return the Map
     */
    public Map<String, Object> sendMail(final DocumentRepository documentRepository) {
        return sfgDocumentRepositoryServiceImpl.sendMail(documentRepository);
    }

    /**
     * partnerVisibility by partnerVisibility.
     *
     * @return the List
     */
    public List<String> partnerVisibility() {
        return sfgDocumentRepositoryServiceImpl.partnerVisibility();
    }

	public ResponseEntity<InputStreamResource> download(Sfgpartner partner) throws IOException {
		return sfgDocumentRepositoryServiceImpl.download(partner);
	}

	public String deletePartner(String partnerName) {
		return sfgDocumentRepositoryServiceImpl.deletePartner(partnerName);
	}

   
}
