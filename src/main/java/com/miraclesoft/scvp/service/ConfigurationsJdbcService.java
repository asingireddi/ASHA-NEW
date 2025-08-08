package com.miraclesoft.scvp.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.model.Configurations;

@Service
public class ConfigurationsJdbcService {

	private final JdbcTemplate jdbcTemplate;
	private final Configurations configurations;

	@Autowired
	public ConfigurationsJdbcService(JdbcTemplate jdbcTemplate, Configurations configurations) {
		this.jdbcTemplate = jdbcTemplate;
		this.configurations = configurations;
	}

	@PostConstruct
	public void loadConfigurations() {
		String sql = "SELECT s3_bucket_access_key, s3_bucket_sceret_key, s3_bucket_name, s3_bucket_region FROM backend_configurations WHERE id = ?";
		Long configId = 1L;
		jdbcTemplate.query(sql, new Object[] { configId }, rs -> {
			if (rs.next()) {
				configurations.setS3BucketAccessKey(rs.getString("s3_bucket_access_key"));
				configurations.setS3BucketSecretKey(rs.getString("s3_bucket_sceret_key"));
				configurations.setS3BbucketName(rs.getString("s3_bucket_name"));
				configurations.setS3BbucketRegion(rs.getString("s3_bucket_region"));
			}
		});
	}
}
