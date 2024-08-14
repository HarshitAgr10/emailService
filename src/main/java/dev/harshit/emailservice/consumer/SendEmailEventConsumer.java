package dev.harshit.emailservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.harshit.emailservice.dtos.SendEmailDto;
import dev.harshit.emailservice.util.EmailUtil;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;


@Service
public class SendEmailEventConsumer {

    private ObjectMapper objectMapper;

    public SendEmailEventConsumer() {
        objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = "sendEmail", groupId = "emailService")
    public void handleSendEmailEvent(String message) throws JsonProcessingException {
//        System.out.println("Sending email: " + message);

        SendEmailDto sendEmailDto = objectMapper.readValue(message, SendEmailDto.class);

        /**
         Outgoing Mail (SMTP) Server
         requires TLS or SSL: smtp.gmail.com (use authentication)
         Use Authentication: Yes
         Port for TLS/STARTTLS: 587
         **/

        final String fromEmail = sendEmailDto.getFrom();
        final String toEmail = sendEmailDto.getTo();
        final String subject = sendEmailDto.getSubject();
        final String body = sendEmailDto.getBody();

        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, "password");
            }
        };
        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session, toEmail, subject, body);

    }
}





/*
* @KafkaListener
* Used in Spring Boot to consume messages from Kafka topics
* Marks a method to be the target of a Kafka message listener on the specified topic(s)
* topics : The Kafka topic(s) to listen to
* groupId : The consumer group ID
*/
 