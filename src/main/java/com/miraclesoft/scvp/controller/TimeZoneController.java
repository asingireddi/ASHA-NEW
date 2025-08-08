package com.miraclesoft.scvp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.scvp.service.TimeZoneService;

/**
 * The Class TimeZoneController.
 * 
 * @author rpidugu
 *
 */
@RestController
@RequestMapping("/timeZones")
public class TimeZoneController {
	/** The time zone service. */
	@Autowired
	private TimeZoneService timeZoneService;

	/**
	 * Time zones list
	 * 
	 * @return the string
	 */
	@GetMapping("/list")
	public String timeZone() {
		return timeZoneService.timeZone();

	}

}
