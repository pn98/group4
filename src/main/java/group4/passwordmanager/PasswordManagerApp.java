package group4.passwordmanager;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.CredentialService;
import group4.passwordmanager.service.PasswordGenerator;
import group4.passwordmanager.service.SearchService;

import static group4.passwordmanager.service.SearchService.viewPasswordOnly;

import java.util.Scanner;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PasswordManagerApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CredentialStorage storage = new CredentialStorage("credentials.json");
        CredentialService credentialService = new CredentialService(storage);
        SearchService searchService = new SearchService(credentialService);

        while (true) {
            System.out.println("\nChoose an option: (search, list, create, view, edit, exit)");
            String option = scanner.nextLine();
            String[] parts = option.split(" ");
            String command = parts[0];

            switch (command.toLowerCase()) {
                case "search":
                    if (parts.length < 2) {
                        System.out.println("Please provide a search term (email or website).");
                    } else {
                        String searchTerm = parts[1];
                        searchCredentials(scanner, searchService, searchTerm);
                    }
                    break;
                case "list":
                    listCredentials(credentialService);
                    break;
                case "create":
                    createCredential(scanner, credentialService);
                    break;
                case "view":
                case "edit":
                    if (parts.length < 2) {
                        System.out.println("Please provide an index number.");
                    } else {
                        int index = Integer.parseInt(parts[1]);
                        if (command.equals("view")) {
                            viewCredential(credentialService, index);
                        } else {
                            editCredential(scanner, credentialService, index);
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
            System.out.println((i + 1) + ": Email/Username: " + credential.getEmailOrUsername() + ", Website: " + credential.getWebsite());
        }
    }

    private static void createCredential(Scanner scanner, CredentialService credentialService) {
        System.out.println("Enter Email or Username:");
        String emailOrUsername = scanner.nextLine();

        System.out.println("Choose an option for password: (1) Enter your own password, (2) Generate a random password");
        String passwordOption = scanner.nextLine();

        String password;
        if ("1".equals(passwordOption)) {
            System.out.println("Enter Password:");
            password = scanner.nextLine();
        } else if ("2".equals(passwordOption)) {
            password = PasswordGenerator.generateRandomPassword();
            System.out.println("Generated Password: " + password);
        } else {
            System.out.println("Invalid option. Defaulting to your own password.");
            System.out.println("Enter Password:");
            password = scanner.nextLine();
        }

        System.out.println("Enter Website:");
        String website = scanner.nextLine();

        Credential credential = new Credential(emailOrUsername, password, website);
        credentialService.addCredential(credential);

        System.out.println("Credential added successfully.");
    }

    private static void viewCredential(CredentialService credentialService, int index) {
        index -= 1;
        Credential credential = credentialService.getCredentialByIndex(index);
        if (credential != null) {
            // Display all details of the credential
            System.out.println("Email/Username: " + credential.getEmailOrUsername());
            System.out.println("Password: " + credential.getPassword());
            System.out.println("Website: " + credential.getWebsite());

            LocalDateTime currentTimestamp = LocalDateTime.now();
            LocalDateTime lastAccessed = credential.getLastAccessed();

            if (lastAccessed == null) {
                System.out.println("Last accessed: This is the first time it is accessed");
                // Set the lastAccessed timestamp only if it's not already set
            } else {
                // Display the lastAccessed timestamp
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                String formattedTimestamp = lastAccessed.format(formatter);
                System.out.println("Last accessed: " + formattedTimestamp);

                // Update the lastAccessed timestamp to the current time
                credential.setLastAccessed(currentTimestamp);
                credentialService.updateCredential(credential);
            }

        } else {
            System.out.println("Invalid index.");
        }
    }

    private static void editCredential(Scanner scanner, CredentialService credentialService, int index) {
        index -= 1;
        Credential credential = credentialService.getCredentialByIndex(index);
        if (credential != null) {
            System.out.println("Editing Credential: " + credential.getEmailOrUsername());
            System.out.println("Current Password: " + credential.getPassword());
            System.out.println("Current Website: " + credential.getWebsite());

            System.out.println("Enter new Email/Username (leave blank to keep current):");
            String newEmailOrUsername = scanner.nextLine();

            System.out.println("Do you want to change the password? (1) Yes, (2) No");
            String changePasswordOption = scanner.nextLine();

            String newPassword;
            if ("1".equals(changePasswordOption)) {
                System.out.println("Choose an option for password: (1) Enter your new password, (2) Generate a new random password");
                String newPasswordOption = scanner.nextLine();

                if ("1".equals(newPasswordOption)) {
                    System.out.println("Enter New Password:");
                    newPassword = scanner.nextLine();
                } else if ("2".equals(newPasswordOption)) {
                    newPassword = PasswordGenerator.generateRandomPassword();
                    System.out.println("Generated Password: " + newPassword);
                } else {
                    System.out.println("Invalid option. Keeping the current password.");
                    newPassword = credential.getPassword();
                }
            } else {
                newPassword = credential.getPassword();
            }

            System.out.println("Enter new Website (leave blank to keep current):");
            String newWebsite = scanner.nextLine();

            credentialService.editCredential(index, newEmailOrUsername, newPassword, newWebsite);
            System.out.println("Credential updated successfully.");

        } else {
            System.out.println("Invalid index.");
        }
    }

private static void searchCredentials(Scanner scanner, SearchService searchService, String searchTerm) {
    while (true) {
        List<Credential> credentials = searchService.searchCredentials(searchTerm.trim());

        if (credentials.isEmpty()) {
            System.out.println("No matching credentials found.");
            return;
        } else {
            System.out.println("Matching credentials:");

            for (int i = 0; i < credentials.size(); i++) {
                Credential credential = credentials.get(i);
                System.out.println((i + 1) + ": Email/Username: " + credential.getEmailOrUsername() + ", Website: " + credential.getWebsite());
            }

            System.out.println("Enter the number of the credential to view its details or 'back' to go back:");
            String selection = scanner.nextLine();

            if (selection.equalsIgnoreCase("back")) {
                return;  // Go back to choosing an option
            }

            try {
                int index = Integer.parseInt(selection);
                if (index >= 1 && index <= credentials.size()) {
                    viewPasswordOnly(credentials.get(index - 1));
                } else {
                    System.out.println("Invalid selection.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid selection.");
            }
        }
    }
}



}
