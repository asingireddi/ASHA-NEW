package com.miraclesoft.scvp.service;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.SearchCriteria;
import com.miraclesoft.scvp.model.WarehouseOrder;
import com.miraclesoft.scvp.service.impl.WarehouseOrderServiceImpl;

/**
 * The Class WarehouseOrderService.
 *
 * @author Narendar Geesidi
 */
@Service
public class WarehouseOrderService {

    /** The warehouse order service impl. */
    @Autowired
    private WarehouseOrderServiceImpl warehouseOrderServiceImpl;

    /**
     * Search.
     *
     * @param searchCriteria the search criteria
     * @return the list
     */ 
    public CustomResponse search(final SearchCriteria searchCriteria) {
        return warehouseOrderServiceImpl.search(searchCriteria);
    }

    /**
     * Download.
     *
     * @param searchCriteria the search criteria
     * @return the response entity
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public ResponseEntity<InputStreamResource> download(final SearchCriteria searchCriteria) throws IOException {
        return warehouseOrderServiceImpl.download(searchCriteria);
    }

    /**
     * Detail info.
     *
     * @param depositorOrderNumber the depositor order number
     * @param fileId the file id
     * @param database the database
     * @return the warehouse order
     */
    public WarehouseOrder detailInfo(final String depositorOrderNumber, final String fileId, final String database) {
        return warehouseOrderServiceImpl.detailInfo(depositorOrderNumber, fileId, database);
    }

    /**
     * wareHouseDocumentType.
     *
     * @param database the database
     * @return the List
     */
    public List<String> wareHouseDocumentType(final String database) {
        return warehouseOrderServiceImpl.wareHouseDocumentType(database);
    }
}
