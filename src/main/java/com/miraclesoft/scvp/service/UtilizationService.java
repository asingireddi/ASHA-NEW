package com.miraclesoft.scvp.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.model.SearchCriteria;
import com.miraclesoft.scvp.service.impl.UtilizationServiceImpl;

/**
 * The Interface UtilizationService.
 *
 * @author Narendar Geesidi
 */
@Service
public class UtilizationService {

    /** The utilization serviceimpl. */
    @Autowired
    private UtilizationServiceImpl utilizationServiceimpl;

    /** 
     * Mscvp roles.
     *
     * @return the string
     */
    public String mscvpRoles() {
        return utilizationServiceimpl.mscvpRoles();
    }

    /**
     * Primary flows.
     *
     * @return the string
     */
    public String primaryFlows() {
        return utilizationServiceimpl.primaryFlows();
    }

    /**
     * Document types.
     *
     * @param database the database
     * @return the list
     */
    public List<String> documentTypes(final String database) {
        return utilizationServiceimpl.documentTypes(database);
    }

    /**
     * Correlations.
     *
     * @param transaction the transaction
     * @return the list
     */
    public List<String> correlations(final String transaction) {
        return utilizationServiceimpl.correlations(transaction);
    }

    /**
     * Parent warehouses.
     *
     * @param database the database
     * @return the list
     */
    public List<String> parentWarehouses(final String database) {
        return utilizationServiceimpl.parentWarehouses(database);
    }

    /**
     * List of warehouses under a parent warehouse.
     *
     * @param database the database
     * @param parentWarehouse the parent warehouse
     * @return the list
     */
    public List<String> warehousesFor(final SearchCriteria searchCriteria) {
        return utilizationServiceimpl.warehousesFor(searchCriteria);
    }
    
    public List<String> acknowledgementStatus() {
		return utilizationServiceimpl.acknowledgementStatus();
	}

    /**
     * Gets the file.
     *
     * @param file the file
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public ResponseEntity<byte[]> getFileFromAmazonS3(final String file) throws IOException {
        return utilizationServiceimpl.getFileFromAmazonS3(file);
    }

	public String tradingPartners(String database) {
		return utilizationServiceimpl.tradingPartners(database);
	}

	 public ResponseEntity<byte[]> getFile(final String file) throws IOException {
        return utilizationServiceimpl.getFile(file);
    }
	 public List<String> getStatus() {
			return utilizationServiceimpl.getStatus();
		}
	 public List<String> getAckStatus() {
			return utilizationServiceimpl.getAckStatus();
		}

	public List<String> getTradingPartnerName() {
		return utilizationServiceimpl.getTradingPartnerName();
	}


	public List<Map<String, Object>> getGroupedPartners() {
		return utilizationServiceimpl.getGroupedPartners();
	}
	 public List<String> getStats() {
			return utilizationServiceimpl.getStats();
		}

	 public List<String> getdeliveredTo() {
			return utilizationServiceimpl.getdeliveredTo();
}
	}

