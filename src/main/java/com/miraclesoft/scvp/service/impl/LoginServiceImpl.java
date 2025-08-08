package com.miraclesoft.scvp.service.impl;

import static java.util.Objects.nonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.miraclesoft.scvp.mail.MailManager;
import com.miraclesoft.scvp.model.Configurations;
import com.miraclesoft.scvp.model.UserInfoBean;
import com.miraclesoft.scvp.util.DataSourceDataProvider;
import com.miraclesoft.scvp.util.PasswordUtil;
import com.miraclesoft.scvp.util.RsaKeyUtil;

/**
 * The Class LoginServiceImpl.
 *
 * @author Narendar Geesidi
 */
@Component
public class LoginServiceImpl {

    /** The jdbc template. */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** The data source data provider. */
    @Autowired
    private DataSourceDataProvider dataSourceDataProvider;
    
    @Autowired
	private MailManager mailManager;
    
    /** The user default role id. */
    @Value("${user.default.roleid}")
    private String userDefaultRoleId;

    /** The Constant logger. */
    private static final Logger logger = LogManager.getLogger(LoginServiceImpl.class);

    /** The Constant SES_USER_ID. */
    public static final String SES_USER_ID = "userId";

    /** The Constant SES_LOGIN_ID. */
    public static final String SES_LOGIN_ID = "loginId";

    /** The Constant ACTIVE_FLAG. */
    public static final String ACTIVE_FLAG = "isActive";

    /** The Constant SES_USER_NAME. */
    public static final String SES_USER_NAME = "userName";

    /** The Constant SES_EMAIL_ID. */
    public static final String SES_EMAIL_ID = "emailid";

    /** The Constant SES_USER_DEFAULT_FLOWID. */
    public static final String SES_USER_DEFAULT_FLOWID = "userDefaultFlowID";

    /** The Constant SES_ROLE_ID. */
    public static final String SES_ROLE_ID = "roleId";

    /** The Constant SES_USER_FLOW_MAP. */
    public static final String SES_USER_FLOW_MAP = "userFlowMap";

    /** The Constant SES_STATES_MAP. */
    public static final String SES_STATES_MAP = "statesMap";

    /** The Constant MSCVPROLE. */
    public static final String MSCVPROLE = "mscvpRole";

    /** The Constant DEFAULT_FLOW_NAME. */
    public static final String DEFAULT_FLOW_NAME = "defaultFlowName";

    /** The Constant REQ_ERROR_INFO. */
    public static final String REQ_ERROR_INFO = "errorMessage";

    /** The Constant PARTNER_VISIBILITY. */
    public static final String PARTNER_VISIBILITY = "partnerVisibility";

    /** The Constant is_all_partners. */
    public static final String IS_ALL_PARTNERS = "isAllPartners";
    
    /** The Constant is_all_sfg_partners. */
    public static final String IS_SFG_ALL_PARTNERS = "isAllSfgPartners";

    /** The Constant sfg_partner_visibility. */
    public static final String PARTNER_SFG_VISIBILITY = "sfgPartnerVisibility";
    
    public static final String ADD_PARTNERS_ACCEESS = "addPartnersAccess";

    public static final String ADD_PARTNER_ID = "partnerId";
    public static final String ADD_PARTNER_NAME = "partnerName";
    public static final String ADD_WEBFORMS = "webForms";
    public static final String ADD_TPM = "tpm";
    public static final String ADD_MSCVP = "mscvp";
    public static final String ADD_USER_TYPE = "userType";

    @Autowired
	private RsaKeyUtil rsaKeyUtil;


