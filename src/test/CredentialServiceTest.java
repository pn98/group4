package test;

import static org.junit.jupiter.api.Assertions.*;

import model.Credential;
import model.CredentialStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.CredentialService;

class SimpleCredentialStorageMock extends CredentialStorage {
    private Credential storedCredential;
    private String storedEmail;

    @Override
    public void store(Credential credential) {
        this.storedCredential = credential;
        this.storedEmail = credential.getEmailOrUsername();
    }

    @Override
    public Credential retrieveByEmail(String email) {
        if (email.equals(this.storedEmail)) {
            return storedCredential;
        }
        return null;
    }
}

public class CredentialServiceTest {

    private CredentialService service;
    private SimpleCredentialStorageMock storageMock;

    @BeforeEach
    public void setUp() {
        storageMock = new SimpleCredentialStorageMock();
        service = new CredentialService(storageMock);
    }

    @Test
    public void testAddCredential() {
        Credential credential = new Credential("user@example.com", "password123", "example.com");
        service.addCredential(credential);

        Credential storedCredential = storageMock.retrieveByEmail("user@example.com");
        assertNotNull(storedCredential);
        assertEquals("user@example.com", storedCredential.getEmailOrUsername());
        assertEquals("password123", storedCredential.getPassword());
    }

    @Test
    public void testRetrieveCredential() {
        Credential credential = new Credential("user@example.com", "password123", "example.com");
        storageMock.store(credential);

        Credential retrieved = service.retrieveCredential("user@example.com");

        assertNotNull(retrieved);
        assertEquals("password123", retrieved.getPassword());
    }

    @Test
    public void testEditPassword() {
        Credential credential = new Credential("user@example.com", "oldPassword123", "example.com");
        storageMock.store(credential);

        service.editPassword("user@example.com", "newPassword123");
        Credential updatedCredential = storageMock.retrieveByEmail("user@example.com");

        assertNotNull(updatedCredential);
        assertEquals("newPassword123", updatedCredential.getPassword());
    }

    // Other test cases as needed
}
