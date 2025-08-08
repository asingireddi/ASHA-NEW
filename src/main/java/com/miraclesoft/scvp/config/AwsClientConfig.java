package com.miraclesoft.scvp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.miraclesoft.scvp.model.Configurations;

/**
 * The Class AwsClientConfig.
 */
@Configuration
public class AwsClientConfig {

	@Autowired
	private Configurations configurations;

	/**
	 * Amazon S3.
	 *
	 * @return the amazon S3
	 */
	@Bean
	public AmazonS3 amazonS3() {
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(configurations.getS3BucketAccessKey(),
				configurations.getS3BucketSecretKey()); // NOPMD
		return AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(configurations.getS3BbucketRegion()))
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
	}

}
