package com.miraclesoft.scvp.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.DocumentRepository;
import com.miraclesoft.scvp.model.SearchCriteria;
import com.miraclesoft.scvp.service.DocumentRepositoryService;

/**
 * The Class DocumentRepositoryController.
 *
 * @author Narendar Geesidi
 */
@RestController
@RequestMapping("/documentRepository")
public class DocumentRepositoryController {

	/** The document repository service. */
	@Autowired
	private DocumentRepositoryService documentRepositoryService;

	/**
	 * Search.
	 *
	 * @param searchCriteria the search criteria
	 * @return the list
	 */
	@PostMapping("/search")
	public CustomResponse search(@RequestBody final SearchCriteria searchCriteria) {
		return documentRepositoryService.search(searchCriteria);
	}

	/**
	 * Download.
	 *
	 * @param searchCriteria the search criteria
	 * @return the response entity
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@PostMapping("/download")
	public ResponseEntity<InputStreamResource> download(@RequestBody final SearchCriteria searchCriteria)
			throws IOException {
		return documentRepositoryService.download(searchCriteria);
	}

	/**
	 * Detail info.
	 *
	 * @param id       the id
	 * @param database the database
	 * @return the document repository
	 */
	@GetMapping("/detailInfo/{id}/{database}")
	public DocumentRepository detailInfo(@PathVariable final Long id, @PathVariable final String database) {
		return documentRepositoryService.detailInfo(id, database);
	}

	/**
	 * Search by status.
	 *
	 * @param status the status
	 * @return the list
	 */
	@PostMapping("/byStatus")
	public CustomResponse searchByStatus(@RequestBody final SearchCriteria searchCriteria) {
		return documentRepositoryService.searchByStatus(searchCriteria);
	}

	/**
	 * Reprocess requests.
	 *
	 * @param searchCriteria the searchCriteria
	 * @return the Map
	 */
	@PostMapping("/ReprocessRequest")
	public Map<String, Object> reprocessRequest(@RequestBody final SearchCriteria searchCriteria) {
		return documentRepositoryService.reprocessRequest(searchCriteria);
	}

	/**
	 * sending files that requested by user.
	 *
	 * @param documentRepository the DocumentRepository
	 * @return the Map
	 */
	@PostMapping("/TransactionsDetailAttachment")
	public Map<String, Object> transactionsDetailAttachment(@RequestBody final DocumentRepository documentRepository) {
		return documentRepositoryService.transactionsDetailAttachment(documentRepository);
	}

	@PostMapping("/multipleDownload")
	public ResponseEntity<ByteArrayResource> multipleDownload() {
		return documentRepositoryService.multipleDownload();
	}

	@PostMapping("/searchByStats")
	public List<DocumentRepository> searchByStats(@RequestBody final SearchCriteria searchCriteria) {
		return documentRepositoryService.searchByStats(searchCriteria);
	}

	@PutMapping("/updateReprocessStatus")
	public Map<String, Object> updateReprocessStatus(@RequestParam int id, @RequestParam String reprocessStatus) {

		return documentRepositoryService.updateReprocessStatus(id, reprocessStatus);
	}

	@GetMapping("/sap-ids")
	public List<String> getSapIdList() {
		return documentRepositoryService.getSapIdlist();
	}
}
