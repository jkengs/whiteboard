package server;
import gui.AdminGUI;
import io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ServerController {

    private Server server;
    private AdminGUI adminGUI;
    private ArrayList<User> userList;   // List of all connected users
    private JSONHandler jsonHandler;

    private final String ERROR_CLEAR = "Unable to clear the session's canvas";

    /**
     * ServerController constructor
     * @param server server
     */
    public ServerController(Server server) {
        this.server = server;
        userList = new ArrayList<>();
        jsonHandler =  new JSONHandler();
    }

    /**
     * Adds the admin to the user list and starts the admin GUI
     * @param username admin username
     */
    public void createAdmin(String username) {
        User user = new User(username);
        user.setAdmin();
        userList.add(user);
        initializeGUI();
    }

    /**
     * Starts the admin GUI
     */
    private void initializeGUI() {
        adminGUI = new AdminGUI(this);
        adminGUI.initialize();
    }

    /**
     * Display a new user join request to the admin GUI and sends the response back to the
     * client via their request handler thread
     * @param username client username
     * @param serverRequestHandler serverRequestHandler
     */
    void joinRequest(String username, ServerRequestHandler serverRequestHandler) {
        boolean isAllowed = adminGUI.displayJoinRequest(username);
        if (isAllowed && isValidUsername(username)) {
            // Add client to user list
            addUser(username, serverRequestHandler);
            try {
                serverRequestHandler.acceptRequest(username, getState());
            } catch (ImageHandlerException e) {
                System.out.println(e.getMessage());
            }
        } else {
            serverRequestHandler.rejectRequest();
        }
    }

    /**
     * Adds the client (as a user) to the user list and updates connected users
     * @param username client username
     * @param serverRequestHandler serverRequestHandler
     */
    public void addUser(String username, ServerRequestHandler serverRequestHandler) {
        userList.add(new User(username, serverRequestHandler));
        refreshUserList();
    }

    /**
     * Removes the client from the user list and updates connected users
     * @param username client username
     */
    public void removeUser(String username) {
        userList.removeIf(user -> user.getUsername().equals(username) && !user.isAdmin());
        refreshUserList();
    }

    /**
     * Returns the encoded string of the current canvas state
     * @return image string
     * @throws ImageHandlerException error encoding the image
     */
    String getState() throws ImageHandlerException {
        BufferedImage image = adminGUI.getCanvas().getImage();
        ImageHandler imageHandler = new ImageHandler();
        return imageHandler.getImageString(image);
    }

    /**
     * Sends canvas changes to all users
     * @param update string containing canvas changes
     */
    public void updateServer(String update) {
        for (User user: userList) {
            if (user.isAdmin()) {
                // Admin
                process(update);
            } else if (user.isPermitted()){
                // Client User
                user.getServerRequestHandler().sendMessage(update);
            }
        }
    }

    /**
     * Process canvas changes and updates the admin GUI
     * @param input string containing canvas changes
     */
    private void process(String input) {
        try {
            String type = jsonHandler.processString(input, MessageProtocol.TYPE);
            if (type.equals(MessageProtocol.CANVAS)) {
                processDrawing(input);
            }
        } catch (JSONHandlerException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Retrieves the type of drawing and its information
     * @param input string containing canvas changes
     */
    private void processDrawing(String input) {
        try {
            String drawingJSON = jsonHandler.processDrawing(input, MessageProtocol.CANVAS);
            String drawingType = jsonHandler.processString(drawingJSON, MessageProtocol.TYPE);
            updateAdminCanvas(drawingType, drawingJSON);
        } catch (JSONHandlerException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Process and apply new drawings to admin canvas
     * @param drawingType type of drawing
     * @param drawingJSON JSON string containing drawing information
     * @throws JSONHandlerException error parsing JSON string
     */
    private void updateAdminCanvas(String drawingType, String drawingJSON) throws JSONHandlerException {
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
                adminGUI.getCanvas().updateCanvas(drawingType, x1, y1, x2, y2, color);
                break;

            case MessageProtocol.TEXT_BOX:

                String text = jsonHandler.processString(drawingJSON, MessageProtocol.TEXT);
                x = jsonHandler.processInt(drawingJSON, MessageProtocol.POS_X);
                y = jsonHandler.processInt(drawingJSON, MessageProtocol.POS_Y);
                adminGUI.getCanvas().updateCanvas(drawingType, text, x, y, color);
                break;

            case MessageProtocol.ERASER:

                x = jsonHandler.processInt(drawingJSON, MessageProtocol.POS_X);
                y = jsonHandler.processInt(drawingJSON, MessageProtocol.POS_Y);
                width = jsonHandler.processInt(drawingJSON, MessageProtocol.WIDTH);
                height = jsonHandler.processInt(drawingJSON, MessageProtocol.HEIGHT);
                adminGUI.getCanvas().updateCanvas(drawingType, x, y, width, height, Color.WHITE);
                break;

            default:

                // Rectangle, Circle, Oval
                x = jsonHandler.processInt(drawingJSON, MessageProtocol.POS_X);
                y = jsonHandler.processInt(drawingJSON, MessageProtocol.POS_Y);
                width = jsonHandler.processInt(drawingJSON, MessageProtocol.WIDTH);
                height = jsonHandler.processInt(drawingJSON, MessageProtocol.HEIGHT);
                adminGUI.getCanvas().updateCanvas(drawingType, x, y, width, height, color);
                break;
        }
    }

    /**
     * Boots a specific user and sends a disconnect message to the user
     * @param username username
     */
    public void bootUser(String username) {
        for (User user: userList) {
            if (user.getUsername().equals(username)) {
                String update = jsonHandler.createJSONString(MessageProtocol.TYPE, MessageProtocol.DISCONNECT,
                        MessageProtocol.DISCONNECT);
                user.getServerRequestHandler().sendMessage(update);
            }
        }
        removeUser(username);
    }

    /**
     * Boots all users (except admin) and sends a disconnect message to the users
     */
    public void bootAll() {
        ArrayList<User> removedUsers = new ArrayList<>();
        for (User user: userList) {
            if (!user.isAdmin()) {
                String update = jsonHandler.createJSONString(MessageProtocol.TYPE, MessageProtocol.DISCONNECT,
                        MessageProtocol.DISCONNECT);
                user.getServerRequestHandler().sendMessage(update);
                removedUsers.add(user);
            }
        }
        userList.removeAll(removedUsers);
        adminGUI.updateUserList();
    }

    /**
     * Updates all users with a blank canvas (done by admin)
     */
    public void refreshState() {
        try {
            String update = jsonHandler.createJSONString(MessageProtocol.TYPE, MessageProtocol.STATE,
                    getState());
            updateServer(update);
        } catch (ImageHandlerException e) {
            System.out.println(ERROR_CLEAR);
        }
    }

    /**
     * Returns true if new user's username has not been used already
     * @param username username
     * @return boolean
     */
    boolean isValidUsername(String username) {
        for (User user: userList) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sends all users the updated user list
     */
    private void refreshUserList() {
        adminGUI.updateUserList();
        String update = jsonHandler.createJSONString(MessageProtocol.TYPE, MessageProtocol.USER_LIST,
                getUserList());
        updateServer(update);
    }

    /**
     * Returns an array list containing usernames of all connected users
     * @return username list
     */
    public ArrayList<String> getUserList() {
        ArrayList<String> usernameList = new ArrayList<>();
        for (User user: userList) {
            usernameList.add(user.getUsername());
        }
        return usernameList;
    }

    /**
     * Disconnects all users and exits the program
     */
    public void shutDown() {
        String update = jsonHandler.createJSONString(MessageProtocol.TYPE, MessageProtocol.SHUT_DOWN,
                MessageProtocol.SHUT_DOWN);
        updateServer(update);
        server.exitProgram();
    }
}