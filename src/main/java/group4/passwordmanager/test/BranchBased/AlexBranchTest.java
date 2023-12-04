package group4.passwordmanager.test.BranchBased;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AlexBranchTest {
    private CredentialStorage storage;
    private static final String TEST_FILENAME = "test_credentials.json";
    private Credential testCredential;

    @BeforeEach
    void setUp() throws IOException {
        // Prepare a clean test environment before each test
        Files.deleteIfExists(Paths.get(TEST_FILENAME));
        storage = new CredentialStorage(TEST_FILENAME);
        testCredential = new Credential("testUser", "testPassword", "testWebsite");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up after tests
        Files.deleteIfExists(Paths.get(TEST_FILENAME));
    }

    @Test
    void storeShouldAddNewCredentialIfNotExists() {
        storage.store(testCredential);
        List<Credential> credentials = storage.getAllCredentials();
        assertTrue(credentials.contains(testCredential));
    }

    @Test
    void storeShouldUpdateCredentialIfExists() {
        storage.store(testCredential);
        Credential updatedCredential = new Credential("testUser", "updatedPassword", "testWebsite");
        storage.store(updatedCredential);

        Credential retrieved = storage.getAllCredentials().get(0);
        assertEquals("updatedPassword", retrieved.getPassword());
    }

    @Test
    void updateShouldDoNothingIfCredentialNotFound() {
        Credential nonExistingCredential = new Credential("nonExistingUser", "password", "website");
        storage.update(nonExistingCredential);
        assertTrue(storage.getAllCredentials().isEmpty());
    }

    @Test
    void updateShouldUpdateCredentialIfExists() {
        storage.store(testCredential);
        Credential updatedCredential = new Credential("testUser", "updatedPassword", "testWebsite");
        storage.update(updatedCredential);

        Credential retrieved = storage.getAllCredentials().get(0);
        assertEquals("updatedPassword", retrieved.getPassword());
    }

    @Test
    void loadCredentialsShouldNotLoadIfFileDoesNotExist() {
        // Assuming the file does not exist at this point
        storage = new CredentialStorage("nonExistingFile.json");
        assertTrue(storage.getAllCredentials().isEmpty());
    }

    @Test
    void loadCredentialsShouldNotLoadIfFileIsDirectory() throws IOException {
        String directoryPath = "testDirectory";
        File directory = new File(directoryPath);
        directory.mkdir();

        storage = new CredentialStorage(directoryPath);
        assertTrue(storage.getAllCredentials().isEmpty());

        // Cleanup
        directory.delete();
    }


}