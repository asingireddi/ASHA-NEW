package com.miraclesoft.scvp.mail;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.miraclesoft.scvp.model.Configurations;
import com.miraclesoft.scvp.service.impl.LoginServiceImpl;

/**
 * The Class MailManager.
 *
 * @author Priyanka Kolla
 */
@Component
public class MailManager {

	/** The mss SMTP host name. */
	/** The application url. */
	@Value("${applicationUrl}")
	private String applicationUrl;

	@Autowired
	private Configurations configurations;
	/** The JavaMailSender provider. */
	@Autowired
	private JavaMailSender sendEmail;
	
	@Autowired
	   private LoginServiceImpl loginServiceImpl; 

	/** The logger. */
	private static Logger logger = LogManager.getLogger(MailManager.class.getName());

	/**
	 * check which environment.
	 * 
	 * @return the string
	 */
	public String getEnvironment() {
		String envName = null;
		String envNames[] = { "local", "dev", "qa", "prod" };
		for (int i = 0; i < envNames.length; i++) {
			if (applicationUrl.contains(envNames[i])) {
				envName = envNames[i];
			}
		}
		return envName;
	}

	/**
	 * Send user login id and password.
	 *
	 * @param toEmail  the to email
	 * @param loginId  the login id
	 * @param userName the user name
	 * @param password the password
	 * @throws AddressException   the address exception
	 * @throws MessagingException the messaging exception
	 */
	public void sendUserLoginIdAndPassword(final String toEmail, final String loginId, final String userName,
			final String password, final String partnerId, final String partnerName) throws AddressException, MessagingException {
		try {
			// sets SMTP server properties
			final Properties properties = new Properties();
			properties.put("mail.smtp.host", configurations.getSmtpHostName());
			properties.put("mail.smtp.port", Integer.parseInt(configurations.getSmtpPort()));
			properties.put("mail.smtp.auth", "true");
			//properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.user", configurations.getSmtpFromMailId());
			properties.put("mail.password",  configurations.getSmtpFromidPwd());

			// creates a new session with an authenticator
			final Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(configurations.getSmtpFromMailId(), configurations.getSmtpFromidPwd());
				}
			};

			final Session session = Session.getInstance(properties, auth);
			// creates a new e-mail message
			final Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(configurations.getSmtpFromMailId()));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			msg.setSubject("Account created!!");
			msg.setSentDate(new Date());
			// creates message part
			final MimeBodyPart messageBodyPart = new MimeBodyPart();
			final StringBuilder htmlText = new StringBuilder();
