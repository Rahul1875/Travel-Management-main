package travel;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewPackage extends JFrame {

    private int currentIndex;

    public ViewPackage() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Travel Packages");

        Font montserratFontBold20 = new Font("Montserrat", Font.BOLD, 20);
        Font montserratFontBold16 = new Font("Montserrat", Font.BOLD, 16);

        JLabel headingLabel = new JLabel("Travel Packages", SwingConstants.CENTER);
        headingLabel.setFont(montserratFontBold20);

        JTextArea packageDetailsArea = new JTextArea();
        packageDetailsArea.setFont(montserratFontBold16);
        packageDetailsArea.setEditable(false);
        packageDetailsArea.setLineWrap(true);
        packageDetailsArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(packageDetailsArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JButton nextButton = new JButton("Next");
        JButton backButton = new JButton("Back to Dashboard");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(headingLabel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(centerPanel);

        nextButton.addActionListener(e -> {
            currentIndex = (currentIndex + 1) % getPackageCount();
            showPackageDetails(packageDetailsArea);
        });

        backButton.addActionListener(e -> {
            dispose();
            new Dashboard();
        });

        setLocationRelativeTo(null);
        setVisible(true);

        showPackageDetails(packageDetailsArea);
    }

    private void showPackageDetails(JTextArea packageDetailsArea) {
        String packageDetails = getPackageDetails(currentIndex);
        packageDetailsArea.setText(packageDetails);
    }

    private int getPackageCount() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123")) {
            String query = "SELECT COUNT(*) FROM travel_packages";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getPackageDetails(int index) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123")) {
            String query = "SELECT * FROM travel_packages LIMIT ?, 1";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, index);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return buildPackageDetails(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "No package details available";
    }

    private String buildPackageDetails(ResultSet resultSet) throws SQLException {
        return "Package Name: " + resultSet.getString("name") + "\n\n" +
                "Passenger Capacity: " + resultSet.getInt("passenger_capacity") + "\n\n" +
                "Itinerary: " + resultSet.getString("itinerary") + "\n\n" +
                "Description: " + resultSet.getString("description") + "\n\n" +
                "Cost: Rs." + resultSet.getDouble("cost")+"\n\n"+
                "Note: The Price doesn't include the cost of activities for more details visit Destinations/Activities Page!";
    }

    public static void main(String[] args) {
        new ViewPackage();
    }
}
