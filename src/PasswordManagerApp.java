import manager.CredentialManager;
import service.ClipboardService;
import service.SearchService;
import ui.UserInputHandler;

public class PasswordManagerApp {
    public static void main(String[] args) {
        CredentialManager manager = new CredentialManager();
        UserInputHandler inputHandler = new UserInputHandler();
        ClipboardService clipboardService = new ClipboardService();
        SearchService searchService = new SearchService(manager.getCredentialStorage());


        // Main loop to interact with the user

    }


}