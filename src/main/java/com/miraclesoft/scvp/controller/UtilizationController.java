package com.miraclesoft.scvp.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.scvp.model.SearchCriteria;
import com.miraclesoft.scvp.service.UtilizationService;
import com.miraclesoft.scvp.util.PasswordUtil;

/**
 * The Class UtilizationController.
 *
 * @author Narendar Geesidi
 */
@RestController
@RequestMapping("/utilities")
public class UtilizationController {

    /** The utilization service. */
    @Autowired
    private UtilizationService utilizationService;

    
    PasswordUtil pass;

    /**
     * Upload file.
     *
     * @param file the file
     * @return the response entity
     * @throws IOException Signals that an I/O exception has occurred.
     */
//    @GetMapping(path = "/download")
//    public ResponseEntity<ByteArrayResource> uploadFile(@RequestParam(value = "file") final String file)
//            throws IOException {
//        final ResponseEntity<byte[]> data = utilizationService.getFileFromAmazonS3(file);
//        final ByteArrayResource resource = new ByteArrayResource(data.getBody());
//        return ResponseEntity.status(data.getStatusCode())
//                             .contentLength(data.getBody().length)
//                             .header("Content-type", "application/octet-stream")
//                             .header("Content-disposition", "attachment; filename=\"" + file + "\"")
//                             .body(resource);
//    }
//    @GetMapping(path = "/download")
//   	public ResponseEntity<?> uploadFile(@RequestParam(value = "file") final String file)
//   			throws IOException {
//   		final ResponseEntity<byte[]> data = utilizationService.getFile(file);
//   		final ByteArrayResource resource = new ByteArrayResource(data.getBody());
//   		return ResponseEntity.status(data.getStatusCode()).contentLength(data.getBody().length).header("Content-type", "application/octet-stream")
//   				.header("Content-disposition", "attachment; filename=\"" + file + "\"").body(resource);
//   	}
    
    @GetMapping(path = "/download")
   	public ResponseEntity<?> uploadFile(@RequestParam(value = "file") final String file)
   			throws IOException {
   		final ResponseEntity<byte[]> data = utilizationService.getFile(file);
   		final ByteArrayResource resource = new ByteArrayResource(data.getBody());
   		return ResponseEntity.status(data.getStatusCode()).contentLength(data.getBody().length).header("Content-type", "application/octet-stream")
   				.header("Content-disposition", "attachment; filename=\"" + file + "\"").body(resource);
   	}
    /**
     * Mscvp roles.
     *
     * @return the string
     */
    @GetMapping("/mscvpRoles")
    public String mscvpRoles() {
        return utilizationService.mscvpRoles();
    }

    /**
     * Primary flows.
     *
     * @return the string
     */
    @GetMapping("/primaryFlows")
    public String primaryFlows() {
        return utilizationService.primaryFlows();
    }

    /**
     * Document types.
     *
     * @param flowFlag the flow flag
     * @param database the database
     * @return the list
     */
    @GetMapping("/documentTypes/{flowFlag}/{database}")
    public List<String> documentTypes(@PathVariable final String flowFlag, @PathVariable final String database) {
        return utilizationService.documentTypes(database);
    }
    
    
    @GetMapping("/tradingPartners/{flowFlag}/{database}")
    public String tradingPartners(@PathVariable final String flowFlag, @PathVariable final String database) {
        return utilizationService.tradingPartners(database);
    }

    /**
     * Correlations.
     *
     * @param transaction the transaction
     * @return the list
     */
    @GetMapping("/correlations/{transaction}")
    public List<String> correlations(@PathVariable final String transaction) {
        return utilizationService.correlations(transaction);
    }

    /**
     * Parent warehouses.
     *
     * @param flowFlag the flow flag
     * @param database the database
     * @return the list
     */
    @GetMapping("/parentWarehouses/{flowFlag}/{database}")
    public List<String> parentWarehouses(@PathVariable final String flowFlag, @PathVariable final String database) {
        return utilizationService.parentWarehouses(database);
    }

    /**
     * List of warehouses under a parent warehouse.
     *
     * @param flowFlag the flow flag
     * @param database the database
     * @param parentWarehouse the parent warehouse
     * @return the list
     */
    @PostMapping("/listOfWarehouses")	
    public List<String> warehousesFor(@RequestBody final SearchCriteria searchCriteria) {
        return utilizationService.warehousesFor(searchCriteria);
    }
    
    @GetMapping("/ackStatus")
    public List<String> acknowledgementStatus(){
		return utilizationService.acknowledgementStatus();
    }
    
    @GetMapping("/password")
    public String password(@RequestParam String password) {
        return pass.decryptPassword(password);
    }
    @GetMapping(path = "/downloadMultipleFiles")
    public ResponseEntity<ByteArrayResource> downloadMultipleFiles(@RequestParam(value = "file") final String files,HttpServletResponse response)
            throws IOException {
         ByteArrayResource resource = null;
        String[] file = files.split(",");
        try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
       for(String fileName : file) {
        final ResponseEntity<byte[]> data = utilizationService.getFileFromAmazonS3(fileName);
        resource = new ByteArrayResource(data.getBody());
                ZipEntry e = new ZipEntry(fileName.replace("/", "-"));
                // Configure the zip entry, the properties of the file
                e.setSize(resource.contentLength());
                e.setTime(System.currentTimeMillis());
                // etc.
                zippedOut.putNextEntry(e);
                // And the content of the resource:
                StreamUtils.copy(resource.getInputStream(), zippedOut);
                zippedOut.closeEntry();
        }
       zippedOut.finish();
        }
        catch (Exception e) {
            e.printStackTrace();
            // Exception handling goes here
        }
        
          // response.setContentType("application/zip");
          
       

        
        return ResponseEntity.ok()
                            
                             .header("Content-type", "application/zip")
                             .header("Content-disposition", "attachment; filename=\"" + files + "\"")
                             .body(resource);
    }
    
    @GetMapping("/status")
    public List<String> getStatus() {
        return utilizationService.getStatus();
    }
    @GetMapping("/ackstatus")
    public List<String> getAckStatus() {
        return utilizationService.getAckStatus();
    }
    
    @GetMapping("/tradingPartnerName")
    public List<String> getTradingPartnerName() {
        return utilizationService.getTradingPartnerName();
    }
    @GetMapping("/PartnersIdAndName")
    public List<Map<String, Object>> getGroupedPartners() {
        return utilizationService.getGroupedPartners();
    }
    @GetMapping("/stats")
    public List<String> getstats() {
        return utilizationService.getStats();
    }
    @GetMapping("/deliveredTo")
    public List<String> getdeliveredTo() {
        return utilizationService.getdeliveredTo();
    }
}
