package travel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddPackage extends JFrame {

    private JTextField packageNameField;
    private JTextField capacityField;
    private JTextField itineraryField;
    private JTextField descriptionField;
    private JTextField costField;

    public AddPackage() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headingLabel = new JLabel("Add Travel Package");
        headingLabel.setFont(new Font("Montserrat", Font.BOLD, 20));
        headingPanel.add(headingLabel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(8, 2, 10, 10));

        Font labelFont = new Font("Montserrat", Font.BOLD, 14);
        Font fieldFont = new Font("Montserrat", Font.PLAIN, 16);
        Font buttonFont = new Font("Montserrat", Font.BOLD, 16);

        JLabel packageNameLabel = new JLabel("Package Name:");
        packageNameLabel.setFont(labelFont);
        formPanel.add(packageNameLabel);

        packageNameField = new JTextField();
        packageNameField.setFont(fieldFont);
        formPanel.add(packageNameField);

        JLabel capacityLabel = new JLabel("Passenger Capacity:");
        capacityLabel.setFont(labelFont);
        formPanel.add(capacityLabel);

        capacityField = new JTextField();
        capacityField.setFont(fieldFont);
        formPanel.add(capacityField);

        JLabel itineraryLabel = new JLabel("Itinerary:");
        itineraryLabel.setFont(labelFont);
        formPanel.add(itineraryLabel);

        itineraryField = new JTextField();
        itineraryField.setFont(fieldFont);
        formPanel.add(itineraryField);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(labelFont);
        formPanel.add(descriptionLabel);

        descriptionField = new JTextField();
        descriptionField.setFont(fieldFont);
        formPanel.add(descriptionField);

        JLabel costLabel = new JLabel("Cost:");
        costLabel.setFont(labelFont);
        formPanel.add(costLabel);

        costField = new JTextField();
        costField.setFont(fieldFont);
        formPanel.add(costField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(buttonFont);
        submitButton.setPreferredSize(new Dimension(120, 40));
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitForm();
            }
        });
        buttonPanel.add(submitButton);

        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(buttonFont);
        backButton.setPreferredSize(new Dimension(180, 40));
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


    //submission form to insert all data back to the travel_package db
    private void submitForm() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123")) {
            String query = "INSERT INTO travel_packages (name, passenger_capacity, itinerary, description, cost) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, packageNameField.getText());
                preparedStatement.setInt(2, Integer.parseInt(capacityField.getText()));
                preparedStatement.setString(3, itineraryField.getText());
                preparedStatement.setString(4, descriptionField.getText());
                preparedStatement.setDouble(5, Double.parseDouble(costField.getText()));

                preparedStatement.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Travel package details saved successfully!");

            dispose();
            new Dashboard();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: Unable to save travel package details.");
        }
    }

    public static void main(String[] args) {
        new AddPackage();
    }
}
