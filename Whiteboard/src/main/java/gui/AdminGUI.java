package gui;
import io.InvalidFormatException;
import server.ServerController;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AdminGUI {

    private ServerController serverController;

    // GUI Components
    private JFrame frame;
    private DefaultListModel<String> listModel;
    private JList<String> list;
    private AdminCanvas canvas;


    // Alert Messages
    private final String JOIN_REQUEST = "A new user (%s) is requesting to join this session, do you wish to allow " +
            "this?";
    private final String INCOMING_USER = "New User Request";
    private final String INPUT_PROMPT = "Enter your text";
    private final String EMPTY_TEXT = "Do not leave it empty!";
    private final String NO_SELECTION = "No user is selected, please select a user!";
    private final String INVALID_SELECTION = "Invalid selection, please select a non-admin user!";
    private final String ERROR_SAVE = "Unable to save file! Please try again.";
    private final String ERROR_LOAD = "Unable to load file! Please try again.";
    private final String ERROR_FORMAT = "Unable to read file! Please ensure the file is in PNG format!";

    // Warning Messages
    private final String WARNING_TITLE = "Warning";
    private final String WARNING_SHUT_DOWN = "Are you sure you want to shut the server down?";
    private final String WARNING_QUIT = "Are you sure you want to quit the application? The server will be shut down " +
            "and all users will be disconnected.";
    private final String WARNING_CLEAR = "Are you sure you want to reset this whiteboard?";
    private final String WARNING_BOOT = "Are you sure you want to boot this user?";
    private final String WARNING_BOOT_ALL = "Are you sure you want to boot all connected users?";

    public AdminGUI(ServerController serverController) {
        this.serverController = serverController;
    }

    public void initialize() {

        // Main Window
        frame = new JFrame();
        frame.setTitle("Whiteboard");
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setBounds(100, 100, 750, 500);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
        frame.getContentPane().setLayout(gridBagLayout);

        // Canvas
        JPanel canvasPanel = new JPanel();
        canvas = new AdminCanvas(550,440, serverController);
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

        // Eraser Button
        JButton eraserButton = new JButton();
        eraserButton.setIcon(new ImageIcon(getClass().getResource("/icons/eraser.png")));
        eraserButton.setSize(new Dimension(24,24));
        eraserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.setTool(canvas.TOOL_ERASER);
            }
        });
        GridBagConstraints gbc_eraserButton = new GridBagConstraints();
        gbc_eraserButton.insets = new Insets(0, 0, 5, 5);
        gbc_eraserButton.gridx = 1;
        gbc_eraserButton.gridy = 2;
        sidebar.add(eraserButton, gbc_eraserButton);

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
        gbc_colorButton.gridy = 3;
        sidebar.add(colorButton, gbc_colorButton);

        // User List
        ArrayList<String> userList = serverController.getUserList();
        listModel = new DefaultListModel<>();
        for (String item : userList) {
            listModel.addElement(item);
        }
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

        // Boot Button
        JButton bootButton = new JButton("Boot");
        bootButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedUser = list.getSelectedValue();
                int selectedIndex = list.getSelectedIndex();
                if (selectedUser == null) {
                    // If no user selected
                    displayAlert(NO_SELECTION);
                } else if (selectedIndex == 0) {
                    // If the admin was selected
                    displayAlert(INVALID_SELECTION);
                } else {
                    int choice = JOptionPane.showConfirmDialog(frame, WARNING_BOOT,
                            WARNING_TITLE,
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (choice == JOptionPane.YES_NO_OPTION) {
                        serverController.bootUser(selectedUser);
                    }
                }
            }
        });
        GridBagConstraints gbc_bootButton = new GridBagConstraints();
        gbc_bootButton.insets = new Insets(0, 0, 5, 5);
        gbc_bootButton.gridx = 0;
        gbc_bootButton.gridy = 13;
        sidebar.add(bootButton, gbc_bootButton);

        // Boot All Button
        JButton bootAllButton = new JButton("Boot All");
        bootAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(frame, WARNING_BOOT_ALL,
                        WARNING_TITLE,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_NO_OPTION) {
                    serverController.bootAll();
                }
            }
        });
        GridBagConstraints gbc_bootAllButton = new GridBagConstraints();
        gbc_bootAllButton.insets = new Insets(0, 0, 5, 0);
        gbc_bootAllButton.gridx = 1;
        gbc_bootAllButton.gridy = 13;
        sidebar.add(bootAllButton, gbc_bootAllButton);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        // Menu Bar -> File
        JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);

        // Menu Bar -> File -> Open
        JMenuItem menuFileOpen = new JMenuItem("Open");
        menuFileOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
                fileChooser.setDialogTitle("Open");
                if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File fileToOpen = fileChooser.getSelectedFile();
                    try {
                        canvas.loadImage(ImageIO.read(fileToOpen));
                    } catch (IOException ex) {
                        // Unable to load file content
                        displayAlert(ERROR_LOAD);
                    } catch (InvalidFormatException ex) {
                        // Incorrect file format
                        displayAlert(ERROR_FORMAT);
                    }
                    serverController.refreshState();
                }
            }
        });
        menuFile.add(menuFileOpen);

        // Menu Bar -> File -> Save
        JMenuItem menuFileSave = new JMenuItem("Save As");
        menuFileSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
                fileChooser.setDialogTitle("Save as");
                if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                   File fileToSave = fileChooser.getSelectedFile();
                   try {
                       ImageIO.write(canvas.getImage(), "png", fileToSave);
                   } catch (Exception ex) {
                       displayAlert(ERROR_SAVE);
                   }
               }
            }
        });
        menuFile.add(menuFileSave);

        // Menu Bar -> Settings
        JMenu menuSettings = new JMenu("Settings");
        menuBar.add(menuSettings);

        // Menu Bar -> Settings -> Reset
        JMenuItem menuSettingsReset = new JMenuItem("Reset Canvas");
        menuSettingsReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(frame, WARNING_CLEAR,
                        WARNING_TITLE,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_NO_OPTION) {
                    canvas.clear();
                    serverController.refreshState();
                }
            }
        });
        menuSettings.add(menuSettingsReset);

        // Menu Bar -> Settings -> Close Server
        JMenuItem menuSettingsCloseServer = new JMenuItem("Close Server");
        menuSettingsCloseServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(frame, WARNING_SHUT_DOWN,
                        WARNING_TITLE,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_NO_OPTION) {
                    serverController.shutDown();
                }
            }
        });
        menuSettings.add(menuSettingsCloseServer);

        // Show GUI
        frame.setVisible(true);

        // Prompts confirmation to close server and exits gracefully if user selected 'OK'
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(frame, WARNING_QUIT,
                        WARNING_TITLE,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_NO_OPTION) {
                    serverController.shutDown();
                    e.getWindow().dispose();
                }
            }
        });
    }

    /**
     * Updates user list
     */
    public void updateUserList() {
        ArrayList<String> userList = serverController.getUserList();
        listModel.clear();
        for (String item : userList) {
            listModel.addElement(item);}
        list.repaint();
    }

    /**
     * Displays a join request of a new user and returns true if admin accepts
     * @param username username
     * @return boolean
     */
    public boolean displayJoinRequest(String username) {
        return JOptionPane.showConfirmDialog(frame, String.format(JOIN_REQUEST,
                username), INCOMING_USER, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /**
     * Displays an alert popup window
     * @param message message
     */
    private void displayAlert(String message) {
        JOptionPane.showMessageDialog(frame, message, "Alert", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Returns true if input is empty
     * @param input input string
     * @return boolean
     */
    private boolean isEmptyInput(String input) {
        return input.trim().length() == 0 || input == null;
    }

    /**
     * Returns the admin canvas
     * @return canvas
     */
    public AdminCanvas getCanvas() {
        return canvas;
    }
}