    /**
     * Logged in user details.
     *
     * @param loginId  the login id
     * @param password the password
     * @return the string
     */
//    public String loggedInUserDetails(final String loginId, final String password) {
//        final JSONObject jsonObject = new JSONObject();
//        final List<String> partnersList = new ArrayList<>();
//        partnersList.add("ALL");
//        try {
//            final UserInfoBean userInfo = userInformation(loginId);
//            if (nonNull(userInfo)) {
//            	//System.out.println("id"+userInfo.getLoginId());
//            	//System.out.println("userInfo"+userInfo.getPassword());
//               String decryptedPwd = PasswordUtil.decryptPassword(userInfo.getPassword());
//              // System.out.println("password"+decryptedPwd);
//                if (decryptedPwd.equals(password)) {
//                	//System.out.println("password"+decryptedPwd);
//                    if ("A".equals(userInfo.getActive())) {
//                        Map<Integer, Integer> userRolesMap = userRoles(userInfo.getUserId());
//                        final int primaryRole = nonNull(userRolesMap.get(1)) ? userRolesMap.get(1)
//                                : Integer.parseInt(userDefaultRoleId);
//                       // int primaryFlowId = dataSourceDataProvider.getPrimaryFlowID(userInfo.getUserId());
////                        if (primaryFlowId != 0) {
////                            List<String> isAll = new ArrayList<>();
////                            isAll.add("ALL");
////                            List<String> isAllSfg = new ArrayList<>();
////                            isAllSfg.add("ALL");
//                           // List<String> partnersData = dataSourceDataProvider.getUsersPartners(userInfo.getUserId());
//                            //List<String> sfgPartnersData = dataSourceDataProvider.getSfgUsersPartners(userInfo.getUserId());
//                            jsonObject.put(SES_USER_ID, userInfo.getUserId());
//                            jsonObject.put(SES_LOGIN_ID, userInfo.getLoginId());
//                            jsonObject.put(ACTIVE_FLAG, userInfo.getActive());
//                            jsonObject.put(SES_USER_NAME, userInfo.getFirstName() + " " + userInfo.getLastName());
//                            jsonObject.put(SES_EMAIL_ID, userInfo.getEmail());
//                            //jsonObject.put(SES_USER_DEFAULT_FLOWID, primaryFlowId);
//                            jsonObject.put(SES_ROLE_ID, primaryRole);
//                            //jsonObject.put(SES_USER_FLOW_MAP, dataSourceDataProvider.getFlows(userInfo.getUserId()));
//                           // jsonObject.put(SES_STATES_MAP, dataSourceDataProvider.getStates());
//                           // jsonObject.put(MSCVPROLE, partnersData);
//                          //  jsonObject.put(DEFAULT_FLOW_NAME,
//                             //       dataSourceDataProvider.getFlowNameByFlowId(primaryFlowId));
//                           // jsonObject.put(IS_ALL_PARTNERS, partnersData.equals(isAll) ? true : false);
//                           // jsonObject.put(PARTNER_VISIBILITY, partnersData);
//                          //  jsonObject.put(PARTNER_SFG_VISIBILITY, sfgPartnersData);
//                           // jsonObject.put(IS_SFG_ALL_PARTNERS,sfgPartnersData.equals(isAll) ? true : false);
////                            jsonObject.put(ADD_PARTNERS_ACCEESS, userInfo.isAddPartnersAccess()?true:false);
//                            jsonObject.put(ADD_PARTNER_ID, userInfo.getPartnerId());
//                            jsonObject.put(ADD_PARTNER_NAME, userInfo.getPartnerName());
//                            jsonObject.put(ADD_WEBFORMS, userInfo.getWebForms());
//                            jsonObject.put(ADD_TPM, userInfo.getTpm());
//                            jsonObject.put(ADD_MSCVP, userInfo.getMscvp());
//                            jsonObject.put(ADD_USER_TYPE, userInfo.getUserType());                            
//                        } else {
//                            jsonObject.put(REQ_ERROR_INFO,
//                                    "Access Denied, Please contact Admin!");
//                        }
//                    } else {
//                        jsonObject.put(REQ_ERROR_INFO,
//                                "Sorry! Your account was InActive, Please contact Admin!");
//                    }
//                } else {
//                    jsonObject.put(REQ_ERROR_INFO, "Please Login with valid UserId and Password!");
//                }
////            } else {
////                jsonObject.put(REQ_ERROR_INFO,
////                        "Please Login with valid UserId and Password!");
////            }
//        } catch (Exception exception) {
//        	exception.printStackTrace();
//            logger.log(Level.ERROR, "loggedInUserDetails :: " + exception.getMessage());
//        }
//        return jsonObject.toString();
//    }

