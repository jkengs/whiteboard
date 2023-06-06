package gui;
import client.Client;
import client.ClientMessageHandler;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class ClientGUI {

    private Client client;
    private ClientMessageHandler clientMessageHandler;

    // GUI Components
    private ClientCanvas canvas;
    private DefaultListModel<String> listModel;
    private JList<String> list;
    private JFrame frame;

    // Alert Messages
    private final String EMPTY_TEXT = "Do not leave it empty!";
    private final String INPUT_PROMPT = "Enter your text";

    /**
     * ClientGUI constructor
     * @param client client
     * @param clientMessageHandler clientMessageHandler
     */
    public ClientGUI(Client client, ClientMessageHandler clientMessageHandler) {
        this.client = client;
        this.clientMessageHandler = clientMessageHandler;
    }

    /**
     * Initializes GUI components
     */
    public void initialize() {

        // Main Window
        frame = new JFrame();
        frame.setTitle("Whiteboard");
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setBounds(100, 100, 750, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
        frame.getContentPane().setLayout(gridBagLayout);

        // Canvas
        JPanel canvasPanel = new JPanel();
        canvas = new ClientCanvas(550,440, clientMessageHandler);
        GridBagConstraints gbc_canvasPanel = new GridBagConstraints();
        gbc_canvasPanel.gridwidth = 18;
        gbc_canvasPanel.insets = new Insets(0, 0, 0, 5);
        gbc_canvasPanel.fill = GridBagConstraints.BOTH;
        gbc_canvasPanel.gridx = 1;
        gbc_canvasPanel.gridy = 0;
        canvasPanel.add(canvas);
        frame.getContentPane().add(canvasPanel, gbc_canvasPanel);

        // Sidebar
        JPanel sidebar = new JPanel();
        GridBagConstraints gbc_sidebar = new GridBagConstraints();
        gbc_sidebar.gridwidth = 6;
        gbc_sidebar.fill = GridBagConstraints.BOTH;
        gbc_sidebar.gridx = 19;
        gbc_sidebar.gridy = 0;
        frame.getContentPane().add(sidebar, gbc_sidebar);
        GridBagLayout gbl_sidebar = new GridBagLayout();
        gbl_sidebar.columnWidths = new int[]{0, 0, 0};
        gbl_sidebar.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        gbl_sidebar.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        gbl_sidebar.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
        sidebar.setLayout(gbl_sidebar);

        // Line Button
        JButton lineButton = new JButton();
        lineButton.setIcon(new ImageIcon(getClass().getResource("/icons/line.png")));
        lineButton.setSize(new Dimension(24,24));
        lineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.setTool(canvas.TOOL_LINE);
            }
        });
        GridBagConstraints gbc_lineButton = new GridBagConstraints();
        gbc_lineButton.insets = new Insets(0, 0, 5, 5);
        gbc_lineButton.gridx = 0;
        gbc_lineButton.gridy = 0;
        sidebar.add(lineButton, gbc_lineButton);

        // Circle Button
        JButton circleButton = new JButton();
        circleButton.setIcon(new ImageIcon(getClass().getResource("/icons/circle.png")));
        circleButton.setSize(new Dimension(24,24));
        circleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.setTool(canvas.TOOL_CIRCLE);
            }
        });
        GridBagConstraints gbc_circleButton = new GridBagConstraints();
        gbc_circleButton.insets = new Insets(0, 0, 5, 0);
        gbc_circleButton.gridx = 1;
        gbc_circleButton.gridy = 0;
        sidebar.add(circleButton, gbc_circleButton);

        // Oval Button
        JButton ovalButton = new JButton();
        ovalButton.setIcon(new ImageIcon(getClass().getResource("/icons/oval.png")));
        ovalButton.setSize(new Dimension(24,24));
        ovalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.setTool(canvas.TOOL_OVAL);
            }
        });
        GridBagConstraints gbc_ovalButton = new GridBagConstraints();
        gbc_ovalButton.insets = new Insets(0, 0, 5, 5);
        gbc_ovalButton.gridx = 0;
        gbc_ovalButton.gridy = 1;
        sidebar.add(ovalButton, gbc_ovalButton);

        // Rectangle Button
        JButton rectangleButton = new JButton();
        rectangleButton.setIcon(new ImageIcon(getClass().getResource("/icons/rectangle.png")));
        rectangleButton.setSize(new Dimension(24,24));
        rectangleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.setTool(canvas.TOOL_RECTANGLE);
            }
        });
        GridBagConstraints gbc_rectangleButton = new GridBagConstraints();
        gbc_rectangleButton.insets = new Insets(0, 0, 5, 0);
        gbc_rectangleButton.gridx = 1;
        gbc_rectangleButton.gridy = 1;
        sidebar.add(rectangleButton, gbc_rectangleButton);

        // Text Button
        JButton textButton = new JButton();
        textButton.setIcon(new ImageIcon(getClass().getResource("/icons/text.png")));
        textButton.setSize(new Dimension(24,24));
        textButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = JOptionPane.showInputDialog(frame, INPUT_PROMPT, null);
                if (text != null) {
                    // User clicked 'OK'
                    if (isEmptyInput(text)) {
                        displayAlert(EMPTY_TEXT);
                    } else {
                        canvas.setText(text);
                        canvas.setTool(canvas.TOOL_TEXT);
                    }
                }
            }
        });
        GridBagConstraints gbc_textButton = new GridBagConstraints();
        gbc_textButton.insets = new Insets(0, 0, 5, 5);
        gbc_textButton.gridx = 0;
        gbc_textButton.gridy = 2;
        sidebar.add(textButton, gbc_textButton);

        // Color Button
        JButton colorButton = new JButton();
        colorButton.setFocusable(false);
        colorButton.setPreferredSize(new Dimension(34,34));
        colorButton.setBackground(canvas.getColor());
        colorButton.setOpaque(true);
        colorButton.setBorderPainted(false);
        colorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Choose Colour
                Color newColor = JColorChooser.showDialog(null, "Choose a color", canvas.getColor());
                if (newColor != null) {
                    // User clicked 'OK'
                    canvas.setColor(newColor);
                    colorButton.setBackground(newColor);
                }
            }
        });
        GridBagConstraints gbc_colorButton = new GridBagConstraints();
        gbc_colorButton.insets = new Insets(0, 0, 5, 0);
        gbc_colorButton.gridx = 1;
        gbc_colorButton.gridy = 2;
        sidebar.add(colorButton, gbc_colorButton);

        // Quit Button
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                client.exitProgram();
            }
        });
        GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
        gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
        gbc_btnNewButton.gridx = 1;
        gbc_btnNewButton.gridy = 13;
        sidebar.add(quitButton, gbc_btnNewButton);

        // User List
        listModel = new DefaultListModel<>();
        listModel.addElement("");
        list = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(list);
        GridBagConstraints gbc_list = new GridBagConstraints();
        gbc_list.gridheight = 7;
        gbc_list.gridwidth = 2;
        gbc_list.insets = new Insets(0, 0, 5, 5);
        gbc_list.fill = GridBagConstraints.BOTH;
        gbc_list.gridx = 0;
        gbc_list.gridy = 5;
        sidebar.add(scrollPane, gbc_list);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        // Menu Bar -> Settings
        JMenu menuSettings = new JMenu("Settings");
        menuBar.add(menuSettings);

        // Menu Bar -> Settings -> Refresh
        JMenuItem menuSettingsRefresh = new JMenuItem("Refresh Canvas");
        menuSettingsRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Refresh canvas
                clientMessageHandler.requestState();
            }
        });
        menuSettings.add(menuSettingsRefresh);

        // Show GUI
        frame.setVisible(true);

        // Exit gracefully on window close
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                client.exitProgram();
            }
        });
    }

    /**
     * Updates user list
     * @param userList list of connected usernames
     */
    public void updateUserList(ArrayList<String> userList) {
        listModel.clear();
        for (String item : userList) {
            listModel.addElement(item);
        }
        list.repaint();
    }

    /**
     * Displays an alert to the user
     * @param message message
     */
    public void displayAlert(String message) {
        JOptionPane.showMessageDialog(frame, message, "Alert", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Returns the canvas
     * @return canvas
     */
    public ClientCanvas getCanvas() {
        return canvas;
    }

    /**
     * Returns true if input is empty
     * @param input input string
     * @return boolean
     */
    private boolean isEmptyInput(String input) {
        return input.trim().length() == 0 || input == null;
    }
}