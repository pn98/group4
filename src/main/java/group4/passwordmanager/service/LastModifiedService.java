package group4.passwordmanager.service;

import group4.passwordmanager.model.Credential;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LastModifiedService {

    // Update the last modified timestamp for a credential
    public static void updateLastModified(Credential credential) {
        LocalDateTime currentTimestamp = LocalDateTime.now();
        credential.setLastModified(currentTimestamp);
        System.out.println("Last modified: " + formatTimestamp(currentTimestamp));
    }

    // Format timestamp as a string
    private static String formatTimestamp(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return timestamp.format(formatter);
    }
}
