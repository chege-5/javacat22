import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class Registration extends JFrame {

    private JTextField nameField, mobileField;
    private JRadioButton maleButton, femaleButton;
    private JComboBox<String> dayBox, monthBox, yearBox;
    private JTextArea addressArea;
    private JCheckBox termsBox;
    private Connection connection;

    public Registration() {
        // Set up the frame
        setTitle("Registration Form");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Initialize database connection
        connectToDatabase();

        // Name Label and Text Field
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(50, 20, 80, 25);
        add(nameLabel);
        
        nameField = new JTextField();
        nameField.setBounds(150, 20, 165, 25);
        add(nameField);

        // Mobile Label and Text Field
        JLabel mobileLabel = new JLabel("Mobile");
        mobileLabel.setBounds(50, 50, 80, 25);
        add(mobileLabel);
        
        mobileField = new JTextField();
        mobileField.setBounds(150, 50, 165, 25);
        add(mobileField);

        // Gender Label and Radio Buttons
        JLabel genderLabel = new JLabel("Gender");
        genderLabel.setBounds(50, 80, 80, 25);
        add(genderLabel);
        
        maleButton = new JRadioButton("Male");
        maleButton.setBounds(150, 80, 75, 25);
        femaleButton = new JRadioButton("Female");
        femaleButton.setBounds(225, 80, 75, 25);

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        
        add(maleButton);
        add(femaleButton);

        // Date of Birth Label and ComboBoxes
        JLabel dobLabel = new JLabel("DOB");
        dobLabel.setBounds(50, 110, 80, 25);
        add(dobLabel);
        
        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) days[i - 1] = String.valueOf(i);
        dayBox = new JComboBox<>(days);
        dayBox.setBounds(150, 110, 50, 25);
        
        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        monthBox = new JComboBox<>(months);
        monthBox.setBounds(205, 110, 60, 25);
        
        String[] years = new String[100];
        for (int i = 1920; i <= 2019; i++) years[i - 1920] = String.valueOf(i);
        yearBox = new JComboBox<>(years);
        yearBox.setBounds(270, 110, 60, 25);
        
        add(dayBox);
        add(monthBox);
        add(yearBox);

        // Address Label and Text Area
        JLabel addressLabel = new JLabel("Address");
        addressLabel.setBounds(50, 140, 80, 25);
        add(addressLabel);
        
        addressArea = new JTextArea();
        addressArea.setBounds(150, 140, 165, 50);
        add(addressArea);

        // Terms Checkbox
        termsBox = new JCheckBox("Accept Terms And Conditions.");
        termsBox.setBounds(50, 200, 250, 25);
        add(termsBox);

        // Submit and Reset Buttons
        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(100, 230, 80, 25);
        add(submitButton);

        JButton resetButton = new JButton("Reset");
        resetButton.setBounds(200, 230, 80, 25);
        add(resetButton);
        
        JButton viewButton = new JButton("View");
        viewButton.setBounds(300, 230, 80, 25);
        add(viewButton);

        // Action Listeners
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (termsBox.isSelected()) {
                    insertData();
                } else {
                    JOptionPane.showMessageDialog(null, "Please accept the terms and conditions.");
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ViewData();
            }
        });
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/registration1", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed.");
        }
    }

    private void insertData() {
        String name = nameField.getText();
        String mobile = mobileField.getText();
        String gender = maleButton.isSelected() ? "Male" : "Female";
        String dob = yearBox.getSelectedItem() + "-" + monthBox.getSelectedItem() + "-" + dayBox.getSelectedItem();
        String address = addressArea.getText();

        try {
            String query = "INSERT INTO users (name, mobile, gender, dob, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, name);
            pst.setString(2, mobile);
            pst.setString(3, gender);
            pst.setString(4, dob);  // Using YYYY-MM-DD format
            pst.setString(5, address);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data inserted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to insert data.");
        }
    }

    private void resetForm() {
        nameField.setText("");
        mobileField.setText("");
        maleButton.setSelected(false);
        femaleButton.setSelected(false);
        dayBox.setSelectedIndex(0);
        monthBox.setSelectedIndex(0);
        yearBox.setSelectedIndex(0);
        addressArea.setText("");
        termsBox.setSelected(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Registration form = new Registration();
            form.setVisible(true);
        });
    }
}

// ViewData class to display data in a new window
class ViewData extends JFrame {

    public ViewData() {
        setTitle("View Data");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create column names for the table
        String[] columnNames = {"Name", "Mobile", "Gender", "DOB", "Address"};

        // Create a table model with the column names
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        // Create the JTable with the table model
        JTable dataTable = new JTable(tableModel);
        
        // Fetch and display data from the database
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/registration1", "root", "")) {
            String query = "SELECT * FROM users";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Populate table rows with data from the ResultSet
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String mobile = resultSet.getString("mobile");
                String gender = resultSet.getString("gender");
                String dob = resultSet.getString("dob");
                String address = resultSet.getString("address");

                // Add a row to the table model
                tableModel.addRow(new Object[]{name, mobile, gender, dob, address});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve data.");
        }

        // Add the JTable inside a JScrollPane to handle scrolling
        add(new JScrollPane(dataTable), BorderLayout.CENTER);

        setVisible(true);
    }
}
