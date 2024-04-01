package travel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddDestination extends JFrame {

    private JTextField destinationNameField;
    private JTextField activityNameField;
    private JTextField activityCostField;
    private JTextField activityCapField;
    private JComboBox<String> packageDropdown;

    public AddDestination() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headingLabel = new JLabel("Add Destination");
        headingLabel.setFont(new Font("Montserrat", Font.BOLD, 24));
        headingPanel.add(headingLabel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(13, 2, 10, 10));

        Font labelFont = new Font("Montserrat", Font.BOLD, 18);
        Font fieldFont = new Font("Montserrat", Font.PLAIN, 18);
        Font buttonFont = new Font("Montserrat", Font.BOLD, 16);

        JLabel packageLabel = new JLabel("Select Travel Package:");
        packageLabel.setFont(labelFont);
        formPanel.add(packageLabel);

        packageDropdown = new JComboBox<>(getTravelPackageNames());
        packageDropdown.setFont(new Font("Montserrat", Font.PLAIN, 14));
        formPanel.add(packageDropdown);

        JLabel destinationNameLabel = new JLabel("Destination Name:");
        destinationNameLabel.setFont(labelFont);
        formPanel.add(destinationNameLabel);

        destinationNameField = new JTextField();
        destinationNameField.setFont(fieldFont);
        formPanel.add(destinationNameField);

        JLabel activityNameLabel = new JLabel("Activity Name:");
        activityNameLabel.setFont(labelFont);
        formPanel.add(activityNameLabel);

        activityNameField = new JTextField();
        activityNameField.setFont(fieldFont);
        formPanel.add(activityNameField);

        JLabel activityCostLabel = new JLabel("Activity Cost:");
        activityCostLabel.setFont(labelFont);
        formPanel.add(activityCostLabel);

        activityCostField = new JTextField();
        activityCostField.setFont(fieldFont);
        formPanel.add(activityCostField);

        JLabel activityCapLabel = new JLabel("Activity Capacity:");
        activityCapLabel.setFont(labelFont);
        formPanel.add(activityCapLabel);

        activityCapField = new JTextField();
        activityCapField.setFont(fieldFont);
        formPanel.add(activityCapField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(buttonFont);
        submitButton.setPreferredSize(new Dimension(120, 30));
        submitButton.addActionListener(e -> submitForm());
        buttonPanel.add(submitButton);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(buttonFont);
        backButton.setPreferredSize(new Dimension(180, 30));
        backButton.addActionListener(e -> dispose());
        buttonPanel.add(backButton);

        add(headingPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

    }

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


    private void submitForm() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel?sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false", "root", "Mysql@123");

            String query = "INSERT INTO locations (destination_name, activity_name, activity_cost, activity_cap, travel_package_id) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, destinationNameField.getText());
                preparedStatement.setString(2, activityNameField.getText());
                preparedStatement.setDouble(3, Double.parseDouble(activityCostField.getText()));
                preparedStatement.setInt(4, Integer.parseInt(activityCapField.getText()));

                int travelPackageId = getTravelPackageId(connection, (String) packageDropdown.getSelectedItem());
                preparedStatement.setInt(5, travelPackageId);

                if (!isActivityFull(connection, activityNameField.getText())) {
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                int locationId = generatedKeys.getInt(1);

                                // Save destination image to the database
                               /* if (selectedDestinationImageFile != null) {
                                    saveImageToDatabase(selectedDestinationImageFile, true, locationId);
                                }

                                // Save activity image to the database
                                if (selectedActivityImageFile != null) {
                                    saveImageToDatabase(selectedActivityImageFile, false, locationId);
                                }*/

                                JOptionPane.showMessageDialog(this, "Destination details saved successfully!");
                                dispose();
                                new Dashboard();
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Error: No rows affected. Data not saved.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Activity is already full. No more passengers can sign up.");
                }
            }
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: Unable to save destination details.");
        }
    }

    private boolean isActivityFull(Connection connection, String activityName) {
        String query = "SELECT COUNT(*) AS count FROM locations WHERE activity_name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, activityName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int currentCount = resultSet.getInt("count");
                    int capacity = Integer.parseInt(activityCapField.getText());
                    return currentCount >= capacity;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private int getTravelPackageId(Connection connection, String packageName) throws SQLException {
        String query = "SELECT id FROM travel_packages WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, packageName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        new AddDestination();
    }
}



