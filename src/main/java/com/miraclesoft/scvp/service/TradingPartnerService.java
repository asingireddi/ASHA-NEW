package com.miraclesoft.scvp.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.model.Delivery;
import com.miraclesoft.scvp.model.TradingPartner;
import com.miraclesoft.scvp.service.impl.TradingPartnerServiceImpl;

@Service
public class TradingPartnerService {

	@Autowired
	private TradingPartnerServiceImpl tradingPartnerServiceImpl;

	public List<String> getDirections() {
		return tradingPartnerServiceImpl.getDirections();
	}

	public List<String> getTrasactionCode() {
		return tradingPartnerServiceImpl.getTrasactionCode();
	}

	public List<String> getPartnerName() {
		return tradingPartnerServiceImpl.getPartnerName();
	}

	public List<String> getStream() {
		return tradingPartnerServiceImpl.getStream();
	}

	public List<String> getAllColumnNames() {
		return tradingPartnerServiceImpl.getAllColumnNames();
	}

	public Map<String, Object> fetchData(TradingPartner tradingPartner) {
		return tradingPartnerServiceImpl.search(tradingPartner);
	}

	public List<Map<String, Object>> executeQuery1(String sqlQuery) {
		return tradingPartnerServiceImpl.executeQuery1(sqlQuery);
	}

	public String updatePartnerDetails(TradingPartner tradingPartner) {
		return tradingPartnerServiceImpl.updatePartnerDetails(tradingPartner);
	}

	public ResponseEntity<InputStreamResource> download(final TradingPartner tradingPartner) throws IOException {
 
		return tradingPartnerServiceImpl.download(tradingPartner);
	}

	public List<String> getTcSapId() {
		return tradingPartnerServiceImpl.getTcSapId();
	}



	public List<String> getdelivery() {
		return tradingPartnerServiceImpl.getDelivery();
	}

	

	public Object getFilteredData(Delivery delivery) {
		return tradingPartnerServiceImpl.getFilteredData(delivery);
	}

	public List<Map<String, Object>> getIdAndName() {
	    return tradingPartnerServiceImpl.getIdAndName();
	}

	
}

	
	



