package com.miraclesoft.scvp.controller;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.SearchCriteria;
import com.miraclesoft.scvp.model.WarehouseOrder;
import com.miraclesoft.scvp.service.WarehouseOrderService;

/**
 * The Class WarehouseOrderController.
 *
 * @author Narendar Geesidi
 */
@RestController
@RequestMapping("/warehouseOrder")
public class WarehouseOrderController {

    /** The warehouse order service. */
    @Autowired
    private WarehouseOrderService warehouseOrderService;

    /**
     * Search.
     *
     * @param searchCriteria the search criteria
     * @return the list
     */
    @PostMapping("/search") 
    public CustomResponse search(@RequestBody final SearchCriteria searchCriteria) {
        return warehouseOrderService.search(searchCriteria);
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
        return warehouseOrderService.download(searchCriteria);
    }

    /**
     * Detail info.
     *
     * @param depositorOrderNumber the depositor order number
     * @param fileId the file id
     * @param database the database
     * @return the warehouse order
     */
    @GetMapping("/detailInfo/{depositorOrderNumber}/{fileId}/{database}")
    public WarehouseOrder detailInfo(@PathVariable final String depositorOrderNumber, @PathVariable final String fileId,
            @PathVariable final String database) {
        return warehouseOrderService.detailInfo(depositorOrderNumber, fileId, database);
    }

    /**
     * wareHouseDocumentType.
     *
     * @param database the database
     * @return the List
     */
    @GetMapping("/documentType/{database}")
    public List<String> wareHouseDocumentType(@PathVariable final String database) {
        return warehouseOrderService.wareHouseDocumentType(database);
    }

}
