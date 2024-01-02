package group4.passwordmanager.service;

import group4.passwordmanager.model.Credential;
import group4.passwordmanager.model.CredentialStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CredentialService {

    private final CredentialStorage storage;
    private static LocalDateTime lastViewedTime;

    public CredentialService(CredentialStorage storage) {
        this.storage = storage;
    }

    public static void setLastViewedTime(LocalDateTime lastViewedTime) {
        CredentialService.lastViewedTime = lastViewedTime;
    }

    public List<Credential> getAllCredentials() {
        return storage.getAllCredentials();
    }

    public Credential getCredentialByIndex(int index) {
        List<Credential> credentials = getAllCredentials();
        if (index >= 0 && index < credentials.size()) {
            return credentials.get(index);
        }
        return null;
    }

    public void addCredential(Credential credential) {
        List<Credential> credentials = getAllCredentials();

        boolean emailExists = credentials.stream()
                .anyMatch(c -> c.getEmailOrUsername().equalsIgnoreCase(credential.getEmailOrUsername()));

        if (emailExists) {
            System.out.println("A credential with the same email already exists. Do you want to add another account with the same email? (Enter 'yes' or 'no')");
            Scanner scanner = new Scanner(System.in);
            String response = scanner.nextLine();

            if (response.equalsIgnoreCase("yes")) {
                storage.store(credential);
                System.out.println("Credential added successfully.");
            } else {
                System.out.println("Account not added.");
            }
        } else {
            storage.store(credential);
            System.out.println("Credential added successfully.");
        }
    }

    public void editCredential(int index, String newEmailOrUsername, String newPassword, String newWebsite) {
        List<Credential> credentials = storage.getAllCredentials();
        if (index >= 0 && index < credentials.size()) {
            Credential credential = credentials.get(index);

            if (!newEmailOrUsername.isEmpty()) {
                credential.setEmailOrUsername(newEmailOrUsername);
            }
            if (!newPassword.isEmpty()) {
                credential.setPassword(newPassword);
            }
            if (!newWebsite.isEmpty()) {
                credential.setWebsite(newWebsite);
            }

            credential.setLastModified(LocalDateTime.now());
            storage.update(credential);
            LastModifiedService.updateLastModified(credential);
        }
    }

    public void updateCredential(Credential credential) {
        credential.setLastModified(LocalDateTime.now());
        storage.update(credential);
        LastModifiedService.updateLastModified(credential);
    }

    public List<Credential> searchCredentials(String searchTerm) {
        List<Credential> matchingCredentials = new ArrayList<>();
        List<Credential> credentials = getAllCredentials();

        for (Credential credential : credentials) {
            if (credential.getEmailOrUsername().contains(searchTerm) || credential.getWebsite().contains(searchTerm)) {
                matchingCredentials.add(credential);
            }
        }

        return matchingCredentials;
    }

    public int getIndexByCredential(Credential credential) {
        List<Credential> credentials = getAllCredentials();
        for (int i = 0; i < credentials.size(); i++) {
            if (credentials.get(i).equals(credential)) {
                return i;
            }
        }
        return -1;
    }

    public List<Credential> getCredentialsByEmail(String emailOrUsername) {
        List<Credential> matchingCredentials = new ArrayList<>();
        List<Credential> credentials = getAllCredentials();

        for (Credential credential : credentials) {
            if (credential.getEmailOrUsername().equalsIgnoreCase(emailOrUsername)) {
                matchingCredentials.add(credential);
            }
        }

        return matchingCredentials;
    }

    public boolean isCredentialExists(String emailOrUsername, String website) {
        List<Credential> credentials = getAllCredentials();

        return credentials.stream()
                .anyMatch(c -> c.getEmailOrUsername().equalsIgnoreCase(emailOrUsername) && c.getWebsite().equalsIgnoreCase(website));
    }

    public void updateLastModified(int index) {
        List<Credential> credentials = storage.getAllCredentials();
        if (index >= 0 && index < credentials.size()) {
            Credential credential = credentials.get(index);
            credential.setLastModified(LocalDateTime.now());
            storage.update(credential);
            LastModifiedService.updateLastModified(credential);
        }
    }

    private static void viewCredential(CredentialService credentialService, int index) {
        index -= 1;
        Credential credential = credentialService.getCredentialByIndex(index);

        if (credential != null) {
            System.out.println("Email/Username: " + credential.getEmailOrUsername());
            System.out.println("Password: " + credential.getPassword());
            System.out.println("Website: " + credential.getWebsite());

            lastViewedTime = LocalDateTime.now();
            LastModifiedService.updateLastViewed();

            System.out.println("Last viewed: " + LastModifiedService.formatTimestamp(lastViewedTime));

            credentialService.updateLastModified(index);

            List<Credential> matchingCredentials = credentialService.getCredentialsByEmail(credential.getEmailOrUsername());

            Scanner scanner = null;
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
                    return;
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
