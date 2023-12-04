package group4.passwordmanager.service;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;

public class SearchService {
    private CredentialStorage storage;

    public SearchService(CredentialStorage credentialStorage) {
    }

    // Constructor and method to search passwords
    public static void viewPasswordOnly(Credential credential) {
        System.out.println("Password: " + credential.getPassword());
    }
}
