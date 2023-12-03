package group4.passwordmanager;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.CredentialService;
import java.util.Scanner;
import java.util.List;

public class PasswordManagerApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CredentialStorage storage = new CredentialStorage("credentials.json");
        CredentialService credentialService = new CredentialService(storage);

        while (true) {
            System.out.println("\nChoose an option: (list, create, view, edit, exit)");
            String option = scanner.nextLine();
            String[] parts = option.split(" ");
            String command = parts[0];

            switch (command.toLowerCase()) {
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

        System.out.println("Enter Password:");
        String password = scanner.nextLine();

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

            System.out.println("Enter new Password (leave blank to keep current):");
            String newPassword = scanner.nextLine();

            System.out.println("Enter new Website (leave blank to keep current):");
            String newWebsite = scanner.nextLine();

            credentialService.editCredential(index, newEmailOrUsername, newPassword, newWebsite);
            System.out.println("Credential updated successfully.");
        } else {
            System.out.println("Invalid index.");
        }
    }

}