    public String loggedInUserDetails(final String loginId, final String password) throws AuthenticationException {
   	 String decryptedUsername = null;
        String decryptedPassword = null;
        try {
            decryptedUsername = rsaKeyUtil.decryptUsername(loginId);
            decryptedPassword = rsaKeyUtil.decryptPassword(password);
        }catch (Exception e) {
            throw new AuthenticationServiceException("Decryption failed for username or password", e);
		}
        System.out.println("Username "+decryptedUsername+" Passcode "+decryptedPassword);
       final JSONObject jsonObject = new JSONObject();
       final List<String> partnersList = new ArrayList<>();
       partnersList.add("ALL");
       try {
           
           final UserInfoBean userInfo = userInformation(decryptedUsername);
           if (nonNull(userInfo)) {
              String decryptedPwd = PasswordUtil.decryptPassword(userInfo.getPassword());
               if (decryptedPwd.equals(decryptedPassword)) {
                   if ("A".equals(userInfo.getActive())) {
                       Map<Integer, Integer> userRolesMap = userRoles(userInfo.getUserId());
                       final int primaryRole = nonNull(userRolesMap.get(1)) ? userRolesMap.get(1)
                               : Integer.parseInt(userDefaultRoleId);
                           jsonObject.put(SES_USER_ID, userInfo.getUserId());
                           jsonObject.put(SES_LOGIN_ID, userInfo.getLoginId());
                           jsonObject.put(ACTIVE_FLAG, userInfo.getActive());
                           jsonObject.put(SES_USER_NAME, userInfo.getFirstName() + " " + userInfo.getLastName());
                           jsonObject.put(SES_EMAIL_ID, userInfo.getEmail());
                           jsonObject.put(SES_ROLE_ID, primaryRole);
                           jsonObject.put(ADD_PARTNER_ID, userInfo.getPartnerId());
                           jsonObject.put(ADD_PARTNER_NAME, userInfo.getPartnerName());
                           jsonObject.put(ADD_WEBFORMS, userInfo.getWebForms());
                           jsonObject.put(ADD_TPM, userInfo.getTpm());
                           jsonObject.put(ADD_MSCVP, userInfo.getMscvp());
                           jsonObject.put(ADD_USER_TYPE, userInfo.getUserType());                            
                       } else {
                           jsonObject.put(REQ_ERROR_INFO,
                                   "Access Denied, Please contact Admin!");
                       }
                   } else {
                       jsonObject.put(REQ_ERROR_INFO,
                               "Sorry! Your account was InActive, Please contact Admin!");
                   }
               } else {
                   jsonObject.put(REQ_ERROR_INFO, "Please Login with valid UserId and Password!");
               }
       } catch (Exception exception) {
       	exception.printStackTrace();
           logger.log(Level.ERROR, "loggedInUserDetails :: " + exception.getMessage());
       }
       return jsonObject.toString();
   }
    /**
     * User information.
     *
     * @param loginId the login id
     * @return the user info bean
     */
    public UserInfoBean userInformation(final String loginId) {
        UserInfoBean userInfo = null;
        try {
        	final List<Map<String, Object>> rows = jdbcTemplate.queryForList(
					"SELECT id, loginid, passwd, fnme, lnme, email, dept_id, active, file_visibility,partner_name,partner_id,webforms,tpm,mscvp,user_type FROM m_user WHERE loginid='"
							+ loginId + "' ");
            for (final Map<String, Object> row : rows) {
                userInfo = new UserInfoBean();
                userInfo.setUserId(nonNull(row.get("id")) ? (Long) row.get("id") : 0L);
                userInfo.setLoginId(nonNull(row.get("loginid")) ? (String) row.get("loginid") : "-");
                userInfo.setPassword(nonNull(row.get("passwd")) ? (String) row.get("passwd") : "-");
                userInfo.setFirstName(nonNull(row.get("fnme")) ? (String) row.get("fnme") : "-");
                userInfo.setLastName(nonNull(row.get("lnme")) ? (String) row.get("lnme") : "-");
                userInfo.setEmail(nonNull(row.get("email")) ? (String) row.get("email") : "-");
                userInfo.setDeptartmentId(nonNull(row.get("dept_id")) ? (Integer) row.get("dept_id") : 0);
                userInfo.setActive(nonNull(row.get("active")) ? (String) row.get("active") : "-");
                userInfo.setPartnerId(nonNull(row.get("partner_id")) ? (String) row.get("partner_id") : "-");
                userInfo.setPartnerName(nonNull(row.get("partner_name")) ? (String) row.get("partner_name") : "-");
                userInfo.setWebForms(nonNull(row.get("webforms")) ? (String) row.get("webforms") : "false");
                userInfo.setTpm(nonNull(row.get("tpm")) ? (String) row.get("tpm") : "false");
                userInfo.setMscvp(nonNull(row.get("mscvp")) ? (String) row.get("mscvp") : "false");
                userInfo.setUserType(nonNull(row.get("user_type")) ? (String) row.get("user_type") : "-");
                boolean fileVisibility = false;
                if(row.get("file_visibility").equals(true) ) {
                    fileVisibility=true;
                }
                userInfo.setFileVisibility(fileVisibility);
                boolean addPartnersAccess = false;
                if(row.get("add_partners").equals(true)) {
                	addPartnersAccess = true;
                }
                	userInfo.setAddPartnersAccess(addPartnersAccess);
            }
        } catch (Exception exception) {
            logger.log(Level.ERROR, " userInformation :: " + exception.getMessage());
        }
        return userInfo;
    }

