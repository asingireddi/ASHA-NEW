package com.miraclesoft.scvp.controller;

import com.miraclesoft.scvp.util.RsaKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class KeyController {
 
	@Autowired
	private RsaKeyUtil rsaKeyUtil;
 
	@GetMapping("/key")
	public ResponseEntity<Map<String, String>> getPublicKey() {
		String key = rsaKeyUtil.getPublicKeyBase64();
		Map<String, String> response = new HashMap<>();
		response.put("key", key);
		return ResponseEntity.ok(response);
	}
 
}