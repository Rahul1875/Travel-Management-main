package travel;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookPackage extends JFrame {

    private JComboBox<String> packageDropdown;  //for travel package info
    private JTextField passengerCapacityField;
    private int enteredPassengerCapacity;   // entering the passenger capacity

    public BookPackage() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headingLabel = new JLabel("Book Travel Package");
        headingLabel.setFont(new Font("Montserrat", Font.BOLD, 24));
        headingPanel.add(headingLabel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(10, 2, 8, 8));

        Font labelFont = new Font("Montserrat", Font.BOLD, 18);
        Font fieldFont = new Font("Montserrat", Font.PLAIN, 18);
        Font buttonFont = new Font("Montserrat", Font.BOLD, 16);

        JLabel packageLabel = new JLabel("Select Travel Package:");
        packageLabel.setFont(labelFont);
        formPanel.add(packageLabel);

        packageDropdown = new JComboBox<>(getTravelPackageNames());
        packageDropdown.setFont(new Font("Montserrat", Font.PLAIN, 14));
        formPanel.add(packageDropdown);

        JLabel passengerCapacityLabel = new JLabel("Passenger Capacity:");
        passengerCapacityLabel.setFont(labelFont);
        formPanel.add(passengerCapacityLabel);

        passengerCapacityField = new JTextField();
        passengerCapacityField.setFont(fieldFont);
        formPanel.add(passengerCapacityField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton nextButton = new JButton("Next");
        nextButton.setFont(buttonFont);
        nextButton.setPreferredSize(new Dimension(120, 30));
        nextButton.addActionListener(e -> next());
        buttonPanel.add(nextButton);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(buttonFont);
        backButton.setPreferredSize(new Dimension(200, 30));
        backButton.addActionListener(e -> backToDashboard());
        buttonPanel.add(backButton);

        add(headingPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // taking the details of travel package from travel_package db
    private String[] getTravelPackageNames() {
        List<String> packageNames = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123")) {
            String query = "SELECT name FROM travel_packages";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (var resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        packageNames.add(resultSet.getString("name"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return packageNames.toArray(new String[0]);
    }

    /* functionality in the next button - The passenger capacity for a travel package should be
    updated with the entered passenger cap and have validated that*/
    private void next() {
        // Retrieve the selected travel package
        String selectedPackage = (String) packageDropdown.getSelectedItem();
        enteredPassengerCapacity = Integer.parseInt(passengerCapacityField.getText());
        // Check if passenger capacity is selected
        if (enteredPassengerCapacity <= 0) {
            JOptionPane.showMessageDialog(this, "Enter Valid Passenger Count.");
            return;
        }

        // Check if the passenger capacity exceeds the overall capacity
        int overallCapacity = getOverallPassengerCapacity(selectedPackage);
        if (enteredPassengerCapacity > overallCapacity) {
            JOptionPane.showMessageDialog(this, "Passenger Count exceeds the overall capacity for the selected package. " +
                    "Capacity remaining is " + overallCapacity);
            return;
        }

        // Update the overall passenger capacity in the database
        updateOverallPassengerCapacity(selectedPackage, enteredPassengerCapacity);

        // Create a new PassengerDetails frame and pass the selected package, passenger capacity, and dashboard
        EventQueue.invokeLater(() -> {
            try {
                PassengerDetails frame = new PassengerDetails(selectedPackage, enteredPassengerCapacity);
                frame.setVisible(true);
                dispose(); // Close the current frame
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //taking the passenger cap of the selected travel package from travel_package
    private int getOverallPassengerCapacity(String selectedPackage) {
        int overallCapacity = 0;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123")) {
            String query = "SELECT passenger_capacity FROM travel_packages WHERE name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, selectedPackage);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        overallCapacity = resultSet.getInt("passenger_capacity");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return overallCapacity;
    }

    //updating back the passenger capacity of the selected travel package and saving the data back to the db
    private void updateOverallPassengerCapacity(String selectedPackage, int bookedPassengerCapacity) {
        // Retrieve the current overall passenger capacity for the selected package from the database
        int currentOverallCapacity = getOverallPassengerCapacity(selectedPackage);

        // Update the overall passenger capacity by subtracting the booked capacity
        int updatedOverallCapacity = currentOverallCapacity - bookedPassengerCapacity;

        // Update the database with the new overall passenger capacity
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123")) {
            String query = "UPDATE travel_packages SET passenger_capacity = ? WHERE name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, updatedOverallCapacity);
                preparedStatement.setString(2, selectedPackage);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //disposing back to the dashboard
    private void backToDashboard() {
        dispose();
        EventQueue.invokeLater(() -> new Dashboard().setVisible(true));
    }

    public static void main(String[] args) {
        new BookPackage();
    }
}