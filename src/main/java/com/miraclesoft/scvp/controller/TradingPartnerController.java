package com.miraclesoft.scvp.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.scvp.model.Delivery;
import com.miraclesoft.scvp.model.TradingPartner;
import com.miraclesoft.scvp.service.TradingPartnerService;

@RestController
@RequestMapping("/tradingPartner")
public class TradingPartnerController {

	
	@Autowired
	
	
	TradingPartnerService tradingPartnerService;
	
	
  
	 @GetMapping("/directions")
	    public List<String> getDirections() {
	        return tradingPartnerService.getDirections();
	    }
	  

	   
	   @GetMapping("/trasaction-code")
	    public List<String> getTrasactionCode() {
	        return tradingPartnerService.getTrasactionCode();
	    }
	  
	   @GetMapping("/partners")
	    public List<String> getPartnerName() {
	        return tradingPartnerService.getPartnerName();
	    }
	   
	   
	   @GetMapping("/stream")
	    public List<String> getStream() {
	        return tradingPartnerService.getStream();
	    }
	
	   @GetMapping("/available")
	    public List<String> getAllData() {
	        return tradingPartnerService.getAllColumnNames();
	    }
	   
	   @PostMapping("/fetch")
	    public Map<String, Object> fetchData(@RequestBody TradingPartner tradingPartner) {
	        return tradingPartnerService.fetchData(tradingPartner);
	    }
	        
	
	   @PostMapping("/execute-query")
	    public ResponseEntity<?> executeQuery1(@RequestBody String sqlQuery) {
	        try {
	    
	            List<Map<String, Object>> result = tradingPartnerService.executeQuery1(sqlQuery);
	            return ResponseEntity.ok(result);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.badRequest().body(e.getMessage());
	        } catch (BadSqlGrammarException e) {
	            return ResponseEntity.badRequest().body("Your SQL query has an error: " + e.getMessage());
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
	        }
	    }
	
	   @PostMapping("/update")
	   public String updatePartnerDetails(@RequestBody TradingPartner tradingPartner) {
		   
		   return tradingPartnerService.updatePartnerDetails(tradingPartner);
	   }
	   
	   
	   @PostMapping("/download") 
	    public ResponseEntity<InputStreamResource> download(@RequestBody TradingPartner tradingPartner)
	            throws IOException {
	        return tradingPartnerService.download(tradingPartner);
	    }
	   @GetMapping("/tcSapId")
	    public List<String> getTcSapId() {
	        return tradingPartnerService.getTcSapId();
	    }
	   @GetMapping("/delivery")
	    public List<String> getDelivery() {
	        return tradingPartnerService.getdelivery();
	    }
	 
	   @PostMapping("/filter")
	    public ResponseEntity<Object> getFilteredData(@RequestBody Delivery delivery) {
	        return ResponseEntity.ok(tradingPartnerService.getFilteredData(delivery));
	    }
	   @GetMapping("/lookUp")
	   public List<Map<String, Object>> getIdAndName() {
	       return tradingPartnerService.getIdAndName();
	   }
}