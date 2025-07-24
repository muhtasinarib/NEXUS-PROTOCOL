// Importing Java packages needed for file handling, date operations, collections, and pattern matching
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

// Main class for the Blood Bank Management System, which manages blood donations, inventory, and user roles
public class BloodBankManagementSystem {
    // Scanner object to read user input from the console; shared across the program
    private static final Scanner scanner = new Scanner(System.in);

    // Main method: the starting point of the program
    public static void main(String[] args) {
        // Create CSV files to store data (like donor info, inventory, etc.)
        FileHandler.createCSVFiles();
        // Show a welcome message to the user
        displayWelcomeBanner();

        // Keep the program running in a loop to show the menu repeatedly
        while (true) {
            // Display the main menu with options for different user roles
            displayMainMenu();
            // Get the user's choice (1, 2, 3, or 4)
            String choice = scanner.nextLine();

            // Handle the user's choice using a switch statement
            switch (choice) {
                case "1" -> handleAdministratorAccess(); // Go to admin operations
                case "2" -> handleDonorOperations();    // Go to donor operations
                case "3" -> handleRecipientOperations(); // Go to recipient operations
                case "4" -> {
                    // Exit the program after closing the scanner and showing a goodbye message
                    try (scanner) {
                        displayExitBanner();
                    }
                    System.exit(0);
                }
                default -> displayInvalidInputError("Please enter a number between 1 and 4."); // Show error for invalid input
            }
        }
    }

    // Shows a welcome message when the program starts
    private static void displayWelcomeBanner() {
        System.out.println("====================================================================");
        System.out.println("            BLOOD BANK MANAGEMENT SYSTEM - LIFE SAVER              ");
        System.out.println("====================================================================");
    }

    // Displays the main menu with options for different user roles
    private static void displayMainMenu() {
        System.out.println("\n==================== MAIN OPERATION MENU =======================");
        System.out.println("Please select your role:");
        System.out.println("  1. System Administrator");
        System.out.println("  2. Blood Donor");
        System.out.println("  3. Blood Recipient");
        System.out.println("  4. Exit Application");
        System.out.print("Enter your choice (1-4): ");
    }

    // Shows a goodbye message when the user exits the program
    private static void displayExitBanner() {
        System.out.println("\n====================================================================");
        System.out.println("  THANK YOU FOR USING LIFE SAVER BLOOD BANK MANAGEMENT SYSTEM!");
        System.out.println("                  YOUR CONTRIBUTION SAVES LIVES!                  ");
        System.out.println("====================================================================");
    }

    // Displays an error message when the user enters invalid input
    private static void displayInvalidInputError(String message) {
        System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("   " + message);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    // Manages admin access, including first-time admin setup and login
    private static void handleAdministratorAccess() {
        // Check if there are no admins registered yet
        if (FileHandler.isUsersFileEmpty()) {
            System.out.println("\n--------------------------------------------------------------------");
            System.out.println("  NO ADMINISTRATORS REGISTERED. CREATING FIRST ADMINISTRATOR ACCOUNT");
            System.out.println("--------------------------------------------------------------------");
            registerFirstAdministrator(); // Create the first admin account
        } else {
            // Ask if the user is an existing admin
            System.out.print("\nAre you an existing administrator? (YES/NO): ");
            String existing = scanner.nextLine();
            if (existing.equalsIgnoreCase("YES")) {
                authenticateAdministrator(); // Log in the existing admin
            } else {
                // Inform that only existing admins can create new admin accounts
                System.out.println("\n***************************************************************");
                System.out.println("  NEW ADMINISTRATORS CAN ONLY BE CREATED BY EXISTING ADMINS.");
                System.out.println("  PLEASE LOGIN WITH AN EXISTING ADMINISTRATOR ACCOUNT.");
                System.out.println("***************************************************************");
            }
        }
    }

    // Creates the first admin account with a secure ID and password
    private static void registerFirstAdministrator() {
        // Generate a unique admin ID using a random UUID
        String adminId = "ADMIN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.println("Your system-generated Administrator ID: " + adminId);

        // Ask for a password and confirm it
        System.out.print("Create your secure password: ");
        String password = scanner.nextLine();
        System.out.print("Confirm your password: ");
        String confirmPassword = scanner.nextLine();

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            displayInvalidInputError("PASSWORD MISMATCH. ADMINISTRATOR REGISTRATION FAILED.");
            return;
        }

        // Generate a 6-digit OTP for extra security
        String otp = generateRandomOTP();
        System.out.println("SECURITY VERIFICATION: Your OTP is " + otp);
        System.out.print("Enter OTP to complete registration: ");
        String enteredOtp = scanner.nextLine();

        // Verify the OTP
        if (!enteredOtp.equals(otp)) {
            displayInvalidInputError("INVALID OTP. ADMINISTRATOR REGISTRATION ABORTED.");
            return;
        }

        // Save the admin's ID and password to the users file
        FileHandler.saveUser(adminId, "Administrator", password);
        System.out.println("\n***************************************************************");
        System.out.println("  SUCCESS! ADMINISTRATOR ACCOUNT CREATED:");
        System.out.println("  ID: " + adminId);
        System.out.println("  PLEASE SECURELY STORE YOUR CREDENTIALS");
        System.out.println("***************************************************************");
        FileHandler.logAction(adminId, "Administrator", "First Admin Setup", "Initial administrator registration");
    }

