package travel;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class Dashboard extends JFrame {
    Dashboard() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(null);

        // Main Panel
        JPanel p1 = new JPanel();
        p1.setLayout(null);
        p1.setBackground(Color.darkGray);
        p1.setBounds(45, 10, 1200, 75);
        add(p1);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/Dashboard.png"));
        Image i2 = i1.getImage().getScaledInstance(70, 70, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel icon = new JLabel(i3);
        icon.setBounds(5, 0, 70, 70);
        p1.add(icon);

        // Dashboard label
        JLabel heading = new JLabel("NYMBLE TRAVEL");
        heading.setBounds(icon.getX() + icon.getWidth() + 5, 12, 300, 40);
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("Montserrat", Font.BOLD, 30));
        p1.add(heading);

        // Center Text
        JLabel centerTextLine1 = new JLabel("Choose your next vacation with us,");
        centerTextLine1.setFont(new Font("Montserrat", Font.BOLD, 47));
        centerTextLine1.setHorizontalAlignment(SwingConstants.CENTER);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        centerTextLine1.setBounds((screenSize.width - centerTextLine1.getPreferredSize().width) / 2,
                (screenSize.height - centerTextLine1.getPreferredSize().height * 2) / 3,
                centerTextLine1.getPreferredSize().width,
                centerTextLine1.getPreferredSize().height);
        add(centerTextLine1);

        JLabel centerTextLine2 = new JLabel("minus the hassle");
        centerTextLine2.setFont(new Font("Montserrat", Font.BOLD, 47));
        centerTextLine2.setHorizontalAlignment(SwingConstants.CENTER);
        centerTextLine2.setBounds((screenSize.width - centerTextLine2.getPreferredSize().width) / 2,
                (screenSize.height - centerTextLine2.getPreferredSize().height * 2) / 3 + centerTextLine1.getPreferredSize().height,
                centerTextLine2.getPreferredSize().width,
                centerTextLine2.getPreferredSize().height);
        add(centerTextLine2);

        JButton bookPackageButton = new JButton("Book Package");
        bookPackageButton.setBounds((screenSize.width - 200) / 2,
                (screenSize.height - centerTextLine2.getPreferredSize().height) / 3 + centerTextLine2.getPreferredSize().height * 2,
                200, 42);
        bookPackageButton.setBackground(Color.getHSBColor(56,89,139));
        bookPackageButton.setForeground(Color.black);
        bookPackageButton.setFont(new Font("Montserrat", Font.PLAIN, 19));
        bookPackageButton.addActionListener(e -> {
            try {
                openBookPackageWindow();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        add(bookPackageButton);

        // Creating a dropdown button for Travel Packages
        JComboBox<String> travelDropdown = new JComboBox<>(new String[]{"Add Travel Package", "Book Travel Package", "View Travel Packages", "Delete Travel Package"});
        travelDropdown.setBounds(heading.getX() + heading.getWidth() + 5, heading.getY(), 230, 42);
        travelDropdown.setBackground(Color.getHSBColor(56,89,139));
        travelDropdown.setForeground(Color.black);
        travelDropdown.setFont(new Font("Montserrat", Font.PLAIN, 18));
        p1.add(travelDropdown);

        // action listener for travel dropdown
        travelDropdown.addActionListener(e -> {
            String selectedOption = (String) travelDropdown.getSelectedItem();
            if ("Add Travel Package".equals(selectedOption)) {
                openAddPackageWindow();
            }else if ("Book Travel Package".equals(selectedOption)) {
                try {
                    openBookPackageWindow();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else if ("View Travel Packages".equals(selectedOption)) {
                new ViewPackage();
            } else if ("Delete Travel Package".equals(selectedOption)) {
                openDeletePackageWindow();
            }
        });

        // Destinations/Activities button - Add/view
        JComboBox<String> destDropdown = new JComboBox<>(new String[]{"Add Destination/Activities", "View Destinations/Activities"});
        destDropdown.setBounds(travelDropdown.getX() + travelDropdown.getWidth() + 5, heading.getY(), 280, 42);
        destDropdown.setBackground(Color.getHSBColor(56,89,139));
        destDropdown.setForeground(Color.black);
        destDropdown.setFont(new Font("Montserrat", Font.PLAIN, 18));
        p1.add(destDropdown);

        destDropdown.addActionListener(e -> {
            String selectedOption = (String) destDropdown.getSelectedItem();
            if ("Add Destination/Activities".equals(selectedOption)) {
                openAddDestinationWindow();
            } else if ("View Destinations/Activities".equals(selectedOption)) {
                openViewDestinationWindow();
            }
        });

        // Passengers button (Details of all passengers)
        JButton passengerListButton = new JButton("View All Passengers");
        passengerListButton.setBounds(destDropdown.getX() + destDropdown.getWidth() + 5, heading.getY(), 200, 42);
        passengerListButton.setBackground(Color.getHSBColor(56,89,139));
        passengerListButton.setForeground(Color.black);
        passengerListButton.setFont(new Font("Montserrat", Font.PLAIN, 18));
        passengerListButton.setMargin(new Insets(0, 0, 0, 0));
        p1.add(passengerListButton);

        passengerListButton.addActionListener(e -> openPassengerListDetails());


        ImageIcon i4 = new ImageIcon(ClassLoader.getSystemResource("icons/home.jpg"));
        Image i5 = i4.getImage().getScaledInstance(1700, 900, Image.SCALE_DEFAULT);
        ImageIcon i6 = new ImageIcon(i5);
        JLabel image = new JLabel(i6);
        image.setBounds(0, 0, 1600, 900);
        add(image);

        setVisible(true);
    }


    //calling different pages through buttons
    private void openAddPackageWindow() {
        AddPackage addPackagePage = new AddPackage();
        addPackagePage.setVisible(true);
    }

    private void openDeletePackageWindow() {
        DeleteTravelPackage deletePackagePage = new DeleteTravelPackage();
        deletePackagePage.setVisible(true);
    }

    private void openBookPackageWindow() throws SQLException {
        BookPackage bookPackagePage = new BookPackage();
        bookPackagePage.setVisible(true);

    }

    private void openViewDestinationWindow() {
        Viewdestination viewDestinationPage = new Viewdestination();
        viewDestinationPage.setVisible(true);
    }

    private void openAddDestinationWindow(){
        AddDestination addDestinationPage = new AddDestination();
        addDestinationPage.setVisible(true);
    }
    private void openPassengerListDetails() {
        PassengerListDetails passengerListDetailsPage = new PassengerListDetails();
        passengerListDetailsPage.setVisible(true);
    }
    public static void main(String[] args) {
        new Dashboard();
    }
}




