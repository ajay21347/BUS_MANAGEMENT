package bus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Login - Bus Management System");
        setSize(350, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("User Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);
        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> checkLogin());
        registerButton.addActionListener(e -> new RegisterFrame());

        setVisible(true);
    }
//code updated
private void checkLogin() {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword());

    String role = getUserRole(username, password);
    if (role != null) {
        JOptionPane.showMessageDialog(this, "Login Successful as " + role + "!");
        dispose();

        if (role.equals("admin")) {
            new AdminDashboard(); // make sure this class exists
        } else {
            new UserDashboard(); // make sure this class exists
        }
    } else {
        JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private String getUserRole(String user, String pass) {
        try (BufferedReader br = new BufferedReader(new FileReader("src/bus/user.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equals(user) && parts[1].equals(pass)) {
                    return parts[2]; // return role
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // invalid user
    }


    public static void main(String[] args) {
        new LoginFrame();
    }
}

// ✅ Registration Frame Class
class RegisterFrame extends JFrame {
    public RegisterFrame() {
        setTitle("User Registration");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(30, 30, 80, 25);
        add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(120, 30, 160, 25);
        add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(30, 70, 80, 25);
        add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(120, 70, 160, 25);
        add(passField);

        JLabel confirmLabel = new JLabel("Confirm:");
        confirmLabel.setBounds(30, 110, 80, 25);
        add(confirmLabel);

        JPasswordField confirmField = new JPasswordField();
        confirmField.setBounds(120, 110, 160, 25);
        add(confirmField);

        //New Code Added...
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(30, 150, 80, 25);
        add(roleLabel);

        String[] roles = {"user", "admin"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        roleBox.setBounds(120, 150, 160, 25);
        add(roleBox);
        //new code Ended...

        JButton registerBtn = new JButton("Register");
        registerBtn.setBounds(120, 190, 100, 30);
        add(registerBtn);

        registerBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!");
                return;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/bus/user.txt", true))) {
                String role = (String) roleBox.getSelectedItem();
                writer.write(username + "," + password + "," + role);
                writer.newLine();
                JOptionPane.showMessageDialog(this, "User registered successfully!");
                dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error writing to user.txt!");
            }
        });

        setVisible(true);
    }
}
