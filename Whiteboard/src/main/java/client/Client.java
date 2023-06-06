package client;
import gui.ClientGUI;

public class Client {

    private ClientMessageHandler clientMessageHandler;
    private ClientGUI gui;
    private String username;
    private boolean hasValidPort;

    // Server Information
    private int portNo;
    private String hostAddress;

    // Validity Constraints
    private final int ADDRESS_INDEX = 0;
    private final int PORT_NO_INDEX = 1;
    private final int USERNAME_INDEX = 2;
    private final int PORT_NO_LOWER_LIMIT = 1024;
    private final int PORT_NO_UPPER_LIMIT = 65335;
    private final int REQUIRED_ARGS = 3;

    // Error Messages
    private final String REQUIRED_ARGS_ERROR = "Warning: Insufficient arguments entered. \n" +
            "Usage: java -jar ClientWhiteboard.jar <server-address> <server-port> <username>";
    private final String INVALID_PORT_NO = "Warning: Invalid port number entered. Please input a port number between " +
            "1024 and 65335.";

    // Status Messages
    private final String APP_RUNNING = "Application is launching...";
    private final String ARG_VERIFY = "Verifying arguments...";
    private final String APP_TERMINATING = "Application is terminating...";

    /**
     * Entry point to Whiteboard Client
     * @param args args
     */
    public static void main(String[] args) {
        Client client = new Client();
        client.runProgram(args);
    }

    /**
     * Starts the program
     * @param args args
     */
    private void runProgram(String[] args) {
        hasValidPort = false;
        scanCommandArguments(args);
        if (hasValidPort) {
            System.out.println(APP_RUNNING);
            initializeMessageHandler();
            initializeGUI();
            clientMessageHandler.run();
        }
    }

    /**
     * Scans the args provided by client and check if valid
     * @param args args
     */
    private void scanCommandArguments(String[] args) {
        System.out.println(ARG_VERIFY);
        if (args.length == REQUIRED_ARGS) {
            validatePort(Integer.parseInt(args[PORT_NO_INDEX]));
            hostAddress = args[ADDRESS_INDEX];
            username = args[USERNAME_INDEX];
        } else {
            System.out.println(REQUIRED_ARGS_ERROR);
            exitProgram();
        }
    }

    /**
     * Check if port number is within valid range
     * @param portNo port number
     */
    private void validatePort(int portNo) {
        if (portNo >= PORT_NO_LOWER_LIMIT && portNo <= PORT_NO_UPPER_LIMIT) {
            hasValidPort = true;
            this.portNo = portNo;
        } else {
            // If provided port number is ia well-known port and thus invalid
            System.out.println(INVALID_PORT_NO);
            exitProgram();
        }
    }

    /**
     * Creates a message handler that handles communication to server
     */
    private void initializeMessageHandler() {
        this.clientMessageHandler = new ClientMessageHandler(hostAddress, portNo, username, this);
    }

    /**
     * Initializes client GUI
     */
    private void initializeGUI() {
        this.gui = new ClientGUI(this, clientMessageHandler);
        gui.initialize();
    }

    /**
     * Returns GUI
     * @return GUI
     */
    ClientGUI getGUI() {
        return gui;
    }


    /**
     * Disconnects from socket and exit gracefully
     */
    public void exitProgram() {
        System.out.println(APP_TERMINATING);
        if (clientMessageHandler != null) {
            clientMessageHandler.disconnect();
        }
        System.exit(0);
    }
}
