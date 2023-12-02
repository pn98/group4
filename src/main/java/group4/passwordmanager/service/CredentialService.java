package group4.passwordmanager.service;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;

public class CredentialService {

    private final CredentialStorage storage;

    public CredentialService(CredentialStorage storage) {
        this.storage = storage;
    }

    public void addCredential(Credential credential) {
        // Logic to add a credential
        storage.store(credential);
    }

    public Credential retrieveCredential(String emailOrUsername) {
        // Logic to retrieve a credential
        return storage.retrieveByEmail(emailOrUsername);
    }

    public void editPassword(String emailOrUsername, String newPassword) {
        // Logic to edit a credential's password
        Credential credential = storage.retrieveByEmail(emailOrUsername);
        if (credential != null) {
            credential.setPassword(newPassword);
            storage.update(credential);
        }
    }

    public void associateWebsite(String emailOrUsername, String website) {
        // Logic to associate a website with a credential
        Credential credential = storage.retrieveByEmail(emailOrUsername);
        if (credential != null) {
            credential.setWebsite(website);
            storage.update(credential);
        }
    }

}