    // Logs in an existing admin by checking their ID and password
    private static void authenticateAdministrator() {
        System.out.print("\nEnter Administrator ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        // Create an Admin object and check credentials
        Admin admin = new Admin(id, password);
        if (admin.authenticate()) {
            System.out.println("\n***************************************************************");
            System.out.println("  ACCESS GRANTED. WELCOME, SYSTEM ADMINISTRATOR!");
            System.out.println("***************************************************************");
            administratorMenu(admin); // Show the admin menu
        } else {
            displayInvalidInputError("ACCESS DENIED. INVALID CREDENTIALS. PLEASE TRY AGAIN.");
        }
    }

    // Shows the admin menu and handles admin tasks
    private static void administratorMenu(Admin admin) {
        while (true) {
            // Display admin menu options
            System.out.println("\n================ ADMINISTRATOR CONTROL PANEL =================");
            System.out.println("  1. View Blood Inventory Status");
            System.out.println("  2. Update Blood Inventory");
            System.out.println("  3. Process Blood Type Test Requests");
            System.out.println("  4. View System Reports and Analytics");
            System.out.println("  5. Create Data Backup");
            System.out.println("  6. View All Registered Donors");
            System.out.println("  7. View All Registered Recipients");
            System.out.println("  8. Reserve Blood Units");
            System.out.println("  9. Manage Blood Availability");
            System.out.println(" 10. Register New Administrator");
            System.out.println(" 11. Logout and Return to Main Menu");
            System.out.print("Enter your command (1-11): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> admin.viewInventory(); // Show current blood stock
                case "2" -> {
                    // Add or update blood units in the inventory
                    System.out.print("Enter Blood Type (e.g., O+): ");
                    String bloodType = scanner.nextLine();
                    System.out.print("Enter Blood Component (Whole Blood/Plasma/Platelets): ");
                    String component = scanner.nextLine();
                    System.out.print("Enter Number of Units: ");
                    int units = getValidIntegerInput();
                    System.out.print("Enter Expiration Date (YYYY-MM-DD): ");
                    String expDate = scanner.nextLine();
                    admin.updateInventory(bloodType, component, units, expDate);
                }
                case "3" -> admin.processTestRequests(); // Handle pending blood type tests
                case "4" -> admin.viewReports(); // Show system statistics
                case "5" -> admin.backupData(); // Save a backup of all data
                case "6" -> admin.viewAllDonors(); // List all donors
                case "7" -> admin.viewAllRecipients(); // List all recipients
                case "8" -> {
                    // Reserve blood units for future use
                    System.out.print("Enter Blood Type to Reserve: ");
                    String reserveBloodType = scanner.nextLine();
                    System.out.print("Enter Blood Component: ");
                    String reserveComponent = scanner.nextLine();
                    System.out.print("Enter Units to Reserve: ");
                    int reserveUnits = getValidIntegerInput();
                    admin.reserveBlood(reserveBloodType, reserveComponent, reserveUnits);
                }
                case "9" -> {
                    // Manage blood stock (block specific units, types, or all)
                    System.out.println("Blood Management Options:");
                    System.out.println("  1. Block Specific Units");
                    System.out.println("  2. Block Entire Blood Type");
                    System.out.println("  3. Block All Blood Stock");
                    System.out.print("Select option (1-3): ");
                    String blockOption = scanner.nextLine();
                    switch (blockOption) {
                        case "1" -> {
                            // Block a specific number of units
                            System.out.print("Enter Blood Type: ");
                            String blockBloodType = scanner.nextLine();
                            System.out.print("Enter Blood Component: ");
                            String blockComponent = scanner.nextLine();
                            System.out.print("Enter Units to Block: ");
                            int blockUnits = getValidIntegerInput();
                            admin.blockBlood(blockBloodType, blockComponent, blockUnits);
                        }
                        case "2" -> {
                            // Block all units of a specific blood type
                            System.out.print("Enter Blood Type: ");
                            String blockType = scanner.nextLine();
                            admin.blockBloodType(blockType);
                        }
                        case "3" -> {
                            // Block the entire blood inventory
                            admin.blockEntireStock();
                        }
                        default -> displayInvalidInputError("Invalid block operation selection.");
                    }
                }
                case "10" -> admin.registerNewAdministrator(); // Create a new admin account
                case "11" -> {
                    System.out.println("Logging out from administrator account...");
                    return; // Go back to the main menu
                }
                default -> displayInvalidInputError("Invalid command selection. Please enter a number between 1 and 11.");
            }
        }
    }

    // Manages donor operations like registration and login
    private static void handleDonorOperations() {
        System.out.print("\nAre you an existing donor? (YES/NO): ");
        String existing = scanner.nextLine();
        Donor donor;
        String id;

        if (existing.equalsIgnoreCase("YES")) {
            // Log in an existing donor
            System.out.print("Enter Donor ID: ");
            id = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();
            if (FileHandler.authenticateUser(id, "Donor", password)) {
                donor = FileHandler.loadDonor(id);
                if (donor == null) {
                    displayInvalidInputError("Donor profile not found. Please register again.");
                    return;
                }
                System.out.println("\n***************************************************************");
                System.out.println("  ACCESS GRANTED. WELCOME, DONOR " + donor.getName() + "!");
                System.out.println("***************************************************************");
            } else {
                displayInvalidInputError("Invalid Donor ID or password. Try again or register.");
                return;
            }
        } else {
            // Register a new donor
            System.out.println("\n--------------------------------------------------------------------");
            System.out.println("  REGISTERING NEW DONOR");
            System.out.println("--------------------------------------------------------------------");
            id = "DONOR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            System.out.println("Your system-generated Donor ID: " + id);
            System.out.print("Enter Full Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Age: ");
            int age = getValidIntegerInput();
            System.out.print("Enter Blood Type (e.g., A+, UNKNOWN): ");
            String bloodType = scanner.nextLine();
            System.out.print("Enter Contact (10-digit phone or email): ");
            String contact = scanner.nextLine();
            System.out.print("Create Password: ");
            String password = scanner.nextLine();
            System.out.print("Confirm Password: ");
            String confirmPassword = scanner.nextLine();

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                displayInvalidInputError("PASSWORD MISMATCH. DONOR REGISTRATION FAILED.");
                return;
            }

            // Generate and verify OTP for security
            String otp = generateRandomOTP();
            System.out.println("SECURITY VERIFICATION: Your OTP is " + otp);
            System.out.print("Enter OTP to complete registration: ");
            String enteredOtp = scanner.nextLine();

            if (!enteredOtp.equals(otp)) {
                displayInvalidInputError("INVALID OTP. DONOR REGISTRATION ABORTED.");
                return;
            }

            // Create a new donor object
            donor = new Donor(id, name, age, bloodType, contact, null);
            System.out.print("Do you want to donate blood now or enroll in the donor list? (DONATE/ENROLL): ");
            String donationChoice = scanner.nextLine();
            if (donationChoice.equalsIgnoreCase("DONATE")) {
                if (donor.checkDonationEligibility()) {
                    System.out.print("Enter Donation Date (YYYY-MM-DD): ");
                    String date = scanner.nextLine();
                    donor.scheduleDonation(date);
                } else {
                    System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    System.out.println("   NOT ELIGIBLE TO DONATE YET. ENROLLED IN DONOR LIST.");
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }
            } else if (donationChoice.equalsIgnoreCase("ENROLL")) {
                System.out.println("\nYou have been enrolled in the donor list. Recipients may contact you when blood is needed.");
            } else {
                System.out.println("\nInvalid choice. Defaulting to enrollment in the donor list.");
            }
            donor.registerInSystem(); // Save donor to the system
            FileHandler.saveUser(id, "Donor", password); // Save credentials
            System.out.println("\n***************************************************************");
            System.out.println("  SUCCESS! DONOR ACCOUNT CREATED:");
            System.out.println("  ID: " + id);
            System.out.println("  PLEASE SECURELY STORE YOUR CREDENTIALS");
            System.out.println("***************************************************************");
        }

        donorMenu(donor); // Show the donor menu
    }

