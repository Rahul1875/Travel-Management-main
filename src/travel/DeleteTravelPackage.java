package travel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeleteTravelPackage extends JFrame {

    private JComboBox<String> packageDropdown;

    public DeleteTravelPackage() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headingLabel = new JLabel("Delete Travel Package");
        headingLabel.setFont(new Font("Montserrat", Font.BOLD, 24));
        headingPanel.add(headingLabel);

        JPanel formPanel = new JPanel(new GridLayout(5, 5, 5, 5));

        Font labelFont = new Font("Montserrat", Font.BOLD, 16);
        Font fieldFont = new Font("Montserrat", Font.PLAIN, 16);
        Font buttonFont = new Font("Montserrat", Font.BOLD, 16);

        JLabel packageLabel = new JLabel("Select Package:");
        packageLabel.setFont(labelFont);
        formPanel.add(packageLabel);

        packageDropdown = new JComboBox<>();
        packageDropdown.setFont(fieldFont);
        formPanel.add(packageDropdown);

        populatePackageDropdown();
        formPanel.add(new JPanel());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton deleteButton = new JButton("Delete Package");
        deleteButton.setFont(buttonFont);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDeleteConfirmation();
            }
        });
        buttonPanel.add(deleteButton);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(buttonFont);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Dashboard();
            }
        });
        buttonPanel.add(backButton);

        add(headingPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populatePackageDropdown() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123");

            String selectQuery = "SELECT id, name FROM travel_packages";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<String> packageNames = new ArrayList<>();

                    while (resultSet.next()) {
                        int packageId = resultSet.getInt("id");
                        String packageName = resultSet.getString("name");
                        packageNames.add(packageName);
                    }

                    packageDropdown.setModel(new DefaultComboBoxModel<>(packageNames.toArray(new String[0])));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: Unable to fetch package names.");
        }
    }

    private void showDeleteConfirmation() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the selected travel package?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            deletePackage();
        }
    }

    private void deletePackage() {
        try {
            String selectedPackageName = (String) packageDropdown.getSelectedItem();

            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123");

            String deleteQuery = "DELETE FROM travel_packages WHERE name = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, selectedPackageName);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Travel package deleted successfully!");
                    populatePackageDropdown(); // Refresh the dropdown after deletion
                } else {
                    JOptionPane.showMessageDialog(this, "No package found with the specified name.");
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: Unable to delete the travel package.");
        }
    }

    public static void main(String[] args) {
        new DeleteTravelPackage();
    }
}
