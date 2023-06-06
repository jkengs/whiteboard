package server;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    // Server Info
    private ServerSocket serverSocket;
    private int portNo;
    private ServerController serverController;

    // Server Status
    private boolean hasValidPort;
    private boolean isRunning;

    // Argument Indexes
    private final int PORT_NO_INDEX = 0;
    private final int USERNAME_INDEX = 1;

    // Validity Constraints
    private final int PORT_NO_LOWER_LIMIT = 1024;
    private final int PORT_NO_UPPER_LIMIT = 65335;
    private final int REQUIRED_ARGS = 2;

    // Error Messages
    private final String INVALID_PORT_NO = "Invalid port number entered. " +
            "Please input a port number between 1024 and 65335.";
    private final String INVALID_ARG_NO = "Insufficient arguments entered.\n" +
            "Usage: java -jar ServerWhiteBoard.jar <port> <username>";
    private final String ERROR_CLOSE_SOCKET = "Unable to close the server socket.";
    private final String ERROR_BIND = "Server port number is in use, please try another.";
    private final String ERROR_CREATE_SOCKET = "Unable to create a server socket.";

    // Status Messages
    private final String PROGRAM_RUNNING = "Server is running...";
    private final String ARG_VERIFY = "Verifying arguments...";
    private final String SOCKET_CLOSING = "Server socket is closing...";
    private final String SERVER_LISTENING = "Server listening for connections on port ";
    private final String SERVER_TERMINATING = "Server application is terminating...";

    /**
     * Entry point to Whiteboard Server
     * @param args args
     */
    public static void main(String[] args) {
        Server server = new Server();
        server.runProgram(args);
    }

    /**
     * Starts the program
     * @param args args
     */
    private void runProgram(String[] args) {
        isRunning = true;
        serverSocket = null;
        serverController = new ServerController(this);
        scanCommandArguments(args);
        if (hasValidPort) {
            System.out.println(PROGRAM_RUNNING);
            listen();
        } else {
            exitProgram();
        }
    }

    /**
     * Scans the args provided by the admin and check if valid
     * @param args args
     */
    private void scanCommandArguments(String[] args) {
        System.out.println(ARG_VERIFY);
        if (args.length == REQUIRED_ARGS) {
            validatePort(Integer.parseInt(args[PORT_NO_INDEX]));
            serverController.createAdmin(args[USERNAME_INDEX]);
        } else {
            System.out.println(INVALID_ARG_NO);
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
            // If provided port number is a well-known port and thus invalid
            System.out.println(INVALID_PORT_NO);
            exitProgram();
        }
    }

    /**
     * Creates server socket and listens for incoming client connections
     */
    private void listen() {
        try {
            serverSocket = new ServerSocket(portNo);
            System.out.println(SERVER_LISTENING + portNo + "...");
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                // Creates a thread for every incoming client connection
                Thread t = new Thread((new ServerRequestHandler(clientSocket, serverController)));
                t.start();
            }
        } catch (BindException e) {
            System.out.println(ERROR_BIND);
            exitProgram();
        } catch (IOException e) {
            if (!serverSocket.isClosed()) {
                System.out.println(ERROR_CREATE_SOCKET);
                exitProgram();
            }
        }
    }

    /**
     * Close the server socket and exit gracefully
     */
    void exitProgram() {
        isRunning = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
                System.out.println(SOCKET_CLOSING);
            } catch (IOException e) {
                System.out.println(ERROR_CLOSE_SOCKET);
            }
        }
        System.out.println(SERVER_TERMINATING);
        System.exit(0);
    }
}