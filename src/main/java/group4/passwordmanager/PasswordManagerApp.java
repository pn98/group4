package group4.passwordmanager;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;
import group4.passwordmanager.service.CredentialService;
import group4.passwordmanager.service.PasswordGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class PasswordManagerApp {
    public static Scanner scanner;
    private static LocalDateTime lastViewedTime = null;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        CredentialStorage storage = new CredentialStorage("credentials.json");
        CredentialService credentialService = new CredentialService(storage);

        while (true) {
            System.out.println("\nChoose an option: (search, list, create, view, edit, group, help, exit)");
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
                case "group":
                    groupCredentials(credentialService);
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


    private static void listCredentials(CredentialService credentialService) {
        System.out.println("Choose how to list credentials: (1) By Website ID, (2) By Email/Username");
        String listOption = scanner.nextLine();

        List<Credential> credentials;

        switch (listOption) {
            case "1":
                credentials = credentialService.getAllCredentials();
                credentials.sort(Comparator.comparing(Credential::getWebsite, String.CASE_INSENSITIVE_ORDER));
                break;
            case "2":
                System.out.println("Enter Email/Username:");
                String emailOrUsername = scanner.nextLine();
                credentials = credentialService.getCredentialsByEmail(emailOrUsername);
                break;
            default:
                System.out.println("Invalid option. Listing all credentials by default.");
                credentials = credentialService.getAllCredentials();
                break;
        }

        for (int i = 0; i < credentials.size(); i++) {
            Credential credential = credentials.get(i);
            System.out.println((i + 1) + ": Email/Username: " + credential.getEmailOrUsername() +
                    ", Website: " + credential.getWebsite());
        }
    }


    private static void groupCredentials(CredentialService credentialService) {
        System.out.println("Choose how to display credentials: (1) Group by Website, (2) Group by Email/Username");
        String displayOption = scanner.nextLine();

        List<Credential> credentials = credentialService.getAllCredentials();

        credentials.sort((c1, c2) -> {
            if ("1".equals(displayOption)) {
                return c1.getWebsite().compareToIgnoreCase(c2.getWebsite());
            } else if ("2".equals(displayOption)) {
                return c1.getEmailOrUsername().compareToIgnoreCase(c2.getEmailOrUsername());
            }
            return 0;
        });

        String currentGroup = null;

        for (int i = 0; i < credentials.size(); i++) {
            Credential credential = credentials.get(i);

            String groupValue;
            if ("1".equals(displayOption)) {
                groupValue = credential.getWebsite();
            } else {
                groupValue = credential.getEmailOrUsername();
            }

            if (!groupValue.equals(currentGroup)) {
                System.out.println("\nGroup: " + groupValue);
                currentGroup = groupValue;
            }

            System.out.println((i + 1) + ": Email/Username: " + credential.getEmailOrUsername() +
                    ", Website: " + credential.getWebsite());
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

    private static void createCredential(Scanner scanner, CredentialService credentialService) {
        System.out.println("Enter Email or Username:");
        String emailOrUsername = scanner.nextLine();

        System.out.println("Enter Website:");
        String website = scanner.nextLine();

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

            if (isWeakPassword(password)) {
                System.out.println("The entered password is weak. We recommend using a combination of letters, numbers, and symbols.");

                String variant1 = password + "123";
                String variant2 = "!" + password + "@";
                String variant3 = password.toUpperCase();

                System.out.println("Here are three password variants for you to consider:");
                System.out.println("1. " + variant1);
                System.out.println("2. " + variant2);
                System.out.println("3. " + variant3);

                System.out.println("Please choose one of the variants (1, 2, 3) or enter your own password:");

                String variantChoice = scanner.nextLine();
                switch (variantChoice) {
                    case "1":
                        password = variant1;
                        break;
                    case "2":
                        password = variant2;
                        break;
                    case "3":
                        password = variant3;
                        break;
                    default:
                        System.out.println("Invalid choice. Using the entered password.");
                        break;
                }
            }
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

    private static boolean isWeakPassword(String password) {
        return password.matches("[a-zA-Z]+");
    }

    private static void viewCredential(CredentialService credentialService, int index) {
        index -= 1;
        Credential credential = credentialService.getCredentialByIndex(index);

        if (credential != null) {
            System.out.println("Email/Username: " + credential.getEmailOrUsername());
            System.out.println("Password: " + credential.getPassword());
            System.out.println("Website: " + credential.getWebsite());

            lastViewedTime = LocalDateTime.now();

            System.out.println("Last viewed: " + PasswordManagerApp.formatTimestamp(lastViewedTime));

            credentialService.updateLastModified(index);

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

            System.out.println("Do you want to change the password? (1) Yes, (2) No");
            String changePasswordOption = scanner.nextLine();

            String newPassword;
            if ("1".equals(changePasswordOption)) {
                newPassword = changePassword(scanner);
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

    private static String changePassword(Scanner scanner) {
        System.out.println("Choose an option for password: (1) Enter your new password, (2) Generate a new random password");
        String newPasswordOption = scanner.nextLine();

        String newPassword;
        if ("1".equals(newPasswordOption)) {
            System.out.println("Enter New Password:");
            newPassword = scanner.nextLine();
        } else if ("2".equals(newPasswordOption)) {
            newPassword = PasswordGenerator.generateRandomPassword();
            System.out.println("Generated Password: " + newPassword);
        } else {
            System.out.println("Invalid option. Keeping the current password.");
            newPassword = "";
        }

        return newPassword;
    }

    private static String formatTimestamp(LocalDateTime timestamp) {
        return timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
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


