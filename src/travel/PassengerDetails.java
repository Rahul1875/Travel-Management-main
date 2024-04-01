package travel;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PassengerDetails extends JFrame {

    private JTextField nameField;
    private JTextField numberField;
    private JComboBox<String> typeDropdown;
    private List<JCheckBox> activityCheckboxes;
    private JButton computeBillButton;
    private JButton submitButton;
    private JButton backButton;

    private String travelPackageName;

    // doubt full
    private int passengerCapacity;
    private int passengersRegistered = 0;

    public PassengerDetails(String travelPackageName, int passengerCapacity) {
        this.travelPackageName = travelPackageName;
        this.passengerCapacity = passengerCapacity;
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headingLabel = new JLabel("Passenger Details for " + travelPackageName);
        headingLabel.setFont(new Font("Montserrat", Font.BOLD, 24));
        headingPanel.add(headingLabel);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(10, 3, 10, 10));

        Font labelFont = new Font("Montserrat", Font.BOLD, 16);
        Font fieldFont = new Font("Montserrat", Font.PLAIN, 16);
        Font buttonFont = new Font("Montserrat", Font.BOLD, 16);

        JLabel nameLabel = new JLabel("Passenger Name:");
        nameLabel.setFont(labelFont);
        formPanel.add(nameLabel);

        nameField = new JTextField();
        nameField.setFont(fieldFont);
        formPanel.add(nameField);

        JLabel numberLabel = new JLabel("Passenger Number:");
        numberLabel.setFont(labelFont);
        formPanel.add(numberLabel);

        numberField = new JTextField();
        numberField.setFont(fieldFont);
        formPanel.add(numberField);

        JLabel typeLabel = new JLabel("Passenger Type:");
        typeLabel.setFont(labelFont);
        formPanel.add(typeLabel);

        // Populate the dropdown with passenger types
        typeDropdown = new JComboBox<>(new String[]{"Standard", "Gold", "Premium"});
        typeDropdown.setFont(new Font("Montserrat", Font.PLAIN, 14));
        formPanel.add(typeDropdown);

        JLabel activityLabel = new JLabel("Activities:");
        activityLabel.setFont(labelFont);
        formPanel.add(activityLabel);

        activityCheckboxes = new ArrayList<>();
        List<String> activityNames = getActivityNames();
        for (String activityName : activityNames) {
            JCheckBox activityCheckbox = new JCheckBox(activityName);
            activityCheckbox.setFont(new Font("Montserrat", Font.PLAIN, 14));
            activityCheckboxes.add(activityCheckbox);
            formPanel.add(activityCheckbox);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        computeBillButton = new JButton("Compute Bill");
        computeBillButton.setFont(buttonFont);
        computeBillButton.setPreferredSize(new Dimension(150, 30));
        computeBillButton.addActionListener(e -> computeBill());
        buttonPanel.add(computeBillButton);

        submitButton = new JButton("Submit");
        submitButton.setFont(buttonFont);
        submitButton.setPreferredSize(new Dimension(150, 30));
        submitButton.addActionListener(e -> submitPassengerDetails());
        buttonPanel.add(submitButton);

        backButton = new JButton("Back to Booking");
        backButton.setFont(buttonFont);
        backButton.setPreferredSize(new Dimension(200, 30));
        backButton.addActionListener(e -> backToBooking());
        buttonPanel.add(backButton);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(headingPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Fetching activities associated with the selected travel package
    private List<String> getActivityNames() {
        List<String> activityNames = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123")) {
            String query = "SELECT DISTINCT l.activity_name FROM locations l INNER JOIN travel_packages tp ON l.travel_package_id = tp.id WHERE tp.name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, travelPackageName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        activityNames.add(resultSet.getString("activity_name"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activityNames;
    }

    //printing the matter on clicking computeBill
    private void computeBill() {
        String passengerName = nameField.getText();
        String passengerNumber = numberField.getText();
        String passengerType = (String) typeDropdown.getSelectedItem();
        double travelPackageCost = getTravelPackageCost(travelPackageName); // Fetch the cost of the travel package
        double totalActivityCost = getActivityCost();
        double totalBill = computePassengerBill(passengerType, travelPackageCost, totalActivityCost); // Include travel package cost
        JOptionPane.showMessageDialog(this, "Passenger: " + passengerName + "\nNumber: " + passengerNumber + "\nType: " + passengerType + "\nTotal Bill: " + totalBill);
    }

    //fetching the names of travel package from travel_package
    private double getTravelPackageCost(String packageName) {
        double packageCost = 0;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123")) {
            String query = "SELECT cost FROM travel_packages WHERE name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, packageName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        packageCost = resultSet.getDouble("cost");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return packageCost;
    }


    private double getActivityCost() {
        double totalCost = 0;
        for (JCheckBox checkbox : activityCheckboxes) {
            if (checkbox.isSelected()) {
                totalCost += getActivityCost(checkbox.getText());
            }
        }
        return totalCost;
    }

    //fetching the associated activity cost
    private double getActivityCost(String activityName) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123")) {
            String query = "SELECT activity_cost FROM locations WHERE activity_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, activityName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getDouble("activity_cost");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //computing the final bill for the passenger
    private double computePassengerBill(String passengerType, double travelPackageCost, double totalActivityCost) {
        switch (passengerType) {
            case "Standard":
                return travelPackageCost + totalActivityCost; // Include travel package cost
            case "Gold":
                return (travelPackageCost) + (totalActivityCost * 0.9 ); // 10% discount on total bill
            case "Premium":
                return travelPackageCost; // Only travel package cost for premium passengers
            default:
                return 0;
        }
    }


    //checking for duplicate number entry
    private boolean isDuplicateNumber(String passengerNumber) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123")) {
            String query = "SELECT COUNT(*) AS count FROM passengers WHERE passenger_number = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, passengerNumber);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt("count");
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    //validation checking and submitting
    private void submitPassengerDetails() {
        // Validate fields
        String passengerName = nameField.getText();
        String passengerNumber = numberField.getText();
        String passengerType = (String) typeDropdown.getSelectedItem();
        List<String> selectedActivities = getSelectedActivities();

        if (passengerName.isEmpty() || passengerNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both passenger name and number.");
            return;
        }

        // Validate phone number to be a 10-digit number
        if (!passengerNumber.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone number must be a 10-digit number.");
            return;
        }

        // Check for duplicate number
        if (isDuplicateNumber(passengerNumber)) {
            JOptionPane.showMessageDialog(this, "Duplicate passenger number detected. Please enter a unique number.");
            return;
        }

        // Insert passenger details into the database and get the generated passenger ID
        int passengerId = insertPassenger(passengerName, passengerNumber, passengerType);

        if (passengerId != 0) {
            // Insert passenger activities into the database
            insertPassengerActivities(selectedActivities, passengerId);

            // Update activity capacity
            updateActivityCapacity(selectedActivities);

            passengersRegistered++;

            if (passengersRegistered == passengerCapacity) {
                JOptionPane.showMessageDialog(this, "All passengers registered successfully.");
                backToBooking();
            } else {
                clearForm();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error saving passenger details.");
        }
    }

    //updating the activity cap afterwards
    private void updateActivityCapacity(List<String> selectedActivities) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123")) {
            String query = "UPDATE locations SET activity_cap = activity_cap - 1 WHERE location_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                for (String activityName : selectedActivities) {
                    int locationId = getLocationId(activityName);
                    preparedStatement.setInt(1, locationId);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //inserting the details of passenger in passengers table
    private int insertPassenger(String name, String number, String type) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123")) {
            String query = "INSERT INTO passengers (name, passenger_number, passenger_type) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, number);
                preparedStatement.setString(3, type);

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //inserting the data into passenger_activities table in db
    private void insertPassengerActivities(List<String> activityNames, int passengerId) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123")) {
            String query = "INSERT INTO passenger_activities (location_id, passenger_id) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                for (String activityName : activityNames) {
                    int locationId = getLocationId(activityName);
                    preparedStatement.setInt(1, locationId);
                    preparedStatement.setInt(2, passengerId);
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //foreign key exhange
    private int getLocationId(String activityName) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/travel", "root", "Mysql@123")) {
            String query = "SELECT location_id FROM locations WHERE activity_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, activityName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("location_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    private List<String> getSelectedActivities() {
        List<String> selectedActivities = new ArrayList<>();
        for (JCheckBox checkbox : activityCheckboxes) {
            if (checkbox.isSelected()) {
                selectedActivities.add(checkbox.getText());
            }
        }
        return selectedActivities;
    }

    private void clearForm() {
        nameField.setText("");
        numberField.setText("");
        typeDropdown.setSelectedIndex(0);
        for (JCheckBox checkbox : activityCheckboxes) {
            checkbox.setSelected(false);
        }
    }

    private void backToBooking() {
        dispose();
        EventQueue.invokeLater(() -> new BookPackage().setVisible(true));
    }

    public static void main(String[] args) {
        new PassengerDetails("", 0).setVisible(true);
    }
}
