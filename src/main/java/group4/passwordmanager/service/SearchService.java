package group4.passwordmanager.service;

import group4.passwordmanager.model.Credential;

import java.util.ArrayList;
import java.util.List;

public class SearchService {

    private final CredentialService credentialService;

    public SearchService(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    public static void viewPasswordOnly(Credential credential) {
        System.out.println("Password: " + credential.getPassword());
    }

    public List<Credential> searchCredentials(String searchTerm) {
        List<Credential> matchingCredentials = new ArrayList<>();

        // Retrieve all credentials from the CredentialService
        List<Credential> credentials = credentialService.getAllCredentials();

        for (Credential credential : credentials) {
            if (credential.getEmailOrUsername().contains(searchTerm) || credential.getWebsite().contains(searchTerm)) {
                matchingCredentials.add(credential);
            }
        }

        return matchingCredentials;
    }
}
