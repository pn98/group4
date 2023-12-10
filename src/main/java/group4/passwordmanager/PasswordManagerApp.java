package group4.passwordmanager;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.CredentialService;
import group4.passwordmanager.service.PasswordGenerator;

import java.util.List;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PasswordManagerApp {
    private static Scanner scanner;
    private static LocalDateTime lastViewedTime = null;
    private static LocalDateTime lastEditedTime = null;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        CredentialStorage storage = new CredentialStorage("credentials.json");
        CredentialService credentialService = new CredentialService(storage);

        while (true) {
            System.out.println("\nChoose an option: (search, list, create, view, edit, help, exit)");
            String option = scanner.nextLine();
            String[] parts = option.split(" ");
            String command = parts[0];

            switch (command.toLowerCase()) {
                case "search":
                    if (parts.length < 2) {
                        System.out.println("Please provide a search term (email or website).");
                    } else {
                        String searchTerm = parts[1];
                        searchCredentials(scanner, credentialService, searchTerm);
                    }
                    break;
                case "list":
                    listCredentials(credentialService);
                    break;
                case "create":
                    createCredential(scanner, credentialService);
                    break;
                case "view":
                    if (parts.length < 2) {
                        System.out.println("Please provide an index number.");
                    } else {
                        int index = Integer.parseInt(parts[1]);
                        viewCredential(credentialService, index);
                    }
                    break;
                case "edit":
                    if (parts.length < 2) {
                        System.out.println("Please provide an index number.");
                    } else {
                        int index = Integer.parseInt(parts[1]);
                        editCredential(scanner, credentialService, index);
                    }
                    break;
                case "help":
                    printHelp();
                    break;
                case "exit":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void printHelp() {
        System.out.println("Available commands:");
        System.out.println("  - search [term]: Search for credentials with the provided term (email or website).");
        System.out.println("  - list: List all stored credentials.");
        System.out.println("  - create: Create a new credential.");
        System.out.println("  - view [index]: View the details of the credential at the specified index.");
        System.out.println("  - edit [index]: Edit the credential at the specified index.");
        System.out.println("  - help: Display this help message.");
        System.out.println("  - exit: Exit the Password Manager.");
    }

    private static void listCredentials(CredentialService credentialService) {
        List<Credential> credentials = credentialService.getAllCredentials();

        credentials.sort((c1, c2) -> {
            int websiteComparison = c1.getWebsite().compareToIgnoreCase(c2.getWebsite());
            if (websiteComparison != 0) {
                return websiteComparison;
            }
            return c1.getEmailOrUsername().compareToIgnoreCase(c2.getEmailOrUsername());
        });

        String currentWebsite = null;

        for (int i = 0; i < credentials.size(); i++) {
            Credential credential = credentials.get(i);
            String website = credential.getWebsite();

            if (!website.equals(currentWebsite)) {
                System.out.println("\nWebsite: " + website);
                currentWebsite = website;
            }

            System.out.println((i + 1) + ": Email/Username: " + credential.getEmailOrUsername());

            if (i == credentials.size() - 1 || !credentials.get(i + 1).getWebsite().equals(currentWebsite)) {
                System.out.println();
            }
        }
    }


    private static void createCredential(Scanner scanner, CredentialService credentialService) {
        System.out.println("Enter Email or Username:");
        String emailOrUsername = scanner.nextLine();

        System.out.println("Enter Website:");
        String website = scanner.nextLine();

        // Check if the email/username is already in use for this website
        if (credentialService.isCredentialExists(emailOrUsername, website)) {
            System.out.println("Username/Email address already in use for this website. Please choose a different one.");
            return;
        }

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

        Credential credential = new Credential(emailOrUsername, password, website);

        credentialService.addCredential(credential);
        System.out.println("Credential added successfully.");
    }


    private static void viewCredential(CredentialService credentialService, int index) {
        index -= 1;
        Credential credential = credentialService.getCredentialByIndex(index);

        if (credential != null) {
            System.out.println("Email/Username: " + credential.getEmailOrUsername());
            System.out.println("Password: " + credential.getPassword());
            System.out.println("Website: " + credential.getWebsite());

            lastViewedTime = LocalDateTime.now();

            if (lastViewedTime != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                String formattedTimestamp = lastViewedTime.format(formatter);
                System.out.println("Last viewed: " + formattedTimestamp);
            }

            List<Credential> matchingCredentials = credentialService.getCredentialsByEmail(credential.getEmailOrUsername());

            if (matchingCredentials.size() > 1) {
                System.out.println("User has other accounts with the same email:");

                for (int i = 0; i < matchingCredentials.size(); i++) {
                    if (i != index) {
                        Credential otherCredential = matchingCredentials.get(i);
                        System.out.println((i + 1) + ": Website: " + otherCredential.getWebsite());
                    }
                }

                boolean chooseAnother = true;
                while (chooseAnother) {
                    System.out.println("Would you like to view another account? (Enter 'yes' or 'no')");
                    String response = scanner.nextLine();

                    if (response.equalsIgnoreCase("yes")) {
                        System.out.println("Enter the number of the account to view:");
                        int otherIndex = Integer.parseInt(scanner.nextLine());

                        index = otherIndex - 1;

                        Credential chosenCredential = credentialService.getCredentialByIndex(index);
                        System.out.println("Email/Username: " + chosenCredential.getEmailOrUsername());
                        System.out.println("Password: " + chosenCredential.getPassword());
                        System.out.println("Website: " + chosenCredential.getWebsite());

                        System.out.println("Enter 'edit' to modify this credential or 'back' to go back:");
                        String editOption = scanner.nextLine();

                        if (editOption.equalsIgnoreCase("edit")) {
                            editCredential(scanner, credentialService, index);
                        }

                        System.out.println("Would you like to view another account? (Enter 'yes' or 'no')");
                        response = scanner.nextLine();
                        chooseAnother = response.equalsIgnoreCase("yes");
                    } else {
                        chooseAnother = false;
                    }
                }
            } else {
                System.out.println("Enter 'edit' to modify this credential or 'back' to go back:");
                String editOption = scanner.nextLine();

                if (editOption.equalsIgnoreCase("edit")) {
                    editCredential(scanner, credentialService, index);
                }
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

            lastEditedTime = LocalDateTime.now();

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

                if (!newPassword.equals(credential.getPassword())) {
                    credential.setLastModified(LocalDateTime.now());
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

    private static void searchCredentials(Scanner scanner, CredentialService credentialService, String searchTerm) {
        while (true) {
            List<Credential> credentials = credentialService.searchCredentials(searchTerm.trim());

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
                        viewCredential(credentialService, credentialService.getIndexByCredential(credentials.get(index - 1)));
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


