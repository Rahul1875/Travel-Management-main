package travel;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PassengerListDetails extends JFrame {

    private JComboBox<String> packageDropdown;

    public PassengerListDetails() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Passenger List Details");
        initializeComponents();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeComponents() {
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headingLabel = new JLabel("Passenger List Details");
        headingLabel.setFont(new Font("Montserrat", Font.BOLD, 20));
        headingPanel.add(headingLabel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 2, 10, 10));

        Font labelFont = new Font("Montserrat", Font.BOLD, 14);
        Font buttonFont = new Font("Montserrat", Font.BOLD, 16);

        JLabel packageLabel = new JLabel("Select Travel Package:");
        packageLabel.setFont(labelFont);
        formPanel.add(packageLabel);

        packageDropdown = new JComboBox<>(getTravelPackageNames());
        packageDropdown.setFont(new Font("Montserrat", Font.PLAIN, 14));
        formPanel.add(packageDropdown);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton printListButton = new JButton("Print Passenger List");
        printListButton.setFont(buttonFont);
        printListButton.setPreferredSize(new Dimension(200, 40));
        printListButton.addActionListener(e -> printPassengerList());
        buttonPanel.add(printListButton);

        JButton printDetailsButton = new JButton("Print Passenger Details");
        printDetailsButton.setFont(buttonFont);
        printDetailsButton.setPreferredSize(new Dimension(200, 40));
        printDetailsButton.addActionListener(e -> printPassengerDetails());
        buttonPanel.add(printDetailsButton);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(buttonFont);
        backButton.setPreferredSize(new Dimension(200, 40));
        backButton.addActionListener(e -> dispose());
        buttonPanel.add(backButton);

        setLayout(new BorderLayout());
        add(headingPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private String[] getTravelPackageNames() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123")) {
            String query = "SELECT name FROM travel_packages";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<String> packageNames = new ArrayList<>();
                    while (resultSet.next()) {
                        packageNames.add(resultSet.getString("name"));
                    }
                    return packageNames.toArray(new String[0]);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    private void printPassengerList() {
        String packageName = (String) packageDropdown.getSelectedItem();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123")) {
            String packageQuery = "SELECT * FROM travel_packages WHERE name = ?";
            try (PreparedStatement packageStatement = connection.prepareStatement(packageQuery)) {
                packageStatement.setString(1, packageName);
                try (ResultSet packageResultSet = packageStatement.executeQuery()) {
                    if (packageResultSet.next()) {
                        int packageId = packageResultSet.getInt("id");
                        int passengerCapacity = packageResultSet.getInt("passenger_capacity");

                        String passengerListQuery = "SELECT * FROM passengers WHERE passenger_id IN (SELECT passenger_id FROM passenger_activities WHERE location_id IN (SELECT location_id FROM locations WHERE travel_package_id = ?))";
                        try (PreparedStatement passengerListStatement = connection.prepareStatement(passengerListQuery)) {
                            passengerListStatement.setInt(1, packageId);
                            try (ResultSet passengerListResultSet = passengerListStatement.executeQuery()) {
                                StringBuilder result = new StringBuilder();
                                result.append("Package Name: ").append(packageName).append("\n");
                                result.append("Passenger Capacity: ").append(passengerCapacity).append("\n");
                                result.append("Enrolled Passengers: ").append("\n");

                                while (passengerListResultSet.next()) {
                                    result.append("Name: ").append(passengerListResultSet.getString("name")).append(", ");
                                    result.append("Number: ").append(passengerListResultSet.getString("passenger_number")).append("\n");
                                }

                                JOptionPane.showMessageDialog(this, result.toString());
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void printPassengerDetails() {
        String packageName = (String) packageDropdown.getSelectedItem();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123")) {
            String packageQuery = "SELECT * FROM travel_packages WHERE name = ?";
            try (PreparedStatement packageStatement = connection.prepareStatement(packageQuery)) {
                packageStatement.setString(1, packageName);
                try (ResultSet packageResultSet = packageStatement.executeQuery()) {
                    if (packageResultSet.next()) {
                        int packageId = packageResultSet.getInt("id");

                        String passengerNumber = JOptionPane.showInputDialog("Enter Passenger Number:");
                        String passengerDetailsQuery = "SELECT * FROM passengers WHERE passenger_number = ? AND passenger_id IN (SELECT passenger_id FROM passenger_activities WHERE location_id IN (SELECT location_id FROM locations WHERE travel_package_id = ?))";
                        try (PreparedStatement passengerDetailsStatement = connection.prepareStatement(passengerDetailsQuery)) {
                            passengerDetailsStatement.setString(1, passengerNumber);
                            passengerDetailsStatement.setInt(2, packageId);
                            try (ResultSet passengerDetailsResultSet = passengerDetailsStatement.executeQuery()) {
                                if (passengerDetailsResultSet.next()) {
                                    StringBuilder result = new StringBuilder();
                                    result.append("Passenger Name: ").append(passengerDetailsResultSet.getString("name")).append("\n");
                                    result.append("Passenger Number: ").append(passengerDetailsResultSet.getString("passenger_number")).append("\n");

                                    int passengerId = passengerDetailsResultSet.getInt("passenger_id");
                                    String activitiesQuery = "SELECT * FROM locations WHERE location_id IN (SELECT location_id FROM passenger_activities WHERE passenger_id = ?)";
                                    try (PreparedStatement activitiesStatement = connection.prepareStatement(activitiesQuery)) {
                                        activitiesStatement.setInt(1, passengerId);
                                        try (ResultSet activitiesResultSet = activitiesStatement.executeQuery()) {
                                            result.append("Activities:").append("\n");
                                            int activityCount = 1;
                                            while (activitiesResultSet.next()) {
                                                result.append(activityCount).append(". ");
                                                result.append("Destination: ").append(activitiesResultSet.getString("destination_name")).append(", ");
                                                result.append("Activity: ").append(activitiesResultSet.getString("activity_name")).append(", ");
                                                result.append("Price: Rs").append(activitiesResultSet.getDouble("activity_cost")).append("\n");
                                                activityCount++;
                                            }
                                        }
                                    }
                                    JOptionPane.showMessageDialog(this, result.toString());
                                } else {
                                    JOptionPane.showMessageDialog(this, "Passenger not found for the given number in the selected package.");
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PassengerListDetails());
    }
}
