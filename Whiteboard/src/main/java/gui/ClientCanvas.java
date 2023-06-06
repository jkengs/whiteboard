package gui;
import client.ClientMessageHandler;
import io.ImageHandler;
import io.ImageHandlerException;
import io.JSONHandler;
import io.MessageProtocol;
import org.json.simple.JSONObject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ClientCanvas extends JPanel {

    private ClientMessageHandler clientMessageHandler;
    private JSONHandler jsonHandler;
    private String selectedTool;

    // Canvas Graphic Components
    private BufferedImage image;
    private Graphics2D g2;

    // Drawing Information
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private Color color;
    private String text;

    // Tool Types
    final String TOOL_RECTANGLE = "Rect";
    final String TOOL_CIRCLE = "Circle";
    final String TOOL_OVAL = "Oval";
    final String TOOL_LINE = "Line";
    final String TOOL_TEXT = "Text";

    public ClientCanvas(int width, int height, ClientMessageHandler clientMessageHandler) {
        super();
        this.clientMessageHandler = clientMessageHandler;
        this.jsonHandler = new JSONHandler();
        this.color = Color.BLACK;
        setPreferredSize(new Dimension(width, height));
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Start Coordinates
                x1 = e.getX();
                y1 = e.getY();
                repaint();
            }
            public void mouseReleased(MouseEvent e) {
                // End Coordinates
                x2 = e.getX();
                y2 = e.getY();
                if(toolSelected() && clientMessageHandler.isPermitted()) {
                    draw();
                }
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (image == null) {
            image = new BufferedImage(550, 440, BufferedImage.TYPE_INT_RGB);
            g2 = image.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            clear();
        }
        g.drawImage(image,0,0,null);
    }

    /**
     * Retrieves drawing information and sends to the server
     */
    private void draw() {
        int x = getPosX();
        int y = getPosY();
        int width = getShapeWidth();
        int height = getShapeHeight();
        JSONObject drawingJSON = null;
        String update;
        switch (selectedTool) {
            case TOOL_RECTANGLE:

                drawingJSON = jsonHandler.createShape(MessageProtocol.TYPE, MessageProtocol.POS_X,
                        MessageProtocol.POS_Y, MessageProtocol.WIDTH, MessageProtocol.HEIGHT, MessageProtocol.COLOR,
                        MessageProtocol.RECTANGLE, x, y, width, height, color);
                break;

            case TOOL_CIRCLE:

                int radius = getRadius();
                int diameter = getDiameter(radius);
                drawingJSON = jsonHandler.createShape(MessageProtocol.TYPE, MessageProtocol.POS_X, MessageProtocol.POS_Y,
                        MessageProtocol.WIDTH, MessageProtocol.HEIGHT, MessageProtocol.COLOR,
                        MessageProtocol.CIRCLE, x - radius, y - radius, diameter, diameter, color);

                break;

            case TOOL_OVAL:

                drawingJSON = jsonHandler.createShape(MessageProtocol.TYPE, MessageProtocol.POS_X,
                        MessageProtocol.POS_Y, MessageProtocol.WIDTH, MessageProtocol.HEIGHT, MessageProtocol.COLOR,
                        MessageProtocol.OVAL, x, y, width, height, color);
                break;

            case TOOL_LINE:

                drawingJSON = jsonHandler.createShape(MessageProtocol.TYPE, MessageProtocol.POS_X1,
                        MessageProtocol.POS_Y1, MessageProtocol.POS_X2, MessageProtocol.POS_Y2, MessageProtocol.COLOR,
                        MessageProtocol.LINE, x1, y1, x2, y2, color);
                break;

            case TOOL_TEXT:

                drawingJSON = jsonHandler.createText(
                        MessageProtocol.TYPE, MessageProtocol.TEXT,MessageProtocol.POS_X, MessageProtocol.POS_Y,
                        MessageProtocol.COLOR,
                        MessageProtocol.TEXT_BOX, text, x, y, color);
                break;

            default:
                break;
        }
        update = jsonHandler.createJSONString(MessageProtocol.TYPE,
                MessageProtocol.CANVAS, drawingJSON);
        clientMessageHandler.sendMessage(update);
    }

    /**
     * Draws shapes received from the server
     * @param drawingType type of drawing
     * @param x x
     * @param y y
     * @param width width
     * @param height height
     * @param color color
     */
    public void updateCanvas(String drawingType, int x, int y, int width, int height, Color color) {
        g2.setPaint(color);
        switch (drawingType) {
            case MessageProtocol.RECTANGLE:

                g2.drawRect(x, y, width, height);
                break;

            case MessageProtocol.CIRCLE:
            case MessageProtocol.OVAL:

                g2.drawOval(x, y, width, height);
                break;

            case MessageProtocol.LINE:

                g2.drawLine(x, y, width, height);
                break;

            case MessageProtocol.ERASER:

                g2.fillRect(x, y, width, height);
                break;
        }
        g2.setPaint(this.color);
        repaint();
    }

    /**
     * Draws text received from the server
     * @param drawingType type of drawing
     * @param text text
     * @param x x
     * @param y y
     * @param color color
     */
    public void updateCanvas(String drawingType, String text, int x, int y, Color color) {
        if (drawingType.equals(MessageProtocol.TEXT_BOX)) {
            g2.setPaint(color);
            g2.drawString(text, x, y);
            g2.setPaint(this.color);
            repaint();
        }
    }

    /**
     * Clears the canvas
     */
    private void clear() {
        g2.setPaint(Color.white);
        g2.fillRect(0,0, getSize().width, getSize().height);
        g2.setPaint(Color.black);
        repaint();
    }

    /**
     * Applies the state of the session's canvas locally
     * @param imageString string containing the encoded image
     * @throws ImageHandlerException error decoding the image string
     */
    public void setImage(String imageString) throws ImageHandlerException {
        ImageHandler imageHandler = new ImageHandler();
        BufferedImage updatedImage =  imageHandler.convertImageString(imageString);
        image.getGraphics().drawImage(updatedImage,0,0,updatedImage.getWidth(),updatedImage.getHeight(),null);
        repaint();
    }

    /**
     * Set paint color
     * @param color color
     */
    void setColor(Color color) {
        this.color = color;
    }

    /**
     * Saves user inputted text
     * @param text text
     */
    void setText(String text) {
        this.text = text;
    }

    /**
     * Set selected tool
     * @param tool tool
     */
    void setTool(String tool) {
        selectedTool = tool;
    }

    /**
     * Returns true if user has selected a tool
     * @return boolean
     */
    private boolean toolSelected() {
        return (selectedTool != null);
    }

    /**
     * Returns color selected
     * @return color
     */
    Color getColor() {
        return this.color;
    }

    /**
     * Returns the x coordinate required to draw shapes
     * @return x coordinate
     */
    private int getPosX() {
        return Math.min(x1, x2);
    }

    /**
     * Returns the y coordinate required to draw shapes
     * @return y coordinate
     */
    private int getPosY() {
        return Math.min(y1, y2);
    }

    /**
     * Returns the height required to draw shapes
     * @return height
     */
    private int getShapeHeight() {
        return Math.abs(y2 - y1);
    }

    /**
     * Returns the width required to draw shapes
     * @return width
     */
    private int getShapeWidth() {
        return Math.abs(x2 - x1);
    }

    /**
     * Returns the radius required to draw a circle
     * @return radius
     */
    private int getRadius() {
        return (int) Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    }

    /**
     * Returns the diameter required to draw a circle
     * @param radius radius
     * @return diameter
     */
    private int getDiameter(int radius) {
        return radius * 2;
    }
}