package group4.passwordmanager.service;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CredentialService {

    private final CredentialStorage storage;

    public CredentialService(CredentialStorage storage) {
        this.storage = storage;
    }

    public List<Credential> getAllCredentials() {
        return storage.getAllCredentials();
    }

    public Credential getCredentialByIndex(int index) {
        List<Credential> credentials = getAllCredentials();
        if (index >= 0 && index < credentials.size()) {
            return credentials.get(index);
        }
        return null;
    }

    public void addCredential(Credential credential) {
        // Retrieve all credentials from the storage
        List<Credential> credentials = getAllCredentials();

        // Check if there is already a credential with the same email
        boolean emailExists = credentials.stream()
                .anyMatch(c -> c.getEmailOrUsername().equalsIgnoreCase(credential.getEmailOrUsername()));

        if (emailExists) {
            System.out.println("A credential with the same email already exists. Do you want to add another account with the same email? (Enter 'yes' or 'no')");
            Scanner scanner = null;
            String response = scanner.nextLine();

            if (response.equalsIgnoreCase("yes")) {
                // Add the new credential
                storage.store(credential);
                System.out.println("Credential added successfully.");
            } else {
                System.out.println("Account not added.");
            }
        } else {
            // Add the new credential
            storage.store(credential);
            System.out.println("Credential added successfully.");
        }
    }


    public void editCredential(int index, String newEmailOrUsername, String newPassword, String newWebsite) {
        List<Credential> credentials = storage.getAllCredentials();
        if (index >= 0 && index < credentials.size()) {
            Credential credential = credentials.get(index);

            if (!newEmailOrUsername.isEmpty()) {
                credential.setEmailOrUsername(newEmailOrUsername);
            }
            if (!newPassword.isEmpty()) {
                credential.setPassword(newPassword);
            }
            if (!newWebsite.isEmpty()) {
                credential.setWebsite(newWebsite);
            }

            storage.update(credential);
        }
    }

    public void updateCredential(Credential credential) {
        storage.update(credential);
    }

    public List<Credential> searchCredentials(String searchTerm) {
        List<Credential> matchingCredentials = new ArrayList<>();

        // Retrieve all credentials from the storage
        List<Credential> credentials = getAllCredentials();

        for (Credential credential : credentials) {
            if (credential.getEmailOrUsername().contains(searchTerm) || credential.getWebsite().contains(searchTerm)) {
                matchingCredentials.add(credential);
            }
        }

        return matchingCredentials;
    }

    public int getIndexByCredential(Credential credential) {
        List<Credential> credentials = getAllCredentials();
        for (int i = 0; i < credentials.size(); i++) {
            if (credentials.get(i).equals(credential)) {
                return i;
            }
        }
        return -1; // Return -1 if not found
    }


    public List<Credential> getCredentialsByEmail(String emailOrUsername) {
        List<Credential> matchingCredentials = new ArrayList<>();
        List<Credential> credentials = getAllCredentials();

        for (Credential credential : credentials) {
            if (credential.getEmailOrUsername().equalsIgnoreCase(emailOrUsername)) {
                matchingCredentials.add(credential);
            }
        }

        return matchingCredentials;
    }

}
