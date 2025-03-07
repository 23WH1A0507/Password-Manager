package projectjava;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;

public class PasswordManager {

    private static final String FILE_NAME = "password_manager_data.txt";
    private final HashMap<String, String> userDatabase;
    private final HashMap<String, HashMap<String, String>> userPasswordStores;
    private String loggedInUser;

    public PasswordManager() {
        userDatabase = new HashMap<>();
        userPasswordStores = new HashMap<>();
        loggedInUser = null;
        loadDataFromFile();
    }

    private void loadDataFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            boolean readingPasswords = false;
            while ((line = reader.readLine()) != null) {
                if (line.equals("---")) {
                    readingPasswords = true;
                    continue;
                }
                if (!readingPasswords) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        userDatabase.put(parts[0], parts[1]);
                        userPasswordStores.put(parts[0], new HashMap<>());
                    }
                } else {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String username = parts[0];
                        String[] keyValue = parts[1].split(",");
                        if (keyValue.length == 2) {
                            userPasswordStores.get(username).put(keyValue[0], keyValue[1]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("No existing data found. Starting fresh.");
        }
    }

    private void saveDataToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String username : userDatabase.keySet()) {
                writer.write(username + "," + userDatabase.get(username));
                writer.newLine();
            }
            writer.write("---");
            writer.newLine();
            for (String username : userPasswordStores.keySet()) {
                HashMap<String, String> passwords = userPasswordStores.get(username);
                for (String key : passwords.keySet()) {
                    writer.write(username + ":" + key + "," + passwords.get(key));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    public void showLoginWindow() {
        JFrame frame = new JFrame("Password Manager - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400); // Increase height to accommodate image
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Set background color
        frame.getContentPane().setBackground(new Color(230, 230, 250));

        // Title Label
        JLabel titleLabel = new JLabel("Password Manager");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(75, 0, 130));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 20, 0);
        frame.add(titleLabel, gbc);

        // Username and Password Labels and Fields
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(new Color(75, 0, 130));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 5, 5);
        frame.add(userLabel, gbc);

        JTextField userField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 5, 5, 10);
        frame.add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(new Color(75, 0, 130));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 10, 5, 5);
        frame.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 5, 5, 10);
        frame.add(passField, gbc);

        // Buttons for Register and Login
        JButton registerButton = createButton("Register", new Color(144, 238, 144));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 10, 5);
        frame.add(registerButton, gbc);

        JButton loginButton = createButton("Login", new Color(173, 216, 230));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 5, 10, 10);
        frame.add(loginButton, gbc);

        // Output Area
        JTextArea outputArea = new JTextArea(5, 30);
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(240, 248, 255));
        outputArea.setForeground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        frame.add(scrollPane, gbc);

        // Image at the bottom of the login window
        ImageIcon imageIcon = resizeImageIcon(new ImageIcon("path/to/your/image.png"), 300, 150); // Replace with actual image path
        JLabel imageLabel = new JLabel(imageIcon);
        gbc.gridx = 0;
        gbc.gridy = 5; // This should go below all other components
        gbc.gridwidth = 2; // Span across two columns
        gbc.insets = new Insets(10, 10, 10, 10);
        frame.add(imageLabel, gbc);

        // Action Listeners for Register and Login buttons
        registerButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (userDatabase.containsKey(username)) {
                outputArea.setText("Username already exists!");
            } else {
                userDatabase.put(username, password);
                userPasswordStores.put(username, new HashMap<>());
                outputArea.setText("User registered successfully!");
                saveDataToFile();
                userField.setText(""); // Clear the field after registration
                passField.setText("");
            }
        });

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
                loggedInUser = username;
                outputArea.setText("Login successful! Welcome, " + username + ".");
                frame.dispose(); // Close the login frame
                showMenuWindow(); // Show menu window after login
            } else {
                outputArea.setText("Invalid username or password.");
            }
        });

        frame.setVisible(true);
    }

    public void showMenuWindow() {
        JFrame frame = new JFrame("Password Manager - Menu");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(230, 230, 250));
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel welcomeLabel = new JLabel("Welcome, " + loggedInUser + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(75, 0, 130));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 20, 0);
        frame.add(welcomeLabel, gbc);

        // Menu Buttons
        JButton addPasswordButton = createButton("Add Password", new Color(144, 238, 144));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 5, 10);
        frame.add(addPasswordButton, gbc);

        JButton getPasswordButton = createButton("Get Password", new Color(173, 216, 230));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 10, 5, 10);
        frame.add(getPasswordButton, gbc);

        JButton getAllPasswordsButton = createButton("Get All Passwords", new Color(135, 206, 250));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 10, 5, 10);
        frame.add(getAllPasswordsButton, gbc);

        JButton deletePasswordButton = createButton("Delete Password", new Color(255, 182, 193));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 10, 5, 10);
        frame.add(deletePasswordButton, gbc);

        JButton logoutButton = createButton("Logout", new Color(255, 204, 204));
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 5, 20, 10);
        frame.add(logoutButton, gbc);

        // Add actions for buttons
        addPasswordButton.addActionListener(e -> showAddPasswordWindow());
        getPasswordButton.addActionListener(e -> showGetPasswordWindow());
        getAllPasswordsButton.addActionListener(e -> showGetAllPasswordsWindow());
        deletePasswordButton.addActionListener(e -> showDeletePasswordWindow());
        logoutButton.addActionListener(e -> {
            loggedInUser = null;
            frame.dispose();
            showLoginWindow();
        });

        frame.setVisible(true);
    }

    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createSoftBevelBorder(1));
        button.setPreferredSize(new Dimension(150, 40));
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private ImageIcon resizeImageIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage(); // Transform it
        Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH); // Scale it the smooth way
        return new ImageIcon(newImg); // Transform it back
    }

    private void showAddPasswordWindow() {
        JFrame addFrame = new JFrame("Add Password");
        addFrame.setSize(400, 300);
        addFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbcAdd = new GridBagConstraints();

        JLabel keyLabel = new JLabel("Key:");
        gbcAdd.gridx = 0;
        gbcAdd.gridy = 0;
        gbcAdd.insets = new Insets(10, 10, 5, 5);
        addFrame.add(keyLabel, gbcAdd);

        JTextField keyField = new JTextField(20);
        gbcAdd.gridx = 1;
        gbcAdd.gridy = 0;
        gbcAdd.insets = new Insets(10, 5, 5, 10);
        addFrame.add(keyField, gbcAdd);

        JLabel passwordLabel = new JLabel("Password:");
        gbcAdd.gridx = 0;
        gbcAdd.gridy = 1;
        gbcAdd.insets = new Insets(5, 10, 5, 5);
        addFrame.add(passwordLabel, gbcAdd);

        JPasswordField passwordField = new JPasswordField(20);
        gbcAdd.gridx = 1;
        gbcAdd.gridy = 1;
        gbcAdd.insets = new Insets(5, 5, 5, 10);
        addFrame.add(passwordField, gbcAdd);

        JButton saveButton = createButton("Save", new Color(144, 238, 144));
        gbcAdd.gridx = 1;
        gbcAdd.gridy = 2;
        gbcAdd.insets = new Insets(10, 5, 10, 10);
        addFrame.add(saveButton, gbcAdd);

        saveButton.addActionListener(ev -> {
            String key = keyField.getText();
            String password = new String(passwordField.getPassword());
            if (!key.isEmpty() && !password.isEmpty()) {
                userPasswordStores.get(loggedInUser).put(key, password);
                saveDataToFile();
                JOptionPane.showMessageDialog(addFrame, "Password saved successfully!");
                addFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(addFrame, "Please fill out both fields.");
            }
        });

        addFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PasswordManager manager = new PasswordManager();
            manager.showLoginWindow();
        });
    }
}
