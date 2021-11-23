package fr.polytech.mtp.coviwad_geolocation.services;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender sender;

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    public void sendEmail(String id, String content, String object) throws MessagingException {

        MimeMessage message = sender.createMimeMessage();

        //get email from id user
        var keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();

        UsersResource usersResource = keycloak.realm(realm).users();
        UserResource userResource = usersResource.get(id);

        // Enable the multipart flag!
        MimeMessageHelper helper = new MimeMessageHelper(message,false);

        helper.setTo(userResource.toRepresentation().getEmail());
        helper.setText(content);
        helper.setSubject(object);

        sender.send(message);
    }
}