//            htmlText.append("<html><head><title>MSCVP account</title>");

			htmlText.append("<html><head><title>WebForms Account created successfully</title>");
			htmlText.append("</head><body>");
			htmlText.append("<p>Hello <b>" + userName + "</b>,</p>");
			htmlText.append("<p>Welcome to Rehlko " + getEnvironment()
					+ " Portal! Your account has been created with the following credentials. Please login and change your password.<br><br></p>");
			htmlText.append("<p>Login Id: " + loginId + "</p>");
			htmlText.append("<p>Password: " + password + "</p>");
			if(partnerId!= null && partnerName!= null)
			{
			htmlText.append("<p>Partner Id: " + partnerId + "</p>");
			htmlText.append("<p>Partner Name: " + partnerName + "</p>");
			}
			htmlText.append(
					"<br>Please <a href='" + applicationUrl + "' target='_blank' >click here</a> to login.<br>");
			htmlText.append("<p><u><font size='2'>Regards</font></u><br>");
			htmlText.append("Rehlko Team</p><br>");
			htmlText.append(
					"<b>*Note:</b> <font color='red', size='2' face='Arial'>Please do not reply to this email as this is an automated notification.</font>");
			htmlText.append("</body></html>");
			messageBodyPart.setContent(htmlText.toString(), "text/html");
			// creates multi-part
			final Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			// sets the multi-part as e-mail's content
			msg.setContent(multipart);
			// sends the e-mail
			Transport.send(msg);
		} catch (Exception exception) {
			logger.log(Level.ERROR, " sendUserLoginIdAndPassword :: " + exception.getMessage());
		}
	}

	/**
	 * Send updated password.
	 *
	 * @param toEmail  the to email
	 * @param userName the user name
	 * @param password the password
	 * @throws AddressException   the address exception
	 * @throws MessagingException the messaging exception
	 */
	public void sendUpdatedPassword(final String toEmail, final String userName, final String password)
			throws AddressException, MessagingException {
		try {
			// sets SMTP server properties
			final Properties properties = new Properties();
			properties.put("mail.smtp.host", configurations.getSmtpHostName());
			properties.put("mail.smtp.port", Integer.parseInt(configurations.getSmtpPort()));
			properties.put("mail.smtp.auth", "true");
			//properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.user", configurations.getSmtpFromMailId());
			properties.put("mail.password",  configurations.getSmtpFromidPwd());

			// creates a new session with an authenticator
			final Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(configurations.getSmtpFromMailId(), configurations.getSmtpFromidPwd());
				}
			};

			final Session session = Session.getInstance(properties, auth);
			// creates a new e-mail message
			final Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(configurations.getSmtpFromMailId()));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			msg.setSubject("Password Updated");
			msg.setSentDate(new Date());
			// creates message part
			final MimeBodyPart messageBodyPart = new MimeBodyPart();
			final StringBuilder htmlText = new StringBuilder();
			htmlText.append("<html><head><title>MSCVP account password updated</title>");
			htmlText.append("</head><body>");
			htmlText.append("<p>Hello <b>" + userName + "</b>,</p>");
			htmlText.append("<p>Welcome to Miracle's Supply Chain Visibility " + getEnvironment()
					+ " Portal! Your password has been updated successfully. Please login and change your password .<br><br></p>");
			// htmlText.append("<p>Login Id: " + loginId + "</p>");
			htmlText.append("<p>Updated password: " + password + "</p>");
			htmlText.append(
					"<br>Please <a href='" + applicationUrl + "' target='_blank' >click here</a> to login.<br>");
			htmlText.append("<p><u><font size='2'>Regards</font></u><br>");
			htmlText.append("Team</p><br>");
			htmlText.append(
					"<b>*Note:</b> <font color='red', size='2' face='Arial'>Please do not reply to this email as this is an automated notification.</font>");
			htmlText.append("</body></html>");
			messageBodyPart.setContent(htmlText.toString(), "text/html");
			// creates multi-part
			final Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			// sets the multi-part as e-mail's content
			msg.setContent(multipart);
			// sends the e-mail
			Transport.send(msg);
		} catch (Exception exception) {
			logger.log(Level.ERROR, " sendUpdatedPassword :: " + exception.getMessage());
		}
	}

	/**
	 * Send mail with attachment.
	 *
	 * @param type     the type
	 * @param toEmails the to emails
	 * @param ccEmails the cc emails
	 * @param filePath the file path
	 * @throws AddressException   the address exception
	 * @throws MessagingException the messaging exception
	 */
	public final void sendMailWithAttachment(final String type, final Set<String> toEmails, final Set<String> ccEmails,
			final String filePath) throws AddressException, MessagingException {
		try {
			// sets SMTP server properties
			final Properties properties = new Properties();
			properties.put("mail.smtp.host", configurations.getSmtpHostName());
			properties.put("mail.smtp.port", Integer.parseInt(configurations.getSmtpPort()));
			properties.put("mail.smtp.auth", "true");
			//properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.user", configurations.getSmtpFromMailId());
			properties.put("mail.password",  configurations.getSmtpFromidPwd());

			// creates a new session with an authenticator
			final Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(configurations.getSmtpFromMailId(), configurations.getSmtpFromidPwd());
				}
			};

			final Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			final Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(configurations.getSmtpFromMailId()));
			for (final String toEmail : toEmails) {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			}
			if (ccEmails.size() > 0) {
				for (final String ccEmail : ccEmails) {
					msg.addRecipient(Message.RecipientType.CC, new InternetAddress(ccEmail));
				}
			}
			msg.setSubject("MSCVP " + type + " Report");
			msg.setSentDate(new Date());

			// creates message part
			final MimeBodyPart messageBodyPart = new MimeBodyPart();
			final StringBuilder htmlText = new StringBuilder();
			htmlText.append("<html><head><title>" + type + " report</title>");
			htmlText.append("</head><body>");
			htmlText.append("<p>Dear Team,</p>");
			htmlText.append("<p>Please find the " + type + " report in the attachment.<br><br></p>");
			htmlText.append("<p><u><font size='2'><b>Regards</b></font></u><br>");
			htmlText.append("MSVP Team</p><br>");
			htmlText.append(
					"<font color='red', size='2' face='Arial'><b>*Note:</b> Please do not reply to this email as this is an automated notification.</font>");
			htmlText.append("</body></html>");

			messageBodyPart.setContent(htmlText.toString(), "text/html");
			// creates multi-part
			final Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			final MimeBodyPart attachPart = new MimeBodyPart();
			try {
				attachPart.attachFile(filePath);
			} catch (IOException ioException) {
				logger.log(Level.ERROR, " sendMailWithAttachment :: " + ioException.getMessage());
			}
			multipart.addBodyPart(attachPart);
			// sets the multi-part as e-mail's content
			msg.setContent(multipart);
			// sends the e-mail
			Transport.send(msg);
		} catch (Exception exception) {
			logger.log(Level.ERROR, " sendMailWithAttachment :: " + exception.getMessage());
		}
	}

	/**
	 * Send mail with no reports.
	 *
	 * @param type     the type
	 * @param toEmails the to emails
	 * @param ccEmails the cc emails
	 * @throws AddressException   the address exception
	 * @throws MessagingException the messaging exception
	 */
	public final void sendMailWithNoReports(final String type, final Set<String> toEmails, final Set<String> ccEmails)
			throws AddressException, MessagingException {
		try {
			// sets SMTP server properties
			final Properties properties = new Properties();
			properties.put("mail.smtp.host", configurations.getSmtpHostName());
			properties.put("mail.smtp.port", Integer.parseInt(configurations.getSmtpPort()));
			properties.put("mail.smtp.auth", "true");
			//properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.user", configurations.getSmtpFromMailId());
			properties.put("mail.password",  configurations.getSmtpFromidPwd());

			// creates a new session with an authenticator
			final Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(configurations.getSmtpFromMailId(), configurations.getSmtpFromidPwd());
				}
			};

			final Session session = Session.getInstance(properties, auth);
			// creates a new e-mail message
			final Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(configurations.getSmtpFromMailId()));
			for (final String toEmail : toEmails) {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			}
			if (ccEmails.size() > 0) {
				for (final String ccEmail : ccEmails) {
					msg.addRecipient(Message.RecipientType.CC, new InternetAddress(ccEmail));
				}
			}
			msg.setSubject("MSCVP " + type + " Report");
			msg.setSentDate(new Date());

			// creates message part
			final MimeBodyPart messageBodyPart = new MimeBodyPart();
			final StringBuilder htmlText = new StringBuilder();
			htmlText.append("<html><head><title>" + type + " report</title>");
			htmlText.append("</head><body>");
			htmlText.append("<p>Dear Team,</p>");
			htmlText.append("<p>No " + type + " error reports.<br><br></p>");
			htmlText.append("<p><u><font size='2'><b>Regards</b></font></u><br>");
			htmlText.append("MSVP Team</p><br>");
			htmlText.append(
					"<font color='red', size='2' face='Arial'><b>*Note:</b> Please do not reply to this email as this is an automated notification.</font>");
			htmlText.append("</body></html>");

			messageBodyPart.setContent(htmlText.toString(), "text/html");
			// creates multi-part
			final Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			// sets the multi-part as e-mail's content
			msg.setContent(multipart);
			// sends the e-mail
			Transport.send(msg);
		} catch (Exception exception) {
			logger.log(Level.ERROR, " sendMailWithNoReports :: " + exception.getMessage());
		}
	}

	/**
	 * Send updated username.
	 *
	 * @param toEmail  the to email
	 * @param userName the user name
	 * @throws AddressException   the address exception
	 * @throws MessagingException the messaging exception
	 */
	public void sendUserName(final String toEmail, final String userName) throws AddressException, MessagingException {
		try {
			// sets SMTP server properties
			final Properties properties = new Properties();
			
			properties.put("mail.smtp.host", configurations.getSmtpHostName());
			properties.put("mail.smtp.port", Integer.parseInt(configurations.getSmtpPort()));
			properties.put("mail.smtp.auth", "true");
			//properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.user", configurations.getSmtpFromMailId());
			properties.put("mail.password",  configurations.getSmtpFromidPwd());

			// creates a new session with an authenticator
			final Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(configurations.getSmtpFromMailId(), configurations.getSmtpFromidPwd());
				}
			};

			final Session session = Session.getInstance(properties, auth);
			// creates a new e-mail message
			final Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(configurations.getSmtpFromMailId()));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			msg.setSubject("MSCVP account user name");
			msg.setSentDate(new Date());
			// creates message part
			final MimeBodyPart messageBodyPart = new MimeBodyPart();
			final StringBuilder htmlText = new StringBuilder();
			htmlText.append("<html><head><title>MSCVP account username</title>");
			htmlText.append("</head><body>");
			htmlText.append("<p>Hello <b>" + userName + "</b>,</p>");
			htmlText.append(
					"<p>Welcome to Miracle's Supply Chain Visibility " + getEnvironment() + " Portal!<br><br></p>");
			// htmlText.append("<p>Login Id: " + loginId + "</p>");
			htmlText.append("<p>Kindly login with your username: " + userName + "</p>");
			htmlText.append(
					"<br>Please <a href='" + applicationUrl + "' target='_blank' >click here</a> to login.<br>");
			htmlText.append("<p><u><font size='2'>Regards</font></u><br>");
			htmlText.append("MSVP Team</p><br>");
			htmlText.append(
					"<b>*Note:</b> <font color='red', size='2' face='Arial'>Please do not reply to this email as this is an automated notification.</font>");
			htmlText.append("</body></html>");
			messageBodyPart.setContent(htmlText.toString(), "text/html");
			// creates multi-part
			final Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			// sets the multi-part as e-mail's content
			msg.setContent(multipart);
			// sends the e-mail
			Transport.send(msg);
		} catch (Exception exception) {
			logger.log(Level.ERROR, " sendUpdatedPassword :: " + exception.getMessage());
		}
	}

	/**
	 * Send Attachment.
	 *
	 * @param body        the to body
	 * @param toEmails    the user toEmails
	 * @param filePath    the filePath
	 * @param attachments the attachments
	 * @param subject     the subject
	 * @throws AddressException   the address exception
	 * @throws MessagingException the messaging exception
	 */
	public final void sendMailWithAttachment(final String body, final List<String> toEmails,
			final List<String> filePath, final List<byte[]> attachments, final String subject)
			throws AddressException, MessagingException {
		final MimeMessage msg1 = sendEmail.createMimeMessage();
		final MimeMessageHelper helper = new MimeMessageHelper(msg1, true);
		helper.setFrom(new InternetAddress(configurations.getSmtpFromMailId()));
		String[] emails = toEmails.stream().toArray(String[]::new);
		helper.setTo(emails);
		helper.setSubject(subject);
		helper.setText(body, false);
		try {
			int filePaths = 0;
			for (byte[] file : attachments) {
				ByteArrayResource fr = new ByteArrayResource(file);
				String fileName = new StringBuilder(filePath.get(filePaths)).reverse().toString();
				final int indexForlash = fileName.indexOf("/");
				fileName = new StringBuilder(fileName.substring(0, indexForlash)).reverse().toString();
				helper.addAttachment(fileName, fr);
				filePaths++;
			}
			sendEmail.send(msg1);
		} catch (final Exception exception) {
			exception.printStackTrace();
			logger.log(Level.ERROR, " sendMailWithAttachment :: " + exception.getMessage());
		}
	}

	/** Registration Success Mail **/

	public void sendRegistrationSuccessToUser(final String toEmail, final String loginId, final String userName

			) throws AddressException, MessagingException {

		try {

			// sets SMTP server properties

			final Properties properties = new Properties();

			properties.put("mail.smtp.host", configurations.getSmtpHostName());
			properties.put("mail.smtp.port", Integer.parseInt(configurations.getSmtpPort()));
			properties.put("mail.smtp.auth", "true");
			//properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.user", configurations.getSmtpFromMailId());
			properties.put("mail.password",  configurations.getSmtpFromidPwd());

			// creates a new session with an authenticator
			final Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(configurations.getSmtpFromMailId(), configurations.getSmtpFromidPwd());
				}
			};

			final Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message

			final Message msg = new MimeMessage(session);

			msg.setFrom(new InternetAddress(configurations.getSmtpFromMailId()));

			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

			msg.setSubject("Registration Success!!");

			msg.setSentDate(new Date());

			// creates message part

			final MimeBodyPart messageBodyPart = new MimeBodyPart();

			final StringBuilder htmlText = new StringBuilder();

			// htmlText.append("<html><head><title>MSCVP account</title>");

			htmlText.append("<html><head><title>Hello</title>");

			htmlText.append("</head><body>");

			htmlText.append("<p>Hello <b>" + userName + "</b>,</p>");

			htmlText.append("<p>Your Registartion Successfull " + getEnvironment()

					+ " Admin will Check and approve and you will be provided with Login User and Password<br><br></p>");

			htmlText.append("<p><u><font size='2'>Regards</font></u><br>");

			htmlText.append("Rehlko Team</p><br>");

			htmlText.append(

					"<b>*Note:</b> <font color='red', size='2' face='Arial'>Please do not reply to this email as this is an automated notification.</font>");

			htmlText.append("</body></html>");

			messageBodyPart.setContent(htmlText.toString(), "text/html");

			// creates multi-part

			final Multipart multipart = new MimeMultipart();

			multipart.addBodyPart(messageBodyPart);

			// sets the multi-part as e-mail's content

			msg.setContent(multipart);

			// sends the e-mail

			Transport.send(msg);
			
			

		} catch (Exception exception) {

			logger.log(Level.ERROR, " sendUserLoginIdAndPassword :: " + exception.getMessage());

		}

	}

	/** Registration Mail to admins **/

	public void sendRegistartionEmailToAdmins() throws AddressException, MessagingException {

		try {

			// sets SMTP server properties

			final Properties properties = new Properties();

			properties.put("mail.smtp.host", configurations.getSmtpHostName());
			properties.put("mail.smtp.port", Integer.parseInt(configurations.getSmtpPort()));
			properties.put("mail.smtp.auth", "true");
			//properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.user", configurations.getSmtpFromMailId());
			properties.put("mail.password",  configurations.getSmtpFromidPwd());

			// creates a new session with an authenticator
			final Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(configurations.getSmtpFromMailId(), configurations.getSmtpFromidPwd());
				}
			};
			final Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message

			final Message msg = new MimeMessage(session);

			msg.setFrom(new InternetAddress(configurations.getSmtpFromMailId()));

			msg.addRecipient(Message.RecipientType.TO, new InternetAddress("mchippala@miraclesoft.com"));

			msg.addRecipient(Message.RecipientType.TO, new InternetAddress("lguntuku@miraclesoft.com"));

			msg.setSubject("Action Required on New Registartion");

			msg.setSentDate(new Date());

			// creates message part

			final MimeBodyPart messageBodyPart = new MimeBodyPart();

			final StringBuilder htmlText = new StringBuilder();

			// htmlText.append("<html><head><title>MSCVP account</title>");

			htmlText.append("<html><head><title>Please check new Registartion</title>");

			htmlText.append("</head><body>");

			htmlText.append("<p>Hello <b> Admin</b>,</p>");

			htmlText.append("<p><u><font size='2'>Regards</font></u><br>");

			htmlText.append("Supply Chain Portal Team</p><br>");

			htmlText.append(

					"<b>*Note:</b> <font color='red', size='2' face='Arial'>Please do not reply to this email as this is an automated notification.</font>");

			htmlText.append("</body></html>");

			messageBodyPart.setContent(htmlText.toString(), "text/html");

			// creates multi-part

			final Multipart multipart = new MimeMultipart();

			multipart.addBodyPart(messageBodyPart);

			// sets the multi-part as e-mail's content

			msg.setContent(multipart);

			// sends the e-mail

			Transport.send(msg);

		} catch (Exception exception) {

			logger.log(Level.ERROR, " sendUserLoginIdAndPassword :: " + exception.getMessage());

		}

	}
	 public void sendPassCodeToUser(final String toEmail,final String userName, final String otp) throws AddressException, MessagingException {
	        try {
	            // sets SMTP server properties
	        	Configurations configuration = loginServiceImpl.getConfigurations();
	            final Properties properties = new Properties();
	            properties.put("mail.smtp.host", configuration.getSmtpHostName());
				properties.put("mail.smtp.port", Integer.parseInt(configuration.getSmtpPort()));
				properties.put("mail.smtp.auth", true);
				//properties.put("mail.smtp.ssl.enable", true);
				properties.put("mail.smtp.starttls.enable", "true");
				properties.put("mail.user", configuration.getSmtpFromMailId());
				properties.put("mail.password",  configuration.getSmtpFromidPwd());
				System.out.println(toEmail+"----"+userName+"----"+otp);
				// creates a new session with an authenticator
				final Authenticator auth = new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(configuration.getSmtpFromMailId(), configuration.getSmtpFromidPwd());
					}
				};
	            final Session session = Session.getInstance(properties, auth);
	            // creates a new e-mail message
	            final Message msg = new MimeMessage(session);
	            msg.setFrom(new InternetAddress(configuration.getSmtpFromMailId()));
	            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
	            msg.setSubject("OTP Generated");
	            msg.setSentDate(new Date());
	            // creates message part
	            final MimeBodyPart messageBodyPart = new MimeBodyPart();
	            final StringBuilder htmlText = new StringBuilder();
	            htmlText.append("<html><head><title>passCode</title>");
	            htmlText.append("</head><body>");
	            htmlText.append("<p>Hello <b>" + userName + "</b>,</p>");
	            htmlText.append(
	                    "<p>Welcome to Rehlko Portal!<br><br></p>");
	            // htmlText.append("<p>Login Id: " + loginId + "</p>");
	            htmlText.append("<p>Kindly enter otp to login: " + otp + "</p>");
	            htmlText.append("<p><u><font size='2'>Regards</font></u><br>");
	            htmlText.append("Rehlko Team</p><br>");
	            htmlText.append(
	                    "<b>*Note:</b> <font color='red', size='2' face='Arial'>Please do not reply to this email as this is an automated notification.</font>");
	            htmlText.append("</body></html>");
	            messageBodyPart.setContent(htmlText.toString(), "text/html");
	            // creates multi-part
	            final Multipart multipart = new MimeMultipart();
	            multipart.addBodyPart(messageBodyPart);
	            // sets the multi-part as e-mail's content
	            msg.setContent(multipart);
	            // sends the e-mail
	            Transport.send(msg);
	        } catch (Exception exception) {
	            logger.log(Level.ERROR, " sendOTP :: " + exception.getMessage());
	        }
	    }
}

