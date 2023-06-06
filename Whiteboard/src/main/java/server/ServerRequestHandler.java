package server;
import io.ImageHandlerException;
import io.JSONHandler;
import java.io.*;
import java.net.Socket;
import io.JSONHandlerException;
import io.MessageProtocol;

public class ServerRequestHandler implements Runnable {

    private Socket clientSocket;
    private ServerController serverController;
    private JSONHandler jsonHandler;
    private boolean isPermitted;
    private String username;

    private DataInputStream in;
    private DataOutputStream out;

    private final String ERROR_STREAM = "Unable to maintain input/output streams from/to client socket.";
    private final String ERROR_OUTGOING = "Unable to deliver out outgoing message.";

    /**
     * ServerRequestHandler constructor
     * @param clientSocket client socket
     * @param serverController server controller
     */
    public ServerRequestHandler(Socket clientSocket, ServerController serverController) {
        this.clientSocket = clientSocket;
        this.serverController = serverController;
        this.jsonHandler = new JSONHandler();
        isPermitted = false;
    }

    /**
     * Handles the client's initial join request and subsequent incoming messages if they are permitted to join
     */
    public void run() {
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            String joinRequest = in.readUTF();    // Request from client to join whiteboard
            process(joinRequest);
        } catch (IOException e) {
            System.out.println(ERROR_STREAM);
        } catch (JSONHandlerException | ImageHandlerException e) {
            System.out.println(e.getMessage());
        }

        // Thread to receive incoming messages from client
        Thread inputStream = new Thread(() -> {
            while (isPermitted) {
                // If user is given permission from the admin to use the whiteboard
                try {
                    String input = in.readUTF();
                    process(input);
                } catch (IOException e) {
                    // Client disconnects
                    isPermitted = false;
                    serverController.removeUser(username);
                    break;
                } catch (JSONHandlerException | ImageHandlerException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        inputStream.start();
    }

    /**
     * Process and handle incoming messages from the client
     * @param input client incoming message
     * @throws JSONHandlerException JSON processing error
     */
    private void process(String input) throws JSONHandlerException, ImageHandlerException {
        String type = jsonHandler.processString(input, MessageProtocol.TYPE);
        switch (type) {

            case MessageProtocol.JOIN:

                // Join request
                String username = jsonHandler.processString(input, type);
                if (serverController.isValidUsername(username)) {
                    // If username has not been taken
                    serverController.joinRequest(username, this);
                } else {
                    invalidRequest();
                }
                break;

            case MessageProtocol.CANVAS:

                // Canvas changes
                serverController.updateServer(input);
                break;

            case MessageProtocol.REFRESH:

                // Canvas refresh
                String state = jsonHandler.createJSONString(MessageProtocol.TYPE, MessageProtocol.STATE,
                        serverController.getState());
                sendMessage(state);
                break;

            default:
                break;
        }
    }

    /**
     * Send a message to client
     * @param message message
     */
    void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            System.out.println(ERROR_OUTGOING);
        }
    }

    /**
     * Sends the client the current board state upon accepting their request
     * @param username
     * @param state
     */
    void acceptRequest(String username, String state) {
        this.username = username;
        String response = jsonHandler.createJSONString(MessageProtocol.TYPE, MessageProtocol.STATE,
                state);
        sendMessage(response);
        isPermitted = true;     // Permits client to make changes to session's canvas
    }

    /**
     * Sends a response to inform client that their request was rejected
     */
    void rejectRequest() {
        String response = jsonHandler.createJSONString(MessageProtocol.TYPE, MessageProtocol.REJECT,
                MessageProtocol.REJECT);
        sendMessage(response);
    }

    /**
     * Sends a response to inform client to try with another username
     */
    private void invalidRequest() {
        String response = jsonHandler.createJSONString(MessageProtocol.TYPE, MessageProtocol.INVALID,
                MessageProtocol.INVALID);
        sendMessage(response);
    }
}