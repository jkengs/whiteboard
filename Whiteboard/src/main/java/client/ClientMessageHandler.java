package client;
import io.ImageHandlerException;
import io.JSONHandler;
import io.JSONHandlerException;
import io.MessageProtocol;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientMessageHandler {

    private Client client;
    private Socket socket;
    private String username;
    private JSONHandler jsonHandler;
    private boolean isConnected;
    private boolean isPermitted;

    // Server Information
    private String hostAddress;
    private int portNo;

    // Socket Stream IO
    private DataInputStream in;
    private DataOutputStream out;

    // Error Messages
    private final String ERROR_CLOSE_SOCKET = "Unable to close the server socket.";
    private final String ERROR_INCOMING = "Unable to receive incoming messages.";
    private final String NO_CONNECTION = "Connection to the server has been lost. The application will now exit, " +
            "please try again.";
    private final String ERROR_OUTGOING = "Unable to deliver out outgoing message.";
    private final String ERROR_REQUEST = "An error occurred when requesting to join the whiteboard!";
    private final String ERROR_STATE = "Unable to synchronize with the session's whiteboard. Please try to " +
            "refresh via the settings or rejoin the application.";
    private final String INVALID_USERNAME = "Your username has already been taken, the application will now exit. " +
            "Please try again with a different username!";
    private final String USER_REJECTED = "Your request to join the whiteboard has been rejected. The " +
            "application will now exit, please try again.";
    private final String USER_KICKED = "You have been removed from the whiteboard session. The application will now " +
            "exit.";
    private final String SERVER_CLOSE = "The server is shutting down. All users have been disconnected.";
    private final String CONNECT_FAIL = "Failed to connect to server. The application will now exit, please try again.";

    /**
     * ClientMessageHandler constructor
     * @param hostAddress host address
     * @param portNo port number
     * @param username username
     * @param client client
     */
    public ClientMessageHandler(String hostAddress, int portNo, String username, Client client) {
        this.hostAddress = hostAddress;
        this.portNo = portNo;
        this.username = username;
        this.client = client;
        this.jsonHandler = new JSONHandler();
        isConnected = false;
        isPermitted = false;
    }

    /**
     * Connect to server socket and request to join the whiteboard
     */
    void run() {
        try {
            connect();
            requestJoin();
        } catch (IOException | JSONHandlerException e) {
            client.getGUI().displayAlert(ERROR_REQUEST);
            client.exitProgram();
        }

        // Thread to receive incoming messages from server
        Thread inputStream = new Thread(() -> {
            while (isConnected) {
                try {
                    String input = in.readUTF();
                    process(input);
                } catch (IOException e) {
                    if (isConnected) {
                        // Lost connection to server
                        System.out.println(ERROR_INCOMING);
                        client.getGUI().displayAlert(NO_CONNECTION);
                        client.exitProgram();
                    }
                } catch (JSONHandlerException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        inputStream.start();
    }

    /**
     * Connects to server socket
     */
    private void connect() {
        socket = null;
        try {
            socket = new Socket(hostAddress, portNo);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            isConnected = true;
        } catch (Exception e) {
            // Unable to make initial connection to server
            client.getGUI().displayAlert(CONNECT_FAIL);
            client.exitProgram();
        }
    }

    /**
     * Sends a join request to server and handles the reply
     * @throws IOException outgoing message error
     * @throws JSONHandlerException error parsing JSON string response
     */
    private void requestJoin() throws IOException, JSONHandlerException {
        String request = jsonHandler.createJSONString(MessageProtocol.TYPE, MessageProtocol.JOIN, username);
        sendMessage(request);
        String response = in.readUTF();
        process(response);
        isPermitted = true;     // Permits user to draw on canvas
    }

    /**
     * Send a message to server
     * @param message message
     */
    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            System.out.println(ERROR_OUTGOING);
        }
    }

    /**
     * Processes a message exchange JSON string (from server)
     * @param input JSON string
     * @throws JSONHandlerException error parsing JSON string
     */
    private void process(String input) throws JSONHandlerException {
        String type = jsonHandler.processString(input, MessageProtocol.TYPE);
        switch (type) {

            case MessageProtocol.REJECT:

                // Exits program if join request is rejected
                client.getGUI().displayAlert(USER_REJECTED);
                client.exitProgram();
                break;

            case MessageProtocol.INVALID:

                // Username not valid
                client.getGUI().displayAlert(INVALID_USERNAME);
                client.exitProgram();
                break;

            case MessageProtocol.DISCONNECT:

                // Exits program if user is kicked from the server
                client.getGUI().displayAlert(USER_KICKED);
                client.exitProgram();
                break;

            case MessageProtocol.SHUT_DOWN:

                // Server is shut down by admin
                client.getGUI().displayAlert(SERVER_CLOSE);
                client.exitProgram();
                break;

            case MessageProtocol.USER_LIST:

                // User list changes
                ArrayList<String> userList = jsonHandler.processStringArray(input,type);
                client.getGUI().updateUserList(userList);
                break;

            case MessageProtocol.STATE:

                // Apply current state of whiteboard
                String image = jsonHandler.processString(input, type);
                try {
                    client.getGUI().getCanvas().setImage(image);
                } catch (ImageHandlerException e) {
                    client.getGUI().displayAlert(ERROR_STATE);
                }
                break;

            case MessageProtocol.CANVAS:

                // Update state of canvas
                String drawingJSON = jsonHandler.processDrawing(input, type);
                String drawingType = jsonHandler.processString(drawingJSON, MessageProtocol.TYPE);
                processCanvasUpdate(drawingType, drawingJSON);
                break;
        }
    }

    /**
     * Process and apply new drawings to canvas
     * @param drawingType type of drawing
     * @param drawingJSON JSON string containing drawing information
     * @throws JSONHandlerException error parsing JSON string
     */
    private void processCanvasUpdate(String drawingType, String drawingJSON) throws JSONHandlerException {
        int x;
        int y;
        int width;
        int height;
        int colorRGB = jsonHandler.processInt(drawingJSON, MessageProtocol.COLOR);
        Color color = new Color(colorRGB);
        switch (drawingType) {
            case MessageProtocol.LINE:

                int x1 = jsonHandler.processInt(drawingJSON, MessageProtocol.POS_X1);
                int y1 = jsonHandler.processInt(drawingJSON, MessageProtocol.POS_Y1);
                int x2 = jsonHandler.processInt(drawingJSON, MessageProtocol.POS_X2);
                int y2 = jsonHandler.processInt(drawingJSON, MessageProtocol.POS_Y2);
                client.getGUI().getCanvas().updateCanvas(drawingType, x1, y1, x2, y2, color);
                break;

            case MessageProtocol.TEXT_BOX:

                String text = jsonHandler.processString(drawingJSON, MessageProtocol.TEXT);
                x = jsonHandler.processInt(drawingJSON, MessageProtocol.POS_X);
                y = jsonHandler.processInt(drawingJSON, MessageProtocol.POS_Y);
                client.getGUI().getCanvas().updateCanvas(drawingType, text, x, y, color);
                break;

            case MessageProtocol.ERASER:

                x = jsonHandler.processInt(drawingJSON, MessageProtocol.POS_X);
                y = jsonHandler.processInt(drawingJSON, MessageProtocol.POS_Y);
                width = jsonHandler.processInt(drawingJSON, MessageProtocol.WIDTH);
                height = jsonHandler.processInt(drawingJSON, MessageProtocol.HEIGHT);
                client.getGUI().getCanvas().updateCanvas(drawingType, x, y, width, height, Color.WHITE);
                break;

            default:

                // Rectangle, Circle, Oval shapes
                x = jsonHandler.processInt(drawingJSON, MessageProtocol.POS_X);
                y = jsonHandler.processInt(drawingJSON, MessageProtocol.POS_Y);
                width = jsonHandler.processInt(drawingJSON, MessageProtocol.WIDTH);
                height = jsonHandler.processInt(drawingJSON, MessageProtocol.HEIGHT);
                client.getGUI().getCanvas().updateCanvas(drawingType, x, y, width, height, color);
                break;
        }
    }

    /**
     * Request whiteboard state from server for refreshing canvas
     */
    public void requestState() {
        String request = jsonHandler.createJSONString(MessageProtocol.TYPE, MessageProtocol.REFRESH, MessageProtocol.REFRESH);
        sendMessage(request);
    }

    /**
     * Close the stream socket
     */
    void disconnect() {
        isConnected = false;
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println(ERROR_CLOSE_SOCKET);
            }
        }
    }

    /**
     * Returns true if user is permitted to draw on the canvas
     * @return boolean
     */
    public boolean isPermitted() {
        return isPermitted;
    }
}