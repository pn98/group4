package group4.passwordmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import group4.passwordmanager.manager.CredentialManager;
import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.CredentialService;
import group4.passwordmanager.ui.UserInputHandler;
import group4.passwordmanager.service.ClipboardService;
import group4.passwordmanager.service.SearchService;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class PasswordManagerApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CredentialStorage storage = new CredentialStorage();
        CredentialService credentialService = new CredentialService(storage);

        System.out.println("Enter Email or Username:");
        String emailOrUsername = scanner.nextLine();

        System.out.println("Enter Password:");
        String password = scanner.nextLine();

        System.out.println("Enter Website:");
        String website = scanner.nextLine();

        // Create a Credential object and add it to storage
        Credential credential = new Credential(emailOrUsername, password, website);
        credentialService.addCredential(credential);

        // Now let's store all credentials in JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Make sure the path is accessible and has write permissions
            File file = new File("credentials.json");
            objectMapper.writeValue(file, storage.getAllCredentials());
            System.out.println("Credentials saved to " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}