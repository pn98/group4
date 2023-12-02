package group4.passwordmanager.model;
import java.util.ArrayList;
import java.util.List;

public class CredentialStorage {
    private final List<Credential> credentials = new ArrayList<>();

    public Credential retrieveByEmail(String emailOrUsername) {
        return credentials.stream()
                .filter(cred -> emailOrUsername.equals(cred.getEmailOrUsername()))
                .findFirst()
                .orElse(null);
    }

    public void update(Credential credential) {
        System.out.println("Updating credential for: " + credential.getEmailOrUsername());
    }


    public void store(Credential credential) {
        credentials.add(credential);
    }

    public List<Credential> getAllCredentials() {
        return credentials;
    }
}
