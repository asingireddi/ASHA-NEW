package com.miraclesoft.scvp.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.DocumentRepository;
import com.miraclesoft.scvp.model.SearchCriteria;
import com.miraclesoft.scvp.service.impl.DocumentRepositoryServiceImpl;

/**
 * The Class DocumentRepositoryService.
 *
 * @author Narendar Geesidi
 */
@Service
public class DocumentRepositoryService {

	/** The document repository service impl. */
	@Autowired
	private DocumentRepositoryServiceImpl documentRepositoryServiceImpl;

	/**
	 * Search.
	 *
	 * @param searchCriteria the search criteria
	 * @return the list
	 */
	public CustomResponse search(final SearchCriteria searchCriteria) {
		return documentRepositoryServiceImpl.search(searchCriteria);
	}

	/**
	 * Download.
	 *
	 * @param searchCriteria the search criteria
	 * @return the response entity
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ResponseEntity<InputStreamResource> download(final SearchCriteria searchCriteria) throws IOException {
		return documentRepositoryServiceImpl.download(searchCriteria);
	}

	/**
	 * Detail info.
	 *
	 * @param id       the id
	 * @param database the database
	 * @return the document repository
	 */
	public DocumentRepository detailInfo(final Long id, final String database) {
		return documentRepositoryServiceImpl.detailInfo(id, database);
	}

	/**
	 * Search by status.
	 *
	 * @param status the status
	 * @return the list
	 */
	public CustomResponse searchByStatus(final SearchCriteria searchCriteria) {
		return documentRepositoryServiceImpl.searchByStatus(searchCriteria);
	}

	/**
	 * reprocessRequest by reprocessRequest.
	 *
	 * @param searchCriteria the searchCriteria
	 * @return the list
	 */
	public Map<String, Object> reprocessRequest(final SearchCriteria searchCriteria) {
		return documentRepositoryServiceImpl.reprocessRequest(searchCriteria);
	}

	/**
	 * sendAttachment by sendAttachment.
	 *
	 * @param documentRepository the DocumentRepository
	 * @return the Map
	 */
	public Map<String, Object> transactionsDetailAttachment(final DocumentRepository documentRepository) {
		return documentRepositoryServiceImpl.transactionsDetailAttachment(documentRepository);
	}

	public ResponseEntity<ByteArrayResource> multipleDownload() {
		// TODO Auto-generated method stub
		return documentRepositoryServiceImpl.multipleDownload();
	}

	public List<DocumentRepository> searchByStats(SearchCriteria searchCriteria) {
		// TODO Auto-generated method stub
		return documentRepositoryServiceImpl.searchByStats(searchCriteria);
	}

	public Map<String, Object> updateReprocessStatus(int id, String reprocessStatus) {
		return documentRepositoryServiceImpl.updateReprocessStatus(id, reprocessStatus);
	}

	public List<String> getSapIdlist() {
		return documentRepositoryServiceImpl.getSapIdlist();
	}
}
