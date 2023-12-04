package group4.passwordmanager.test.SpecificationBased;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AlexUserStoriesTest {

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
    void constructorShouldInitializeEmptyListIfFileDoesNotExist() {
        assertTrue(storage.getAllCredentials().isEmpty());
    }

    @Test
    void storeShouldAddNewCredential() {
        storage.store(testCredential);
        List<Credential> credentials = storage.getAllCredentials();
        assertTrue(credentials.contains(testCredential));
    }

    @Test
    void storeShouldNotDuplicateExistingCredential() {
        storage.store(testCredential);
        storage.store(testCredential);
        List<Credential> credentials = storage.getAllCredentials();
        assertEquals(1, credentials.size());
    }

    @Test
    void updateShouldModifyExistingCredential() {
        storage.store(testCredential);
        Credential updatedCredential = new Credential("testUser", "newPassword", "newWebsite");
        storage.update(updatedCredential);
        Credential retrievedCredential = storage.getAllCredentials().get(0);
        assertEquals("newPassword", retrievedCredential.getPassword());
    }

    @Test
    void getAllCredentialsShouldReturnAllStoredCredentials() {
        storage.store(testCredential);
        Credential anotherCredential = new Credential("anotherUser", "anotherPassword", "anotherWebsite");
        storage.store(anotherCredential);
        List<Credential> credentials = storage.getAllCredentials();
        assertEquals(2, credentials.size());
        assertTrue(credentials.contains(testCredential));
        assertTrue(credentials.contains(anotherCredential));
    }

    @Test
    void loadCredentialsShouldLoadFromFile() throws IOException {
        // Pre-populate the file
        CredentialStorage preStorage = new CredentialStorage(TEST_FILENAME);
        preStorage.store(testCredential);
        preStorage.saveCredentials();

        assertTrue(Files.exists(Paths.get(TEST_FILENAME))); // Verify file existence

        // Read file content directly for verification
        String fileContent = new String(Files.readAllBytes(Paths.get(TEST_FILENAME)));
        assertFalse(fileContent.isEmpty()); // Ensure file is not empty

        // Create a new instance to load from the file
        CredentialStorage postStorage = new CredentialStorage(TEST_FILENAME);
        List<Credential> credentials = postStorage.getAllCredentials();

        // Replace the direct comparison with a more detailed check
        boolean containsCredential = credentials.stream()
                .anyMatch(c -> c.getEmailOrUsername().equals(testCredential.getEmailOrUsername()) &&
                        c.getPassword().equals(testCredential.getPassword()) &&
                        c.getWebsite().equals(testCredential.getWebsite()));
        assertTrue(containsCredential);
    }

    @Test
    void saveCredentialsShouldWriteToFile() {
        storage.store(testCredential);
        storage.saveCredentials();
        assertTrue(new File(TEST_FILENAME).exists());
        // Further checks can include reading the file to ensure content is correct
    }
}
