package org.rgukt;

// FILE ----- 6



import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        // Initialize database and tables
        DatabaseInitializer.initialize();

        // Start Swing application
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}

