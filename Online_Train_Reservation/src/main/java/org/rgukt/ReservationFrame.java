package org.rgukt;

// FILE ---- 5


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ReservationFrame extends JFrame {

    // Booking form fields
    private JTextField passengerField;
    private JTextField trainNumberField;
    private JTextField trainNameField;
    private JTextField dateField;
    private JTextField sourceField;
    private JTextField destinationField;

    private JComboBox<String> classBox;

    // Cancellation form fields
    private JTextField pnrField;
    private JTextArea bookingDetails;

    private String fetchedPnr = null;

    public ReservationFrame() {

        setTitle("Online Train Reservation System");

        setSize(750, 600);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Book Ticket", createBookingPanel());

        tabs.addTab("Cancel Ticket", createCancellationPanel());

        add(tabs);

        setVisible(true);
    }

    // =====================================
    // BOOKING PANEL
    // =====================================

    private JPanel createBookingPanel() {

        JPanel panel = new JPanel(
                new GridLayout(8, 2, 12, 12)
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 60, 30, 60
                )
        );

        passengerField = new JTextField();

        trainNumberField = new JTextField();

        trainNameField = new JTextField();

        trainNameField.setEditable(false);

        dateField = new JTextField();

        sourceField = new JTextField();

        destinationField = new JTextField();

        classBox = new JComboBox<>(new String[]{
                "Sleeper",
                "AC 3 Tier",
                "AC 2 Tier",
                "AC First Class",
                "Chair Car"
        });

        JButton bookButton = new JButton("Book Ticket");

        panel.add(new JLabel("Passenger Name:"));
        panel.add(passengerField);

        panel.add(new JLabel("Train Number:"));
        panel.add(trainNumberField);

        panel.add(new JLabel("Train Name:"));
        panel.add(trainNameField);

        panel.add(new JLabel("Class Type:"));
        panel.add(classBox);

        panel.add(new JLabel("Journey Date (YYYY-MM-DD):"));
        panel.add(dateField);

        panel.add(new JLabel("Source Station:"));
        panel.add(sourceField);

        panel.add(new JLabel("Destination Station:"));
        panel.add(destinationField);

        panel.add(new JLabel(""));
        panel.add(bookButton);

        // Automatically populate train name
        trainNumberField.getDocument().addDocumentListener(
                new DocumentListener() {

                    public void insertUpdate(DocumentEvent e) {
                        updateTrainName();
                    }

                    public void removeUpdate(DocumentEvent e) {
                        updateTrainName();
                    }

                    public void changedUpdate(DocumentEvent e) {
                        updateTrainName();
                    }
                }
        );

        bookButton.addActionListener(e -> bookTicket());

        return panel;
    }

    // =====================================
    // FETCH TRAIN NAME
    // =====================================

    private void updateTrainName() {

        String number =
                trainNumberField.getText().trim();

        trainNameField.setText("");

        if (!number.matches("\\d+")) {
            return;
        }

        String sql = """
            SELECT train_name
            FROM trains
            WHERE train_number = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(number));

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    trainNameField.setText(
                            rs.getString("train_name")
                    );
                }
            }

        } catch (Exception e) {

            trainNameField.setText("");
        }
    }

    // =====================================
    // BOOK TICKET
    // =====================================

    private void bookTicket() {

        String passenger =
                passengerField.getText().trim();

        String trainNumber =
                trainNumberField.getText().trim();

        String trainName =
                trainNameField.getText().trim();

        String classType =
                (String) classBox.getSelectedItem();

        String date =
                dateField.getText().trim();

        String source =
                sourceField.getText().trim();

        String destination =
                destinationField.getText().trim();

        // Empty field validation
        if (passenger.isEmpty() ||
                trainNumber.isEmpty() ||
                trainName.isEmpty() ||
                date.isEmpty() ||
                source.isEmpty() ||
                destination.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all required fields."
            );

            return;
        }

        // Numeric train number validation
        if (!trainNumber.matches("\\d+")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Train number must contain digits only."
            );

            return;
        }

        // Date validation
        LocalDate journeyDate;

        try {

            journeyDate = LocalDate.parse(date);

            if (journeyDate.isBefore(LocalDate.now())) {

                JOptionPane.showMessageDialog(
                        this,
                        "Journey date cannot be in the past."
                );

                return;
            }

        } catch (DateTimeParseException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid date! Use YYYY-MM-DD."
            );

            return;
        }

        if (source.equalsIgnoreCase(destination)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Source and destination cannot be the same."
            );

            return;
        }

        String pnr = null;

        String sql = """
            INSERT INTO reservations
            (passenger_name, train_number, train_name,
             class_type, journey_date, source, destination)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        """;

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                long id;

                try (PreparedStatement ps =
                             con.prepareStatement(sql)) {

                    ps.setString(1, passenger);

                    ps.setInt(2, Integer.parseInt(trainNumber));

                    ps.setString(3, trainName);

                    ps.setString(4, classType);

                    ps.setDate(
                            5,
                            java.sql.Date.valueOf(journeyDate)
                    );

                    ps.setString(6, source);

                    ps.setString(7, destination);

                    try (ResultSet rs = ps.executeQuery()) {

                        rs.next();

                        id = rs.getLong("id");
                    }
                }

                // Generate unique PNR
                pnr = "PNR" + String.format("%08d", id);

                String updateSQL = """
                    UPDATE reservations
                    SET pnr = ?
                    WHERE id = ?
                """;

                try (PreparedStatement ps =
                             con.prepareStatement(updateSQL)) {

                    ps.setString(1, pnr);

                    ps.setLong(2, id);

                    ps.executeUpdate();
                }

                con.commit();

            } catch (Exception e) {

                con.rollback();

                throw e;
            }

            String details =
                    "BOOKING CONFIRMED!\n\n" +
                            "PNR Number: " + pnr + "\n" +
                            "Passenger: " + passenger + "\n" +
                            "Train Number: " + trainNumber + "\n" +
                            "Train Name: " + trainName + "\n" +
                            "Class: " + classType + "\n" +
                            "Journey Date: " + date + "\n" +
                            "Source: " + source + "\n" +
                            "Destination: " + destination;

            JOptionPane.showMessageDialog(
                    this,
                    details,
                    "Reservation Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearBookingFields();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Booking Failed: " + e.getMessage()
            );
        }
    }

    // =====================================
    // CLEAR BOOKING FORM
    // =====================================

    private void clearBookingFields() {

        passengerField.setText("");

        trainNumberField.setText("");

        trainNameField.setText("");

        dateField.setText("");

        sourceField.setText("");

        destinationField.setText("");

        classBox.setSelectedIndex(0);
    }

    // =====================================
    // CANCELLATION PANEL
    // =====================================

    private JPanel createCancellationPanel() {

        JPanel panel = new JPanel(
                new BorderLayout(15, 15)
        );

        JPanel topPanel = new JPanel(
                new GridLayout(2, 2, 10, 10)
        );

        topPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 50, 10, 50
                )
        );

        pnrField = new JTextField();

        JButton fetchButton =
                new JButton("Fetch Booking");

        topPanel.add(new JLabel("Enter PNR Number:"));

        topPanel.add(pnrField);

        topPanel.add(new JLabel(""));

        topPanel.add(fetchButton);

        bookingDetails = new JTextArea();

        bookingDetails.setEditable(false);

        bookingDetails.setFont(
                new Font("Monospaced", Font.PLAIN, 14)
        );

        JScrollPane scrollPane =
                new JScrollPane(bookingDetails);

        JButton cancelButton =
                new JButton("Confirm Cancellation");

        JPanel bottomPanel = new JPanel();

        bottomPanel.add(cancelButton);

        panel.add(topPanel, BorderLayout.NORTH);

        panel.add(scrollPane, BorderLayout.CENTER);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        fetchButton.addActionListener(e -> fetchBooking());

        cancelButton.addActionListener(e -> cancelBooking());

        return panel;
    }

    // =====================================
    // FETCH BOOKING USING PNR
    // =====================================

    private void fetchBooking() {

        String pnr =
                pnrField.getText().trim();

        fetchedPnr = null;

        bookingDetails.setText("");

        if (pnr.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter PNR number."
            );

            return;
        }

        String sql = """
            SELECT *
            FROM reservations
            WHERE pnr = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, pnr);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    fetchedPnr = pnr;

                    String details =
                            "BOOKING DETAILS\n" +
                                    "-----------------------------\n" +
                                    "PNR: " + rs.getString("pnr") + "\n" +
                                    "Passenger: " +
                                    rs.getString("passenger_name") + "\n" +
                                    "Train Number: " +
                                    rs.getInt("train_number") + "\n" +
                                    "Train Name: " +
                                    rs.getString("train_name") + "\n" +
                                    "Class: " +
                                    rs.getString("class_type") + "\n" +
                                    "Journey Date: " +
                                    rs.getDate("journey_date") + "\n" +
                                    "Source: " +
                                    rs.getString("source") + "\n" +
                                    "Destination: " +
                                    rs.getString("destination");

                    bookingDetails.setText(details);

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "No booking found for this PNR."
                    );
                }
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }

    // =====================================
    // CANCEL BOOKING
    // =====================================

    private void cancelBooking() {

        String pnr =
                pnrField.getText().trim();

        if (fetchedPnr == null ||
                !fetchedPnr.equals(pnr) ||
                bookingDetails.getText().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fetch a valid booking first."
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel this booking?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = """
            DELETE FROM reservations
            WHERE pnr = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, pnr);

            int rows = ps.executeUpdate();

            if (rows > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Ticket cancelled successfully!"
                );

                pnrField.setText("");

                bookingDetails.setText("");

                fetchedPnr = null;

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Booking not found."
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cancellation Failed: " + e.getMessage()
            );
        }
    }
}