    // Shows the donor menu and handles donor tasks
    private static void donorMenu(Donor donor) {
        while (true) {
            System.out.println("\n================ DONOR DASHBOARD =================");
            System.out.println("  1. Request Blood Type Test");
            System.out.println("  2. Schedule Blood Donation");
            System.out.println("  3. View Donation History");
            System.out.println("  4. View Blood Test Report");
            System.out.println("  5. Logout and Return to Main Menu");
            System.out.print("Enter your command (1-5): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> donor.requestBloodTypeTest(); // Request a test to know blood type
                case "2" -> {
                    // Schedule a donation if the donor hasn't donated recently
                    if (donor.checkDonationEligibility()) {
                        System.out.print("Enter Donation Date (YYYY-MM-DD): ");
                        String date = scanner.nextLine();
                        donor.scheduleDonation(date);
                    } else {
                        displayInvalidInputError("NOT ELIGIBLE TO DONATE YET. MUST WAIT 90 DAYS SINCE LAST DONATION.");
                    }
                }
                case "3" -> donor.viewDonationHistory(); // Show past donations
                case "4" -> donor.viewBloodTestReportHistory(); // Show blood test results
                case "5" -> {
                    System.out.println("Logging out from donor account...");
                    return; // Go back to the main menu
                }
                default -> displayInvalidInputError("Invalid command selection. Please enter a number between 1 and 5.");
            }
        }
    }

    // Manages recipient operations like registration and login
    private static void handleRecipientOperations() {
        System.out.print("\nAre you an existing recipient? (YES/NO): ");
        String existing = scanner.nextLine();
        String id;
        Recipient recipient;

        if (existing.equalsIgnoreCase("YES")) {
            // Log in an existing recipient
            System.out.print("Enter Recipient ID: ");
            id = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();
            if (FileHandler.authenticateUser(id, "Recipient", password)) {
                recipient = FileHandler.loadRecipient(id);
                if (recipient == null) {
                    displayInvalidInputError("Recipient profile not found. Please register again.");
                    return;
                }
                System.out.println("\n***************************************************************");
                System.out.println("  ACCESS GRANTED. WELCOME, RECIPIENT " + recipient.getName() + "!");
                System.out.println("***************************************************************");
            } else {
                displayInvalidInputError("Invalid Recipient ID or password. Try again or register.");
                return;
            }
        } else {
            // Register a new recipient
            System.out.println("\n--------------------------------------------------------------------");
            System.out.println("  REGISTERING NEW RECIPIENT");
            System.out.println("--------------------------------------------------------------------");
            id = "RECIPIENT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            System.out.println("Your system-generated Recipient ID: " + id);
            System.out.print("Enter Full Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Age: ");
            int age = getValidIntegerInput();
            System.out.print("Enter Blood Type (e.g., A+, UNKNOWN): ");
            String bloodType = scanner.nextLine();
            System.out.print("Enter Contact (10-digit phone or email): ");
            String contact = scanner.nextLine();
            System.out.print("Enter Urgency Level (Low/Medium/High): ");
            String urgency = scanner.nextLine();
            System.out.print("Create Password: ");
            String password = scanner.nextLine();
            System.out.print("Confirm Password: ");
            String confirmPassword = scanner.nextLine();

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                displayInvalidInputError("PASSWORD MISMATCH. RECIPIENT REGISTRATION FAILED.");
                return;
            }

            // Generate and verify OTP
            String otp = generateRandomOTP();
            System.out.println("SECURITY VERIFICATION: Your OTP is " + otp);
            System.out.print("Enter OTP to complete registration: ");
            String enteredOtp = scanner.nextLine();

            if (!enteredOtp.equals(otp)) {
                displayInvalidInputError("INVALID OTP. RECIPIENT REGISTRATION ABORTED.");
                return;
            }

            // Create a new recipient object
            recipient = new Recipient(id, name, age, bloodType, contact, urgency);
            recipient.registerInSystem(); // Save recipient to the system
            FileHandler.saveUser(id, "Recipient", password); // Save credentials
            System.out.println("\n***************************************************************");
            System.out.println("  SUCCESS! RECIPIENT ACCOUNT CREATED:");
            System.out.println("  ID: " + id);
            System.out.println("  PLEASE SECURELY STORE YOUR CREDENTIALS");
            System.out.println("***************************************************************");
        }

