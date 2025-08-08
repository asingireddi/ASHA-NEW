package com.miraclesoft.scvp.util;

import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.miraclesoft.scvp.model.Configurations;
import com.miraclesoft.scvp.service.impl.LoginServiceImpl;

/**
 * The Class AwsS3Util.
 */
@Service
public class AwsS3Util {

	/** The amazon S 3. */
	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private Configurations configurations;

	@Autowired
	private LoginServiceImpl loginServiceImpl;

	/** The logger. */
	private static Logger logger = LogManager.getLogger(AwsS3Util.class.getName());

	/**
	 * Gets the file.
	 *
	 * @param key the key
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */

	public ResponseEntity<byte[]> getFile(String file) throws IOException {

		Configurations configuration = loginServiceImpl.getConfigurations();
		String bucketName = configuration.getS3BbucketName();
		logger.info("file ---> " + file);
		if (file.contains(bucketName)) {
			file = file.replace("/" + bucketName + "/", "");
		}
		try {
//    		final List<Map<String, Object>> rows = jdbcTemplate.queryForList(
//    				"SELECT * from  files where file_id='"+fileId+"'");
//    		 for (final Map<String, Object> row : rows) {
//    		      fileName= (String) row.get("filename");
//    		 }

			boolean isObjectExist = amazonS3.doesObjectExist(bucketName, file);
			if (isObjectExist) {
				final S3Object obj = amazonS3.getObject(bucketName, file);
				S3ObjectInputStream stream = obj.getObjectContent();
				byte[] content = IOUtils.toByteArray(stream);
				obj.close();
				return new ResponseEntity<byte[]>(content, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("The specified bucket or key does not exist".getBytes());
			}

		} catch (AmazonS3Exception amazonS3Exception) {
			if (amazonS3Exception.getStatusCode() == 404) {
				logger.log(Level.ERROR, "getFile :: " + amazonS3Exception.getMessage());
				String message = "The specified bucket or key does not exist";
				byte[] responseMessage = message.getBytes();
				HttpHeaders headers = new HttpHeaders();
				headers.set("Status", "404");
				return new ResponseEntity<byte[]>(responseMessage, headers, HttpStatus.NOT_FOUND);
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
			logger.log(Level.ERROR, " getFile :: " + ioException.getMessage());
		}
		return null;
	}

	public ResponseEntity<byte[]> getFileFromAmazonS3(final String file) throws IOException {
		Configurations configuration = loginServiceImpl.getConfigurations();
		String bucketName = configuration.getS3BbucketName();
//String accessKey = configuration.getS3BucketAccessKey() ;
		// String secretKey = configuration.getS3BucketSecretKey();

		return getObjectFromAmazonS3(bucketName, file);
		// return getObjectFromAmazonS3("rhlk-dev-rscvp-s3", key);
	}

	public ResponseEntity<byte[]> getSfgFileFromAmazonS3(final String key) throws IOException {
		return getObjectFromAmazonS3(configurations.getS3BbucketName(), key);
	}

	public boolean checkSfgObject(final String keyName) {
		return amazonS3.doesObjectExist(configurations.getS3BbucketName(), keyName);
	}

	public ResponseEntity<byte[]> getObjectFromAmazonS3(String nameOfBucket, String key) {
		try {
			boolean isObjectExist = amazonS3.doesObjectExist(nameOfBucket, key);
			if (isObjectExist) {
				final S3Object obj = amazonS3.getObject(nameOfBucket, key);
				S3ObjectInputStream stream = obj.getObjectContent();
				byte[] content = IOUtils.toByteArray(stream);
				obj.close();
				return new ResponseEntity<byte[]>(content, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("The specified bucket or key does not exist".getBytes());
			}
		} catch (IOException ioException) {
			logger.log(Level.ERROR, " getFileFromAmazonS3 :: " + ioException.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred".getBytes());
		} catch (Exception exception) {
			logger.log(Level.ERROR, " getFileFromAmazonS3 :: " + exception.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred".getBytes());
		}
	}

	/**
	 * Check object.
	 *
	 * @param keyName the key name
	 * @return true, if successful
	 */
	public boolean checkObject(final String keyName) {
		return amazonS3.doesObjectExist(configurations.getS3BbucketName(), keyName);
	}

	/**
	 * Check object.
	 *
	 * @param keyName the key name
	 * @return true, if successful
	 */

	/**
	 * Gets the file.
	 *
	 * @param keyName the keyName
	 * @return the file
	 */
	@Async
	public byte[] getDownloadedFile(final String keyName) {
		byte[] content = null;
		Configurations configuration = loginServiceImpl.getConfigurations();
		String bucketName = configuration.getS3BbucketName();
		final S3Object s3Object = amazonS3.getObject(bucketName, keyName);
		System.out.println("configurations.getS3BbucketName()" + configurations.getS3BbucketName());
		final S3ObjectInputStream stream = s3Object.getObjectContent();
		try {
			content = IOUtils.toByteArray(stream);
		} catch (final IOException exception) {
			logger.log(Level.ERROR, " downloadFile :: " + exception.getMessage());
		}

		return content;
	}

	/**
	 * Gets the file.
	 *
	 * @param keyName the keyName
	 * @return the file
	 */
	@Async
	public byte[] getSfgDownloadedFile(final String keyName) {
		byte[] content = null;
		final S3Object s3Object = amazonS3.getObject(configurations.getS3BbucketName(), keyName);
		final S3ObjectInputStream stream = s3Object.getObjectContent();
		try {
			content = IOUtils.toByteArray(stream);
		} catch (final IOException exception) {
			logger.log(Level.ERROR, " downloadFile :: " + exception.getMessage());
		}

		return content;
	}

}
