package com.miraclesoft.scvp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.service.impl.TimeZoneServiceImpl;

/**
 * The Class TimeZoneService.
 * 
 * @author rpidugu
 *
 */
@Service
public class TimeZoneService {
	/** The time zone service impl. */
	@Autowired
	private TimeZoneServiceImpl timeZoneServiceImpl;

	/**
	 * Time zones list
	 * 
	 * @return the string
	 */
	public String timeZone() {
		return timeZoneServiceImpl.timeZone();
	}

}