        recipientMenu(recipient); // Show the recipient menu
    }

    // Shows the recipient menu and handles recipient tasks
    private static void recipientMenu(Recipient recipient) {
        while (true) {
            System.out.println("\n================ RECIPIENT DASHBOARD =================");
            System.out.println("  1. Request Blood Type Test");
            System.out.println("  2. Request Blood Units");
            System.out.println("  3. View Blood Request Status");
            System.out.println("  4. View Blood Test Report");
            System.out.println("  5. Logout and Return to Main Menu");
            System.out.print("Enter your command (1-5): ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> recipient.requestBloodTypeTest(); // Request a blood type test
                case "2" -> {
                    // Request blood units from inventory
                    System.out.print("Enter Blood Type Needed: ");
                    String bloodType = scanner.nextLine();
                    System.out.print("Enter Units Needed: ");
                    int units = getValidIntegerInput();
                    System.out.print("Enter Urgency (Low/Medium/High): ");
                    String urgency = scanner.nextLine();
                    recipient.requestBlood(bloodType, units, urgency);
                }
                case "3" -> recipient.viewRequestStatus(); // Show status of blood requests
                case "4" -> recipient.viewBloodTestReportHistory(); // Show blood test results
                case "5" -> {
                    System.out.println("Logging out from recipient account...");
                    return; // Go back to the main menu
                }
                default -> displayInvalidInputError("Invalid command selection. Please enter a number between 1 and 5.");
            }
        }
    }

    // Generates a random 6-digit OTP for secure registration
    private static String generateRandomOTP() {
        Random random = new Random();
        int otpNumber = random.nextInt(1000000);
        return String.format("%06d", otpNumber); // Format to ensure 6 digits
    }

    // Ensures the user enters a valid number
    private static int getValidIntegerInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                displayInvalidInputError("Please enter a valid number.");
            }
        }
    }

    // Abstract class for common attributes and methods for Donors and Recipients
    static abstract class Person {
        // Fields to store user details
        protected String identificationNumber; // Unique ID for the person
        protected String fullName; // Person's name
        protected int ageValue; // Person's age
        protected String bloodGroup; // Blood type (e.g., A+, UNKNOWN)
        protected String contactInformation; // Phone number or email

        // Constructor to initialize a Person with validation
        public Person(String identificationNumber, String fullName, int ageValue, String bloodGroup, String contactInformation) {
            this.identificationNumber = identificationNumber;
            this.fullName = fullName;
            this.ageValue = ageValue;
            this.bloodGroup = bloodGroup;
            // Check if contact is a valid 10-digit phone number or email
            Pattern phonePattern = Pattern.compile("\\d{10}");
            Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
            if (phonePattern.matcher(contactInformation).matches() || emailPattern.matcher(contactInformation).matches()) {
                this.contactInformation = contactInformation;
            } else {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Contact must be 10-digit phone or valid email address.");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                this.contactInformation = "";
            }
        }

        // Returns the person's ID
        public String getIdentificationNumber() {
            return identificationNumber;
        }

        // Returns the person's name
        public String getName() {
            return fullName;
        }

        // Returns the person's age
        public int getAgeValue() {
            return ageValue;
        }

        // Returns the person's blood type
        public String getBloodGroup() {
            return bloodGroup;
        }

        // Returns the person's contact info
        public String getContactInformation() {
            return contactInformation;
        }

        // Updates age if the person is 18 or older
        public void setAgeValue(int ageValue) {
            if (ageValue >= 18) {
                this.ageValue = ageValue;
            } else {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Individuals must be 18 years or older to participate.");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }

        // Updates blood type if it’s valid
        public void setBloodGroup(String bloodGroup) {
            List<String> validBloodGroups = Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "UNKNOWN");
            if (validBloodGroups.contains(bloodGroup)) {
                this.bloodGroup = bloodGroup;
            } else {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Invalid blood group specified. Valid values are: " + validBloodGroups);
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }

        // Updates contact info if it’s a valid phone or email
        public void setContactInformation(String contactInformation) {
            Pattern phonePattern = Pattern.compile("\\d{10}");
            Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
            if (phonePattern.matcher(contactInformation).matches() || emailPattern.matcher(contactInformation).matches()) {
                this.contactInformation = contactInformation;
            } else {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Contact must be 10-digit phone or valid email address.");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }

        // Abstract method to identify the user’s role (Donor or Recipient)
        public abstract String getUserRoleCategory();
    }

    // Interface for common actions that Donors and Recipients can perform
    interface SystemUser {
        void registerInSystem(); // Save user info to the system
        void requestBloodTypeTest(); // Request a test to determine blood type
        void viewBloodTestReportHistory(); // View past blood test results
    }

    // Admin class to handle admin-specific tasks
    static class Admin {
        private final String administratorId; // Admin’s unique ID
        private final String administratorPassword; // Admin’s password

        // Constructor to initialize an Admin
        public Admin(String administratorId, String administratorPassword) {
            this.administratorId = administratorId;
            this.administratorPassword = administratorPassword;
        }

        // Checks if the admin’s ID and password are correct
        public boolean authenticate() {
            try (BufferedReader reader = new BufferedReader(new FileReader("users.csv"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data[0].equals(administratorId) && data[1].equals("Administrator") && data[2].equals(administratorPassword)) {
                        FileHandler.logAction(administratorId, "Administrator", "Login", "Successful authentication");
                        return true;
                    }
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to read user data - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            return false;
        }

        // Creates a new admin account
        public void registerNewAdministrator() {
            System.out.println("\n--------------------------------------------------------------------");
            System.out.println("  REGISTERING NEW ADMINISTRATOR");
            System.out.println("--------------------------------------------------------------------");
            String newId = "ADMIN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            System.out.println("Generated Administrator ID: " + newId);
            System.out.print("Create Password for new administrator: ");
            String password = scanner.nextLine();
            System.out.print("Confirm Password: ");
            String confirmPassword = scanner.nextLine();

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                displayInvalidInputError("PASSWORD MISMATCH. ADMINISTRATOR REGISTRATION FAILED.");
                return;
            }

            // Generate and verify OTP
            String otp = generateRandomOTP();
            System.out.println("SECURITY VERIFICATION: Your OTP is " + otp);
            System.out.print("Enter OTP to complete registration: ");
            String enteredOtp = scanner.nextLine();

            if (!enteredOtp.equals(otp)) {
                displayInvalidInputError("INVALID OTP. ADMINISTRATOR REGISTRATION ABORTED.");
                return;
            }

            // Save the new admin’s credentials
            FileHandler.saveUser(newId, "Administrator", password);
            System.out.println("\n***************************************************************");
            System.out.println("  SUCCESS! NEW ADMINISTRATOR ACCOUNT CREATED:");
            System.out.println("  ID: " + newId);
            System.out.println("  PLEASE SECURELY STORE YOUR CREDENTIALS");
            System.out.println("***************************************************************");
            FileHandler.logAction(administratorId, "Administrator", "Register New Admin", "Registered new admin with ID " + newId);
        }

        // Shows the current blood inventory with details
        public void viewInventory() {
            List<String> inventory = FileHandler.loadInventoryData();
            if (inventory.isEmpty()) {
                System.out.println("\nInventory is currently empty.");
                return;
            }

            // Display inventory details with warnings for low stock or expiring units
            System.out.println("\n================ BLOOD INVENTORY STATUS =================");
            System.out.println("Type | Component     | Units | Expiration   | Reserved | Status");
            System.out.println("---------------------------------------------------------");
            for (String item : inventory) {
                String[] data = item.split(",");
                System.out.printf("%-5s| %-13s| %6s| %12s| %9s| %-8s%n", data[0], data[1], data[2], data[3], data[4], data[5]);
                int units = Integer.parseInt(data[2]);
                int reserved = Integer.parseInt(data[4]);
                if (units < 5) {
                    System.out.println("WARNING: Low stock for " + data[0] + " " + data[1] + " (" + units + " units)");
                }
                if (data[5].equals("BLOCKED")) {
                    System.out.println("NOTICE: " + data[0] + " " + data[1] + " is currently BLOCKED");
                }
                if (reserved > 0) {
                    System.out.println("NOTE: " + reserved + " units reserved for " + data[0] + " " + data[1]);
                }
                LocalDate expDate = LocalDate.parse(data[3]);
                long daysToExpire = ChronoUnit.DAYS.between(LocalDate.now(), expDate);
                if (daysToExpire <= 7 && daysToExpire >= 0) {
                    System.out.println("ALERT: Expiring soon for " + data[0] + " " + data[1] + " on " + expDate);
                }
            }
            FileHandler.logAction(administratorId, "Administrator", "View Inventory", "Viewed full inventory");
        }

        // Adds or updates blood units in the inventory
        public void updateInventory(String bloodType, String component, int units, String expDate) {
            FileHandler.updateInventory(bloodType, component, units, expDate);
            System.out.println("\nInventory updated: " + bloodType + " " + component + ", " + units + " units");
            FileHandler.logAction(administratorId, "Administrator", "Update Inventory", "Updated " + bloodType + " " + component + " with " + units + " units");
        }

        // Processes pending blood type test requests
        public void processTestRequests() {
            List<String> tests = FileHandler.loadTestRequests();
            if (tests.isEmpty()) {
                System.out.println("\nNo pending test requests.");
                return;
            }

            // Show and process each pending test request
            System.out.println("\n================ PENDING TEST REQUESTS =================");
            System.out.println("Test ID | User ID | User Type | Request Date | Status");
            System.out.println("----------------------------------------------------");
            for (String test : tests) {
                String[] data = test.split(",");
                if (data[4].equals("PENDING")) {
                    System.out.printf("%-36s| %-8s| %-10s| %-12s| %-8s%n", data[0], data[1], data[2], data[3], data[4]);
                    System.out.print("Enter Blood Type Result for Test " + data[0] + " (e.g., A+): ");
                    String result = scanner.nextLine();
                    FileHandler.updateTestRequest(data[0], result);
                    if (data[2].equals("Donor")) {
                        Donor donor = FileHandler.loadDonor(data[1]);
                        donor.setBloodGroup(result);
                        FileHandler.saveDonorInformation(donor);
                    } else {
                        Recipient recipient = FileHandler.loadRecipient(data[1]);
                        recipient.setBloodGroup(result);
                        FileHandler.saveRecipientInformation(recipient);
                    }
                    System.out.println("Test " + data[0] + " updated with blood type: " + result);
                    FileHandler.logAction(administratorId, "Administrator", "Process Test", "Updated test " + data[0] + " with " + result);
                }
            }
        }

        // Shows system statistics like donor and recipient counts
        public void viewReports() {
            int donorCount = FileHandler.loadDonors().size();
            int recipientCount = FileHandler.loadRecipients().size();
            int pendingTests = FileHandler.loadTestRequests().size();
            int inventoryUnits = FileHandler.loadInventoryData().stream()
                .mapToInt(line -> Integer.valueOf(line.split(",")[2]))
                .sum();
            int reservedUnits = FileHandler.loadInventoryData().stream().mapToInt(line -> {
                String[] data = line.split(",");
                return Integer.valueOf(data[4]);
            }).sum();

            // Display the statistics
            System.out.println("\n================ SYSTEM REPORTS AND ANALYTICS =================");
            System.out.println("Total Registered Donors: " + donorCount);
            System.out.println("Total Registered Recipients: " + recipientCount);
            System.out.println("Total Pending Test Requests: " + pendingTests);
            System.out.println("Total Available Inventory Units: " + inventoryUnits);
            System.out.println("Total Reserved Units: " + reservedUnits);
            FileHandler.logAction(administratorId, "Administrator", "View Reports", "Viewed system reports");
        }

        // Saves a backup of all data files
        public void backupData() {
            FileHandler.backupData();
            System.out.println("\nData backed up successfully to backup files.");
            FileHandler.logAction(administratorId, "Administrator", "Backup Data", "Backed up all CSV files");
        }

        // Lists all registered donors
        public void viewAllDonors() {
            List<String> donors = FileHandler.loadDonors();
            if (donors.isEmpty()) {
                System.out.println("\nNo donors registered in the system.");
            } else {
                System.out.println("\n================ ALL REGISTERED DONORS =================");
                System.out.println("ID | Name | Age | Blood Type | Contact");
                System.out.println("------------------------------------------------");
                for (String donor : donors) {
                    System.out.println(donor.replace(",", " | "));
                }
            }
            FileHandler.logAction(administratorId, "Administrator", "View Donors", "Viewed all registered donors");
        }

        // Lists all registered recipients
        public void viewAllRecipients() {
            List<String> recipients = FileHandler.loadRecipients();
            if (recipients.isEmpty()) {
                System.out.println("\nNo recipients registered in the system.");
            } else {
                System.out.println("\n================ ALL REGISTERED RECIPIENTS =================");
                System.out.println("ID | Name | Age | Blood Type | Contact | Urgency");
                System.out.println("-----------------------------------------------------");
                for (String recipient : recipients) {
                    System.out.println(recipient.replace(",", " | "));
                }
            }
            FileHandler.logAction(administratorId, "Administrator", "View Recipients", "Viewed all registered recipients");
        }

        // Reserves blood units for specific needs
        public void reserveBlood(String bloodType, String component, int units) {
            boolean success = FileHandler.reserveBlood(bloodType, component, units);
            if (success) {
                System.out.println("\n" + units + " units of " + bloodType + " " + component + " reserved successfully.");
                FileHandler.logAction(administratorId, "Administrator", "Reserve Blood", "Reserved " + units + " units of " + bloodType + " " + component);
            } else {
                displayInvalidInputError("Failed to reserve: Insufficient units or blood is blocked.");
            }
        }

        // Blocks specific blood units from being used
        public void blockBlood(String bloodType, String component, int units) {
            boolean success = FileHandler.blockBlood(bloodType, component, units);
            if (success) {
                System.out.println("\n" + units + " units of " + bloodType + " " + component + " blocked successfully.");
                FileHandler.logAction(administratorId, "Administrator", "Block Blood", "Blocked " + units + " units of " + bloodType + " " + component);
            } else {
                displayInvalidInputError("Failed to block: Insufficient units or already blocked.");
            }
        }

        // Blocks all units of a specific blood type
        public void blockBloodType(String bloodType) {
            boolean success = FileHandler.blockBloodType(bloodType);
            if (success) {
                System.out.println("\nBlood type " + bloodType + " blocked successfully.");
                FileHandler.logAction(administratorId, "Administrator", "Block Blood Type", "Blocked blood type " + bloodType);
            } else {
                displayInvalidInputError("Failed to block: No inventory for " + bloodType);
            }
        }

        // Blocks the entire blood inventory
        public void blockEntireStock() {
            FileHandler.blockEntireStock();
            System.out.println("\nEntire blood stock blocked successfully.");
            FileHandler.logAction(administratorId, "Administrator", "Block Entire Stock", "Blocked entire blood stock");
        }
    }

    // Donor class for managing donor-specific actions
    static class Donor extends Person implements SystemUser {
        private LocalDate lastDonationDate; // Tracks the last time the donor gave blood

        // Constructor to initialize a Donor
        public Donor(String identificationNumber, String fullName, int ageValue, String bloodGroup, String contactInformation, LocalDate lastDonationDate) {
            super(identificationNumber, fullName, ageValue, bloodGroup, contactInformation);
            this.lastDonationDate = lastDonationDate;
        }

        // Returns the last donation date
        public LocalDate getLastDonationDate() {
            return lastDonationDate;
        }

        // Updates the last donation date
        public void setLastDonationDate(LocalDate lastDonationDate) {
            this.lastDonationDate = lastDonationDate;
        }

        // Identifies the user as a Donor
        @Override
        public String getUserRoleCategory() {
            return "Donor";
        }

        // Saves donor info to the system
        @Override
        public void registerInSystem() {
            System.out.println("\nRegistering donor: " + fullName);
            FileHandler.saveDonorInformation(this);
        }

        // Requests a blood type test if the donor’s blood type is unknown
        @Override
        public void requestBloodTypeTest() {
            if (bloodGroup.equals("UNKNOWN")) {
                System.out.println("\nBlood type test requested for Donor: " + fullName);
                System.out.println("Test is pending. An admin will process it. Check your report later.");
                FileHandler.saveTestRequest(identificationNumber, getUserRoleCategory(), LocalDate.now(), "PENDING");
            } else {
                System.out.println("\nBlood type already known: " + bloodGroup + ". View your report for details.");
            }
        }

        // Shows the donor’s blood test history
        @Override
        public void viewBloodTestReportHistory() {
            List<String> tests = FileHandler.loadTestRequests(identificationNumber);
            if (tests.isEmpty()) {
                System.out.println("\nNo blood test requests found.");
            } else {
                System.out.println("\n================ BLOOD TEST REPORT FOR " + fullName + " =================");
                System.out.println("Test ID | Request Date | Status | Blood Type");
                System.out.println("--------------------------------------------");
                for (String test : tests) {
                    String[] data = test.split(",");
                    String bloodTypeResult = data.length > 5 && !data[5].isEmpty() ? data[5] : "Not yet determined";
                    System.out.printf("%-36s| %-12s| %-8s| %-10s%n", data[0], data[3], data[4], bloodTypeResult);
                }
            }
        }

        // Checks if the donor can donate (must wait 90 days since last donation)
        public boolean checkDonationEligibility() {
            if (lastDonationDate == null) return true;
            long daysSinceLast = ChronoUnit.DAYS.between(lastDonationDate, LocalDate.now());
            return daysSinceLast >= 90;
        }

        // Schedules a blood donation
        public void scheduleDonation(String date) {
            try {
                LocalDate donationDate = LocalDate.parse(date);
                FileHandler.saveSchedule(identificationNumber, donationDate, "Blood Bank");
                setLastDonationDate(donationDate);
                FileHandler.saveDonorInformation(this);
                System.out.println("\nDonation scheduled for " + date);
                FileHandler.logAction(identificationNumber, "Donor", "Schedule Donation", "Scheduled donation for " + date);
            } catch (Exception e) {
                displayInvalidInputError("Invalid date format. Use YYYY-MM-DD.");
            }
        }

        // Shows the donor’s past donations
        public void viewDonationHistory() {
            List<String> schedules = FileHandler.loadSchedules(identificationNumber);
            if (schedules.isEmpty()) {
                System.out.println("\nNo donation history found.");
            } else {
                System.out.println("\n================ DONATION HISTORY FOR " + fullName + " =================");
                System.out.println("Donor ID | Date | Location");
                System.out.println("--------------------------------");
                for (String schedule : schedules) {
                    System.out.println(schedule.replace(",", " | "));
                }
            }
        }
    }

    // Recipient class for managing recipient-specific actions
    static class Recipient extends Person implements SystemUser {
        private String medicalUrgencyLevel; // Urgency level for blood needs (Low, Medium, High)

        // Constructor to initialize a Recipient with urgency validation
        public Recipient(String identificationNumber, String fullName, int ageValue, String bloodGroup, String contactInformation, String medicalUrgencyLevel) {
            super(identificationNumber, fullName, ageValue, bloodGroup, contactInformation);
            List<String> validLevels = Arrays.asList("Low", "Medium", "High");
            if (validLevels.contains(medicalUrgencyLevel)) {
                this.medicalUrgencyLevel = medicalUrgencyLevel;
            } else {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Urgency must be Low, Medium, or High. Defaulting to Medium.");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                this.medicalUrgencyLevel = "Medium";
            }
        }

        // Returns the urgency level
        public String getMedicalUrgencyLevel() {
            return medicalUrgencyLevel;
        }

        // Updates the urgency level if valid
        public void setMedicalUrgencyLevel(String medicalUrgencyLevel) {
            List<String> validLevels = Arrays.asList("Low", "Medium", "High");
            if (validLevels.contains(medicalUrgencyLevel)) {
                this.medicalUrgencyLevel = medicalUrgencyLevel;
            } else {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Urgency must be Low, Medium, or High. Defaulting to Medium.");
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                this.medicalUrgencyLevel = "Medium";
            }
        }

        // Identifies the user as a Recipient
        @Override
        public String getUserRoleCategory() {
            return "Recipient";
        }

        // Saves recipient info to the system
        @Override
        public void registerInSystem() {
            System.out.println("\nRegistering recipient: " + fullName);
            FileHandler.saveRecipientInformation(this);
        }

        // Requests a blood type test if the recipient’s blood type is unknown
        @Override
        public void requestBloodTypeTest() {
            if (bloodGroup.equals("UNKNOWN")) {
                System.out.println("\nBlood type test requested for Recipient: " + fullName);
                System.out.println("Test is pending. An admin will process it. Check your report later.");
                FileHandler.saveTestRequest(identificationNumber, getUserRoleCategory(), LocalDate.now(), "PENDING");
            } else {
                System.out.println("\nBlood type already known: " + bloodGroup + ". View your report for details.");
            }
        }

        // Shows the recipient’s blood test history
        @Override
        public void viewBloodTestReportHistory() {
            List<String> tests = FileHandler.loadTestRequests(identificationNumber);
            if (tests.isEmpty()) {
                System.out.println("\nNo blood test requests found.");
            } else {
                System.out.println("\n================ BLOOD TEST REPORT FOR " + fullName + " =================");
                System.out.println("Test ID | Request Date | Status | Blood Type");
                System.out.println("--------------------------------------------");
                for (String test : tests) {
                    String[] data = test.split(",");
                    String bloodTypeResult = data.length > 5 && !data[5].isEmpty() ? data[5] : "Not yet determined";
                    System.out.printf("%-36s| %-12s| %-8s| %-10s%n", data[0], data[3], data[4], bloodTypeResult);
                }
            }
        }

        // Requests blood units from inventory or donors
        public void requestBlood(String bloodType, int units, String urgency) {
            if (FileHandler.isBloodBlocked(bloodType)) {
                displayInvalidInputError("Blood type " + bloodType + " is currently blocked by admin.");
                return;
            }
            if (FileHandler.isEntireStockBlocked()) {
                displayInvalidInputError("Entire blood stock is blocked by admin.");
                return;
            }
            String requestId = UUID.randomUUID().toString();
            boolean fulfilled = FileHandler.processBloodRequest(requestId, identificationNumber, bloodType, units, urgency);
            if (!fulfilled) {
                List<String> donors = FileHandler.findCompatibleDonors(bloodType);
                if (donors.isEmpty()) {
                    System.out.println("\nNo compatible donors found for blood type " + bloodType + ".");
                } else {
                    System.out.println("\nBlood not available in inventory. Contact the following compatible donors:");
                    System.out.println("Name | Contact | Blood Type");
                    System.out.println("--------------------------------");
                    for (String donor : donors) {
                        System.out.println(donor.replace(",", " | "));
                    }
                }
            } else {
                System.out.println("\nBlood request fulfilled from inventory.");
            }
            FileHandler.logAction(identificationNumber, "Recipient", "Request Blood", "Requested " + units + " units of " + bloodType);
        }

        // Shows the status of the recipient’s blood requests
        public void viewRequestStatus() {
            List<String> requests = FileHandler.loadRequests(identificationNumber);
            if (requests.isEmpty()) {
                System.out.println("\nNo blood requests found.");
            } else {
                System.out.println("\n================ BLOOD REQUEST STATUS FOR " + fullName + " =================");
                System.out.println("Request ID | Blood Type | Units | Urgency | Status");
                System.out.println("--------------------------------------------------");
                for (String request : requests) {
                    System.out.println(request.replace(",", " | "));
                }
            }
        }
    }

    // FileHandler class to manage all file operations (saving and loading data)
    static class FileHandler {
        // Creates CSV files for storing data if they don’t exist
        public static void createCSVFiles() {
            String[] files = {"donors.csv", "recipients.csv", "tests.csv", "inventory.csv", "requests.csv", "schedules.csv", "log.csv", "users.csv"};
            for (String file : files) {
                try {
                    File f = new File(file);
                    if (!f.exists()) {
                        f.createNewFile();
                        System.out.println("Created new data file: " + file);
                    }
                } catch (IOException e) {
                    System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    System.out.println("   ERROR: Failed to create " + file + " - " + e.getMessage());
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }
            }
        }

        // Checks if the users.csv file is empty (no users registered)
        public static boolean isUsersFileEmpty() {
            try (BufferedReader reader = new BufferedReader(new FileReader("users.csv"))) {
                return reader.readLine() == null;
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Could not check users file - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                return true;
            }
        }

        // Saves a user’s credentials to users.csv
        public static void saveUser(String userId, String userRole, String password) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.csv", true))) {
                writer.write(userId + "," + userRole + "," + password);
                writer.newLine();
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to save user - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }

        // Checks if a user’s ID, role, and password match
        public static boolean authenticateUser(String id, String role, String password) {
            try (BufferedReader reader = new BufferedReader(new FileReader("users.csv"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data[0].equals(id) && data[1].equals(role) && data[2].equals(password)) {
                        return true;
                    }
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to read user data - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            return false;
        }

        // Saves donor info to donors.csv
        public static void saveDonorInformation(Donor donor) {
            List<String> donors = loadDonors();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("donors.csv"))) {
                boolean exists = false;
                for (String line : donors) {
                    String[] data = line.split(",");
                    if (data[0].equals(donor.getIdentificationNumber())) {
                        writer.write(donor.getIdentificationNumber() + "," + donor.getName() + "," + 
                                   donor.getAgeValue() + "," + donor.getBloodGroup() + "," + 
                                   donor.getContactInformation() + "," + 
                                   (donor.getLastDonationDate() != null ? donor.getLastDonationDate() : ""));
                        exists = true;
                    } else {
                        writer.write(line);
                    }
                    writer.newLine();
                }
                if (!exists) {
                    writer.write(donor.getIdentificationNumber() + "," + donor.getName() + "," + 
                               donor.getAgeValue() + "," + donor.getBloodGroup() + "," + 
                               donor.getContactInformation() + "," + 
                               (donor.getLastDonationDate() != null ? donor.getLastDonationDate() : ""));
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to save donor - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }

        // Loads a donor’s info from donors.csv
        public static Donor loadDonor(String id) {
            try (BufferedReader reader = new BufferedReader(new FileReader("donors.csv"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data[0].equals(id)) {
                        LocalDate lastDonation = data.length > 5 && !data[5].isEmpty() ? LocalDate.parse(data[5]) : null;
                        return new Donor(data[0], data[1], Integer.parseInt(data[2]), data[3], data[4], lastDonation);
                    }
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to load donor - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            return null;
        }

        // Saves recipient info to recipients.csv
        public static void saveRecipientInformation(Recipient recipient) {
            List<String> recipients = loadRecipients();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("recipients.csv"))) {
                boolean exists = false;
                for (String line : recipients) {
                    String[] data = line.split(",");
                    if (data[0].equals(recipient.getIdentificationNumber())) {
                        writer.write(recipient.getIdentificationNumber() + "," + recipient.getName() + "," + 
                                   recipient.getAgeValue() + "," + recipient.getBloodGroup() + "," + 
                                   recipient.getContactInformation() + "," + recipient.getMedicalUrgencyLevel());
                        exists = true;
                    } else {
                        writer.write(line);
                    }
                    writer.newLine();
                }
                if (!exists) {
                    writer.write(recipient.getIdentificationNumber() + "," + recipient.getName() + "," + 
                               recipient.getAgeValue() + "," + recipient.getBloodGroup() + "," + 
                               recipient.getContactInformation() + "," + recipient.getMedicalUrgencyLevel());
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to save recipient - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }

        // Loads a recipient’s info from recipients.csv
        public static Recipient loadRecipient(String id) {
            try (BufferedReader reader = new BufferedReader(new FileReader("recipients.csv"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data[0].equals(id)) {
                        return new Recipient(data[0], data[1], Integer.parseInt(data[2]), data[3], data[4], data[5]);
                    }
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to load recipient - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            return null;
        }

        // Saves a blood type test request to tests.csv
        public static void saveTestRequest(String userId, String userType, LocalDate requestDate, String status) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("tests.csv", true))) {
                String testId = UUID.randomUUID().toString();
                writer.write(testId + "," + userId + "," + userType + "," + requestDate + "," + status + ",");
                writer.newLine();
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to save test request - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }

        // Loads test requests for a specific user
        public static List<String> loadTestRequests(String userId) {
            List<String> tests = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("tests.csv"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data[1].equals(userId)) {
                        tests.add(line);
                    }
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to load test requests - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            return tests;
        }

        // Loads all test requests
        public static List<String> loadTestRequests() {
            List<String> tests = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("tests.csv"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    tests.add(line);
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to load test requests - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            return tests;
        }

        // Updates a test request with the blood type result
        public static void updateTestRequest(String testId, String bloodType) {
            List<String> tests = loadTestRequests();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("tests.csv"))) {
                for (String test : tests) {
                    String[] data = test.split(",");
                    if (data[0].equals(testId)) {
                        writer.write(data[0] + "," + data[1] + "," + data[2] + "," + data[3] + ",COMPLETED," + bloodType);
                    } else {
                        writer.write(test);
                    }
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to update test request - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }

        // Updates the blood inventory with new units
        public static void updateInventory(String bloodType, String component, int units, String expDate) {
            List<String> inventory = loadInventoryData();
            boolean updated = false;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("inventory.csv"))) {
                for (String item : inventory) {
                    String[] data = item.split(",");
                    if (data[0].equals(bloodType) && data[1].equals(component)) {
                        int newUnits = Integer.parseInt(data[2]) + units;
                        int reserved = Integer.parseInt(data[4]);
                        String status = data[5];
                        writer.write(bloodType + "," + component + "," + newUnits + "," + expDate + "," + reserved + "," + status);
                        updated = true;
                    } else {
                        writer.write(item);
                    }
                    writer.newLine();
                }
                if (!updated) {
                    writer.write(bloodType + "," + component + "," + units + "," + expDate + ",0,AVAILABLE");
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to update inventory - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }

        // Loads all inventory data
        public static List<String> loadInventoryData() {
            List<String> inventory = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("inventory.csv"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    inventory.add(line);
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to load inventory - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            return inventory;
        }

        // Reserves blood units from the inventory
        public static boolean reserveBlood(String bloodType, String component, int units) {
            List<String> inventory = loadInventoryData();
            boolean success = false;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("inventory.csv"))) {
                for (String item : inventory) {
                    String[] data = item.split(",");
                    if (data[0].equals(bloodType) && data[1].equals(component) && data[5].equals("AVAILABLE")) {
                        int availableUnits = Integer.parseInt(data[2]);
                        int reserved = Integer.parseInt(data[4]);
                        if (availableUnits >= units) {
                            int newUnits = availableUnits - units;
                            reserved += units;
                            writer.write(data[0] + "," + data[1] + "," + newUnits + "," + data[3] + "," + reserved + "," + data[5]);
                            success = true;
                        } else {
                            writer.write(item);
                        }
                    } else {
                        writer.write(item);
                    }
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to reserve blood - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            return success;
        }

        // Blocks specific blood units
        public static boolean blockBlood(String bloodType, String component, int units) {
            List<String> inventory = loadInventoryData();
            boolean success = false;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("inventory.csv"))) {
                for (String item : inventory) {
                    String[] data = item.split(",");
                    if (data[0].equals(bloodType) && data[1].equals(component) && data[5].equals("AVAILABLE")) {
                        int availableUnits = Integer.parseInt(data[2]);
                        if (availableUnits >= units) {
                            writer.write(data[0] + "," + data[1] + "," + availableUnits + "," + data[3] + "," + data[4] + ",BLOCKED");
                            success = true;
                        } else {
                            writer.write(item);
                        }
                    } else {
                        writer.write(item);
                    }
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to block blood - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            return success;
        }

        // Blocks all units of a specific blood type
        public static boolean blockBloodType(String bloodType) {
            List<String> inventory = loadInventoryData();
            boolean success = false;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("inventory.csv"))) {
                for (String item : inventory) {
                    String[] data = item.split(",");
                    if (data[0].equals(bloodType) && data[5].equals("AVAILABLE")) {
                        writer.write(data[0] + "," + data[1] + "," + data[2] + "," + data[3] + "," + data[4] + ",BLOCKED");
                        success = true;
                    } else {
                        writer.write(item);
                    }
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to block blood type - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            return success;
        }

        // Blocks the entire blood inventory
        public static void blockEntireStock() {
            List<String> inventory = loadInventoryData();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("inventory.csv"))) {
                for (String item : inventory) {
                    String[] data = item.split(",");
                    writer.write(data[0] + "," + data[1] + "," + data[2] + "," + data[3] + "," + data[4] + ",BLOCKED");
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to block entire stock - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }

        // Checks if a specific blood type is blocked
        public static boolean isBloodBlocked(String bloodType) {
            List<String> inventory = loadInventoryData();
            for (String item : inventory) {
                String[] data = item.split(",");
                if (data[0].equals(bloodType) && data[5].equals("BLOCKED")) {
                    return true;
                }
            }
            return false;
        }

        // Checks if the entire inventory is blocked
        public static boolean isEntireStockBlocked() {
            List<String> inventory = loadInventoryData();
            for (String item : inventory) {
                String[] data = item.split(",");
                if (!data[5].equals("BLOCKED")) {
                    return false;
                }
            }
            return !inventory.isEmpty();
        }

        // Processes a blood request by checking inventory or finding donors
        public static boolean processBloodRequest(String requestId, String recipientId, String bloodType, int units, String urgency) {
            if (isBloodBlocked(bloodType) || isEntireStockBlocked()) {
                return false;
            }
            List<String> inventory = loadInventoryData();
            boolean fulfilled = false;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("inventory.csv"))) {
                for (String item : inventory) {
                    String[] data = item.split(",");
                    if (data[0].equals(bloodType) && Integer.parseInt(data[2]) >= units && data[5].equals("AVAILABLE")) {
                        int newUnits = Integer.parseInt(data[2]) - units;
                        writer.write(data[0] + "," + data[1] + "," + newUnits + "," + data[3] + "," + data[4] + "," + data[5]);
                        fulfilled = true;
                    } else {
                        writer.write(item);
                    }
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to process blood request - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            String status = fulfilled ? "FULFILLED" : "PENDING";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("requests.csv", true))) {
                writer.write(requestId + "," + recipientId + "," + bloodType + "," + units + "," + urgency + "," + status);
                writer.newLine();
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to save blood request - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            return fulfilled;
        }

        // Finds donors with compatible blood types
        public static List<String> findCompatibleDonors(String bloodType) {
            List<String> donors = loadDonors();
            List<String> compatible = new ArrayList<>();
            String[] compatibleTypes = getCompatibleBloodTypes(bloodType);
            for (String donor : donors) {
                String[] data = donor.split(",");
                for (String type : compatibleTypes) {
                    if (data[3].equals(type) && !data[3].equals("UNKNOWN")) {
                        compatible.add(data[1] + "," + data[4] + "," + data[3]);
                        break;
                    }
                }
            }
            return compatible;
        }

        // Loads all donors from donors.csv
        public static List<String> loadDonors() {
            List<String> donors = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("donors.csv"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    donors.add(line);
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to load donors - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            return donors;
        }

        // Loads all recipients from recipients.csv
        public static List<String> loadRecipients() {
            List<String> recipients = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("recipients.csv"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    recipients.add(line);
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to load recipients - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            return recipients;
        }

        // Loads blood requests for a specific recipient
        public static List<String> loadRequests(String recipientId) {
            List<String> requests = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("requests.csv"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data[1].equals(recipientId)) {
                        requests.add(line);
                    }
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to load requests - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            return requests;
        }

        // Loads donation schedules for a specific donor
        public static List<String> loadSchedules(String donorId) {
            List<String> schedules = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader("schedules.csv"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data[0].equals(donorId)) {
                        schedules.add(line);
                    }
                }
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to load schedules - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            return schedules;
        }

        // Saves a donation schedule
        public static void saveSchedule(String donorId, LocalDate date, String location) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("schedules.csv", true))) {
                writer.write(donorId + "," + date + "," + location);
                writer.newLine();
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to save schedule - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }

        // Logs an action to log.csv for tracking
        public static void logAction(String userId, String role, String action, String details) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("log.csv", true))) {
                String timestamp = LocalDate.now().toString();
                writer.write(timestamp + "," + userId + "," + role + "," + action + "," + details);
                writer.newLine();
            } catch (IOException e) {
                System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("   ERROR: Failed to log action - " + e.getMessage());
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }

        // Backs up all CSV files to separate backup files
        public static void backupData() {
            String[] files = {"donors.csv", "recipients.csv", "tests.csv", "inventory.csv", "requests.csv", "schedules.csv", "log.csv", "users.csv"};
            for (String file : files) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file));
                     BufferedWriter writer = new BufferedWriter(new FileWriter(file + "backup_"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                } catch (IOException e) {
                    System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    System.out.println("   ERROR: Failed to back up " + file + " - " + e.getMessage());
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }
            }
        }

        // Returns compatible blood types for a given blood type
        private static String[] getCompatibleBloodTypes(String bloodType) {
            Map<String, String[]> compatibility = new HashMap<>();
            compatibility.put("A+", new String[]{"A+", "A-", "O+", "O-"});
            compatibility.put("A-", new String[]{"A-", "O-"});
            compatibility.put("B+", new String[]{"B+", "B-", "O+", "O-"});
            compatibility.put("B-", new String[]{"B-", "O-"});
            compatibility.put("AB+", new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
            compatibility.put("AB-", new String[]{"A-", "B-", "AB-", "O-"});
            compatibility.put("O+", new String[]{"O+", "O-"});
            compatibility.put("O-", new String[]{"O-"});
            return compatibility.getOrDefault(bloodType, new String[]{bloodType});
        }
    }
}
