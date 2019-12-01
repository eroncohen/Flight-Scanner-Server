package smartspace.plugin;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail{
    private static final String USER_NAME = "FlightScannerr";  // GMail user name (just the part before "@gmail.com")
    private static final String PASSWORD = "2019b.rickyd"; // GMail password
    private static String BODY = "The price of the Flight that you are interested got lower";
    private static final String subject = "Alert from Flight scanner, the price of your flight get lower ";
    private static final String from = USER_NAME;
    private static final String pass = PASSWORD;
	
	public static void sendFromGMail(String userMailAddress,String urlAddress ) {  
		 BODY = BODY + "   \n" + urlAddress;
	   	 String toMailAdress = userMailAddress; 
		 Properties props = System.getProperties();
		  String host = "smtp.gmail.com";
		  props.put("mail.smtp.starttls.enable", "true");
		  props.put("mail.smtp.host", host);
		  props.put("mail.smtp.user", from);
		  props.put("mail.smtp.password", pass);
		  props.put("mail.smtp.port", "587");
		  props.put("mail.smtp.auth", "true");

		  Session session = Session.getDefaultInstance(props);
		  MimeMessage message = new MimeMessage(session);

		    try {
		        message.setFrom(new InternetAddress(from));
		        InternetAddress toAddress = new InternetAddress(toMailAdress);
		        message.addRecipient(Message.RecipientType.TO, toAddress);
		        message.setSubject(subject);
		        message.setText(BODY);
		        Transport transport = session.getTransport("smtp");
		        transport.connect(host, from, pass);
		        transport.sendMessage(message, message.getAllRecipients());
		        transport.close();
		    }
		    catch (AddressException ae) {
		        ae.printStackTrace();
		    }
		    catch (MessagingException me) {
		        me.printStackTrace();
		    }
	 }

}