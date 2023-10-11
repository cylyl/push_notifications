package com.github.push;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class AppContext {
    Logger logger = LoggerFactory.getLogger(AppContext.class);


    @Value("${app.firebase-configuration-file}")
    private String firebaseConfigPath;
    @Bean
    public FirebaseAuth firebaseAuth() throws IOException {

        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())).build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase application has been initialized");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return FirebaseAuth.getInstance();
    }
    @Bean
    public PushManager pushManager() throws IOException {
	    firebaseAuth();
        return new PushManager();
    }


}