    /**
     * User roles.
     *
     * @param userId the user id
     * @return the map
     */
    public Map<Integer, Integer> userRoles(final long userId) {
        final Map<Integer, Integer> rolesMap = new HashMap<Integer, Integer>();
        try {
            final List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT priority, role_id FROM m_user_roles WHERE user_id='" + userId + "' ORDER BY priority");
            for (final Map<String, Object> row : rows) {
                rolesMap.put((Integer) row.get("priority"), (Integer) row.get("role_id"));
            }
        } catch (Exception exception) {
            logger.log(Level.ERROR, " userRoles :: " + exception.getMessage());
        }
        return rolesMap;
    }
    
    public String generatePasscode(final String loginId) {
        int updatedRows = 0;
        String responseString = "Something went wrong!";
        
        try {
            final UserInfoBean userInfo = userInformation(loginId);

            if (nonNull(userInfo) && "A".equals(userInfo.getActive())) {
                String selectQuery = "SELECT email, fnme FROM m_user WHERE loginid = ?";
                Map<String, Object> result = jdbcTemplate.queryForMap(selectQuery, loginId);

                if (result != null && !result.isEmpty()) {
                    // Using getOrDefault to handle null cases
                    String email = (String) result.getOrDefault("email", "");
                    String firstName = (String) result.getOrDefault("fnme", "");

                    // Log the retrieved values for debugging
                    logger.info("Retrieved email: {}, firstName: {}", email, firstName);

                    // Validate that email and firstName are not empty
                    if (!email.isEmpty() && !firstName.isEmpty()) {
                        // Generate a 6-digit OTP
                        Random rnd = new Random();
                        int number = rnd.nextInt(999999);
                        String otp = String.format("%06d", number);

                        // Update OTP in the database
                        String updateQuery = "UPDATE m_user SET otp= ?, otp_generated_time = CURRENT_TIMESTAMP WHERE loginid = ?";
                        updatedRows = jdbcTemplate.update(updateQuery, otp, loginId);

                        if (updatedRows > 0) {
                            logger.info("OTP generated: {}", otp);
                            mailManager.sendPassCodeToUser(email, firstName, otp);
                            responseString = "OTP updated and sent successfully.";
                        } else {
                            responseString = "Failed to update OTP!";
                        }
                    } else {
                        responseString = "User email or first name is missing!";
                    }
                } else {
                    responseString = "User not found!";
                }
            } else {
                responseString = "User is inactive or does not exist!";
            }
        } catch (Exception exception) {
            logger.error("generatePasscode :: Error generating OTP - {}", exception.getMessage(), exception);
        }

        return responseString;
    }




public String validateOtp(final String loginId, final String otp) {

		String response = null;
		final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
		try {
			final List<Map<String, Object>> rows = jdbcTemplate
					.queryForList("SELECT otp,otp_generated_time  FROM m_user WHERE loginid='" + loginId + "'");
			System.out.println("rows:::::::::::::" + rows);
			if (rows != null) {
				for (final Map<String, Object> row : rows) {
					if (row.get("otp").toString().equals(otp)) {
						String otpTime = row.get("otp_generated_time").toString();
						
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(new Date(System.currentTimeMillis()));
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						sdf.setTimeZone(TimeZone.getTimeZone(defaultTimeZone));
						sdf.setTimeZone(TimeZone.getDefault());
						Date currentTime = calendar.getTime();
						System.out.println("22222222:::" + currentTime);
						
						calendar.setTime(sdf.parse(otpTime));
						sdf.format(calendar.getTime());
						Date otpGen = calendar.getTime();
						System.out.println("11111111111:::" + otpGen);

						long diff = currentTime.getTime() - otpGen.getTime();

						long diffMinutes = diff / (60 * 1000) % 60;
						if (diffMinutes > 10) {
							response = "OTP time exceeded, Please Login again";
							System.out.println("Entered otp not matched");
							return response;
						}else {
							response = "Login Successful";
						}
						return  "Otp Verified and Success";
					} else {
						response = "Entered otp not matched";
						System.out.println("Entered otp not matched");
						return response;
					}
				}
			} else {
				System.out.println("No Data found with the loginId");
				response = "No Data found with the loginId";
				return response;

			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " userRoles :: " + exception.getMessage());
		}
		return response;
	}
public Configurations getConfigurations() {
	Configurations configurations = null;
	try {
		final List<Map<String, Object>> rows = jdbcTemplate.queryForList(
				"SELECT * FROM backend_configurations"
						);
		for (final Map<String, Object> row : rows) {
			configurations = new Configurations();
			configurations.setBpidLink(nonNull(row.get("bpid_link")) ? (String) row.get("bpid_link") : "-");
			configurations.setEnvironment(nonNull(row.get("environment")) ? (String) row.get("environment") : "-");
			configurations.setGlobalColor(nonNull(row.get("global_color")) ? (String) row.get("global_color") : "-");
			configurations.setGlobalImg(nonNull(row.get("global_img")) ? (String) row.get("global_img") : "-");
			configurations.setRecieve_997(nonNull(row.get("997_recieve")) ? (String) row.get("997_recieve") : "-");
			configurations.setS3BucketAccessKey(nonNull(row.get("s3_bucket_access_key")) ? (String) row.get("s3_bucket_access_key") : "-");
			configurations.setS3BucketSecretKey(nonNull(row.get("s3_bucket_sceret_key")) ? (String) row.get("s3_bucket_sceret_key") : "_");
			configurations.setSubmit_810(nonNull(row.get("810_submit")) ? (String) row.get("810_submit") : "-");
			configurations.setSubmit_855(nonNull(row.get("855_submit")) ? (String) row.get("855_submit") : "-");
			configurations.setSubmit_856(nonNull(row.get("856_submit")) ? (String) row.get("856_submit") : "-");
			configurations.setSubmit_997(nonNull(row.get("997_submit")) ? (String) row.get("997_submit") : "-");
			configurations.setRecieve_997(nonNull(row.get("997_recieve")) ? (String) row.get("997_recieve") : "-");
			configurations.setCreatedBy(nonNull(row.get("created_by")) ? (String) row.get("created_by") : "-");
			configurations.setModifiedBy(nonNull(row.get("modified_by")) ? (String) row.get("modified_by") : "-");
			configurations.setId(nonNull(row.get("id")) ? (Long) row.get("id") : 0L);
			configurations.setWebFormsTutorialLink(nonNull(row.get("webforms_tutorial_link")) ? (String) row.get("webforms_tutorial_link") : "-");
			configurations.setMscvpTutorialLink(nonNull(row.get("mscvp_tutorial_link")) ? (String) row.get("mscvp_tutorial_link") : "-");
			configurations.setFooter(nonNull(row.get("footer_name")) ? (String) row.get("footer_name") : "-");
			configurations.setSmtpHostName(nonNull(row.get("smtp_host_name")) ? (String) row.get("smtp_host_name") : "-");
			configurations.setSmtpFromMailId(nonNull(row.get("smtp_from_mailid")) ? (String) row.get("smtp_from_mailid") : "-");
			configurations.setSmtpPort(nonNull(row.get("smtp_port")) ? (String) row.get("smtp_port") : "-");
    		configurations.setSmtpFromidPwd(nonNull(row.get("smtp_fromid_pwd")) ? (String) row.get("smtp_fromid_pwd") : "-");
    		 configurations.setB2bReprocessUrl(nonNull(row.get("b2b_reprocess_url")) ? (String) row.get("b2b_reprocess_url") : "-");
    		 configurations.setB2bReprocessSfgUrl(nonNull(row.get("b2b_reprocess_sfg_url")) ? (String) row.get("b2b_reprocess_sfg_url") : "-");
    		 configurations.setS3BbucketName(nonNull(row.get("s3_bucket_name")) ? (String) row.get("s3_bucket_name") : "-");
    		 configurations.setS3BbucketRegion(nonNull(row.get("s3_bucket_region")) ? (String) row.get("s3_bucket_region") : "-");
		}

	} catch (Exception exception) {
		logger.log(Level.ERROR, " userInformation :: " + exception.getMessage());
	}
	return configurations;
}
public String saveConfigurations(Configurations configurations) {
	int insertedCount=0;
	String returnString = null;
	
	final List<Map<String, Object>> rows = jdbcTemplate.queryForList(
			"SELECT * from  backend_configurations");
	System.out.print("rows"+ rows.size()+"printed"+rows);
	try {
		if (rows.size()==0) {
			
	insertedCount=jdbcTemplate.update(
			"INSERT INTO backend_configurations (global_img, global_color, 856_submit,"
			+ " environment,810_submit,855_submit,s3_bucket_access_key,s3_bucket_sceret_key,"
			+ "bpid_link,997_submit,997_recieve,created_by,modified_by,created_ts,modified_ts,"
			+ "webforms_tutorial_link,"
			+ "mscvp_tutorial_link,"
			+ "footer_name,"
			+ "smtp_host_name,"
			+ "smtp_from_mailid,"
			+ "smtp_port,"
			+ "b2b_reprocess_url,"
			+ "b2b_reprocess_sfg_url,"
			+ "s3_bucket_name,s3_bucket_region) VALUES (?, ?, ?, ?,?,?,?,?,?,?,?,?,?,current_timestamp,current_timestamp,?,?,?,?,?,?,?,?,?,?)",
			new Object[] { configurations.getGlobalImg(),configurations.getGlobalColor(),
					configurations.getSubmit_856(),
					configurations.getEnvironment(),configurations.getSubmit_810(),configurations.getSubmit_855(),
					configurations.getS3BucketAccessKey(),configurations.getS3BucketSecretKey(),configurations.getBpidLink(),
					configurations.getSubmit_997(),configurations.getRecieve_997(),configurations.getCreatedBy(),configurations.getModifiedBy(),configurations.getWebFormsTutorialLink(),configurations.getMscvpTutorialLink(),configurations.getFooter(),
					configurations.getSmtpHostName(),configurations.getSmtpFromMailId(),configurations.getSmtpPort(),configurations.getB2bReprocessUrl(),configurations.getB2bReprocessSfgUrl(),configurations.getS3BbucketName(),configurations.getS3BbucketRegion()}) ;
	if(insertedCount>0) {
		returnString="Date inserted Successfully";
	}else {
		returnString="Date not inserted,Something went wrong";
	}
}else {
	returnString="Please use update call to update data";	
		}
	}catch (Exception exception) {
		logger.log(Level.ERROR, "Backend Configurations :: " + exception.getMessage());
	}
	return returnString;
}
//saveConfigurations




public String updateConfigurations(Configurations configurations) {
	int insertedCount=0;
	String returnString = null;
	try {
				
	insertedCount=jdbcTemplate.update(
			"UPDATE backend_configurations SET global_img=?, global_color=?, 856_submit=?,"
			+ " environment=?,810_submit=?,855_submit=?,s3_bucket_access_key=?,s3_bucket_sceret_key=?,"
			+ "bpid_link=?,997_submit=?,997_recieve=?,created_by=?,modified_by=?,modified_ts=current_timestamp, webforms_tutorial_link=?,mscvp_tutorial_link=?,footer_name=?,smtp_host_name=?,smtp_from_mailid=?,smtp_port=?,b2b_reprocess_url=?,b2b_reprocess_sfg_url=?,s3_bucket_name=?, s3_bucket_region=? WHERE id="+configurations.getId(),
			new Object[] { configurations.getGlobalImg(),configurations.getGlobalColor(),
					configurations.getSubmit_856(),
					configurations.getEnvironment(),configurations.getSubmit_810(),configurations.getSubmit_855(),
					configurations.getS3BucketAccessKey(),configurations.getS3BucketSecretKey(),configurations.getBpidLink(),
					configurations.getSubmit_997(),configurations.getRecieve_997(),configurations.getCreatedBy(),configurations.getModifiedBy(),configurations.getWebFormsTutorialLink(),configurations.getMscvpTutorialLink(),configurations.getFooter(),
					configurations.getSmtpHostName(),configurations.getSmtpFromMailId(),configurations.getSmtpPort(),configurations.getB2bReprocessUrl(),configurations.getB2bReprocessSfgUrl(),configurations.getS3BbucketName(),configurations.getS3BbucketRegion()}) ;
	if(insertedCount>0) {
		returnString="Data updated Successfully";
	}else {
		returnString="Data update failed,Something went wrong";
	}
	}catch (Exception exception) {
		logger.log(Level.ERROR, "Backend Configurations :: " + exception.getMessage());
	}
	return returnString;
}

}
