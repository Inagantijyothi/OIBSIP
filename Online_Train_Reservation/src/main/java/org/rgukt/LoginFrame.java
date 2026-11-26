package org.rgukt;

// FILE --- 4

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {

        setTitle("Online Reservation System - Login");

        setSize(420, 300);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(
                new GridLayout(4, 2, 12, 15)
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 30, 30, 30
                )
        );

        JLabel title = new JLabel(
                "TRAIN RESERVATION LOGIN"
        );

        title.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        usernameField = new JTextField();

        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");

        panel.add(title);
        panel.add(new JLabel(""));

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        panel.add(new JLabel(""));
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(e -> login());

        passwordField.addActionListener(e -> login());

        setVisible(true);
    }

    private void login() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password"
            );

            return;
        }

        String sql = """
            SELECT password_hash
            FROM users
            WHERE username = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    String storedHash =
                            rs.getString("password_hash");

                    if (PasswordUtil.checkPassword(
                            password, storedHash)) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Login Successful!"
                        );

                        dispose();

                        new ReservationFrame();

                        return;
                    }
                }

                JOptionPane.showMessageDialog(
                        this,
                        "Access Denied! Invalid Credentials",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database Error: " + e.getMessage()
            );
        }
    }
}

