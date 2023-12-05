package group4.passwordmanager;

import group4.passwordmanager.manager.TagManager;
import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.AccessHistoryTracker;
import group4.passwordmanager.service.CredentialService;
import group4.passwordmanager.service.PasswordGenerator;
import group4.passwordmanager.service.SearchService;

import java.util.Scanner;
import java.util.List;

public class PasswordManagerApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CredentialStorage storage = new CredentialStorage("credentials.json");
        CredentialService credentialService = new CredentialService(storage);
        AccessHistoryTracker historyTracker = new AccessHistoryTracker(credentialService);
        SearchService searchService = new SearchService(credentialService);
        TagManager tagManager = new TagManager(storage);

        while (true) {
            System.out.println("\nChoose an option: (search, list, create, view, edit, exit)");
            String option = scanner.nextLine();
            String[] parts = option.split(" ");
            String command = parts[0];

            switch (command.toLowerCase()) {
                case "search":
                    if (parts.length < 2) {
                        System.out.println("Please provide a search term (email or website or tag).");
                    } else {
                        String searchTerm = parts[1];
                        searchCredentials(scanner, searchService, searchTerm);
                    }
                    break;
                case "list":
                    listCredentials(credentialService);
                    break;
                case "create":
                    createCredential(scanner, credentialService, tagManager);
                    break;
                case "view":
                case "edit":
                    if (parts.length < 2) {
                        System.out.println("Please provide an index number.");
                    } else {
                        int index = Integer.parseInt(parts[1]);
                        if (command.equals("view")) {
                            viewCredential(credentialService, index, historyTracker);
                        } else {
                            editCredential(scanner, credentialService, tagManager, index);
                        }
                    }
                    break;
                case "exit":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void listCredentials(CredentialService credentialService) {
        List<Credential> credentials = credentialService.getAllCredentials();
        for (int i = 0; i < credentials.size(); i++) {
            Credential credential = credentials.get(i);
            System.out.println((i + 1) + ": Email/Username: " + credential.getEmailOrUsername() + ", Website: " + credential.getWebsite() + ", Tags " + credential.getTags());
        }
    }

    private static void createCredential(Scanner scanner, CredentialService credentialService, TagManager tagManager) {
        System.out.println("Enter Email or Username:");
        String emailOrUsername = scanner.nextLine();

        // Call PasswordGenerator to enter password
        String password = PasswordGenerator.enterPassword(scanner);

        System.out.println("Enter Website:");
        String website = scanner.nextLine();

        // Call TagManager to enter tags
        List<String> tags = tagManager.enterTags(scanner);

        Credential credential = new Credential(emailOrUsername, password, website);
        credential.setTags(tags);
        credentialService.addCredential(credential);

        System.out.println("Credential added successfully.");
    }

    private static void viewCredential(CredentialService credentialService, int index, AccessHistoryTracker historyTracker) {
        index -= 1;
        Credential credential = credentialService.getCredentialByIndex(index);
        if (credential != null) {
            // Display all details of the credential
            System.out.println("Email/Username: " + credential.getEmailOrUsername());
            System.out.println("Password: " + credential.getPassword());
            System.out.println("Website: " + credential.getWebsite());
            historyTracker.trackAccessHistory(credential);
            System.out.println("Tags: " + credential.getTags());

        } else {
            System.out.println("Invalid index.");
        }
    }

    private static void editCredential(Scanner scanner, CredentialService credentialService, TagManager tagManager, int index) {
        index -= 1;
        Credential credential = credentialService.getCredentialByIndex(index);
        if (credential != null) {
            System.out.println("Editing Credential: " + credential.getEmailOrUsername());
            System.out.println("Current Password: " + credential.getPassword());
            System.out.println("Current Website: " + credential.getWebsite());
            System.out.println("Current Tags: " + credential.getTags());

            System.out.println("Enter new Email/Username (leave blank to keep current):");
            String newEmailOrUsername = scanner.nextLine();

            String newPassword = PasswordGenerator.editPassword(scanner, credential.getPassword());

            System.out.println("Enter new Website (leave blank to keep current):");
            String newWebsite = scanner.nextLine();

            List<String> newTags = tagManager.editTags(scanner, credential.getTags());

            credentialService.editCredential(index, newEmailOrUsername, newPassword, newWebsite);
            System.out.println("Credential updated successfully.");

        } else {
            System.out.println("Invalid index.");
        }
    }

    private static void searchCredentials(Scanner scanner, SearchService searchService, String searchTerm) {
        searchService.searchCredentialsAndPrintDetails(scanner, searchTerm);
    }
}