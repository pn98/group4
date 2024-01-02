package group4.passwordmanager.service;

import group4.passwordmanager.model.Credential;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LastModifiedService {

    public static void updateLastModified(Credential credential) {
        LocalDateTime currentTimestamp = LocalDateTime.now();
        credential.setLastModified(currentTimestamp);
        printLastModified(currentTimestamp);
    }

    public static void printLastModified(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedTimestamp = timestamp.format(formatter);
        System.out.println("Last modified: " + formattedTimestamp);
    }

    public static void updateLastViewed() {
        LocalDateTime currentTimestamp = LocalDateTime.now();
        CredentialService.setLastViewedTime(currentTimestamp);
        printLastViewed(currentTimestamp);
    }

    public static void printLastViewed(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedTimestamp = timestamp.format(formatter);
        System.out.println("Last viewed: " + formattedTimestamp);
    }

    public static String formatTimestamp(LocalDateTime lastViewedTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return lastViewedTime.format(formatter);
    }
}

