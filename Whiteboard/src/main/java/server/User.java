package server;

public class User {

    private String username;
    private ServerRequestHandler serverRequestHandler;
    private boolean isAdmin;
    private boolean isPermitted;

    /**
     * User constructor for admin
     * @param username username
     */
    public User(String username) {
        this.username = username;
    }

    /**
     * User constructor for client
     * @param username username
     * @param serverRequestHandler serverRequestHandler
     */
    public User(String username, ServerRequestHandler serverRequestHandler) {
        this.username = username;
        this.serverRequestHandler = serverRequestHandler;
        this.isAdmin = false;
        this.isPermitted = true;
    }

    /**
     * Give admin privilege to user
     */
    void setAdmin() {
        isAdmin = true;
        isPermitted = true;
    }

    /**
     * Returns serverRequestHandler
     * @return serverRequestHandler
     */
    ServerRequestHandler getServerRequestHandler() {
        return serverRequestHandler;
    }

    /**
     * Returns true if user is permitted
     * @return boolean
     */
    boolean isPermitted() {
        return isPermitted;
    }

    /**
     * Returns true if user is an admin
     * @return boolean
     */
    boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Returns username string
     * @return username
     */
    String getUsername() {
        return username;
    }
}