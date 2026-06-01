#include "ParkingLot.h"
#include "ReportManager.h"

#include <algorithm>
#include <cctype>
#include <iomanip>
#include <iostream>
#include <limits>
#include <string>

std::string trim(const std::string& value) {
    size_t start = 0;
    while (start < value.size() && std::isspace(static_cast<unsigned char>(value[start]))) {
        ++start;
    }

    size_t end = value.size();
    while (end > start && std::isspace(static_cast<unsigned char>(value[end - 1]))) {
        --end;
    }

    return value.substr(start, end - start);
}

int readInt(const std::string& prompt, int minValue, int maxValue) {
    int value;
    while (true) {
        std::cout << prompt;
        if (std::cin >> value && value >= minValue && value <= maxValue) {
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
            return value;
        }
        std::cout << "Invalid input. Please enter a number from " << minValue << " to " << maxValue << ".\n";
        std::cin.clear();
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
    }
}

std::string readLine(const std::string& prompt) {
    std::string value;
    while (true) {
        std::cout << prompt;
        std::getline(std::cin, value);
        value = trim(value);
        if (!value.empty()) {
            return value;
        }
        std::cout << "Input cannot be empty.\n";
    }
}

bool login() {
    const std::string adminPassword = "admin123";
    std::string password;

    std::cout << "========================================\n";
    std::cout << "    Parking Lot Management System\n";
    std::cout << "========================================\n";

    for (int attempt = 1; attempt <= 3; ++attempt) {
        std::cout << "Enter admin password: ";
        std::getline(std::cin, password);
        if (password == adminPassword) {
            std::cout << "Login successful.\n";
            return true;
        }
        std::cout << "Incorrect password. Attempts left: " << 3 - attempt << "\n";
    }

    return false;
}

void pauseScreen() {
    std::cout << "\nPress Enter to continue...";
    std::cin.get();
}

void showMenu() {
    std::cout << "\n========================================\n";
    std::cout << "            MAIN MENU\n";
    std::cout << "========================================\n";
    std::cout << "1. Vehicle Entry\n";
    std::cout << "2. Vehicle Exit\n";
    std::cout << "3. Show Available Slots\n";
    std::cout << "4. Show Occupied Slots\n";
    std::cout << "5. Search Vehicle by Number\n";
    std::cout << "6. Search Vehicle by Slot\n";
    std::cout << "7. Generate Report\n";
    std::cout << "8. Show Parking History\n";
    std::cout << "9. Show Daily Revenue\n";
    std::cout << "10. Save Records\n";
    std::cout << "0. Exit\n";
    std::cout << "========================================\n";
}

std::string selectVehicleType() {
    std::cout << "\nVehicle Types\n";
    std::cout << "1. Car  (50/hour)\n";
    std::cout << "2. Bike (20/hour)\n";
    std::cout << "3. Bus  (100/hour)\n";

    int choice = readInt("Select vehicle type: ", 1, 3);
    if (choice == 1) {
        return "Car";
    }
    if (choice == 2) {
        return "Bike";
    }
    return "Bus";
}

void printTicketDetails(const Ticket* ticket) {
    if (!ticket) {
        std::cout << "Record not found.\n";
        return;
    }

    std::cout << "\nVehicle Found\n";
    std::cout << "-------------\n";
    std::cout << "Vehicle Number : " << ticket->getVehicle().getNumber() << "\n";
    std::cout << "Vehicle Type   : " << ticket->getVehicle().getType() << "\n";
    std::cout << "Parking Slot   : " << ticket->getSlotNumber() << "\n";
    std::cout << "Entry Time     : " << ticket->getFormattedEntryTime() << "\n";
}

int main() {
    if (!login()) {
        std::cout << "Access denied. Program closed.\n";
        return 0;
    }

    int capacity = readInt("\nEnter total parking capacity: ", 1, 500);
    ParkingLot parkingLot(capacity);
    ReportManager reportManager;
    parkingLot.loadRecords();

    bool running = true;
    while (running) {
        showMenu();
        int choice = readInt("Enter your choice: ", 0, 10);

        switch (choice) {
            case 1: {
                std::string vehicleNumber = readLine("Enter vehicle number: ");
                std::transform(vehicleNumber.begin(), vehicleNumber.end(), vehicleNumber.begin(), ::toupper);
                std::string vehicleType = selectVehicleType();
                std::string message;
                parkingLot.addVehicle(vehicleNumber, vehicleType, message);
                std::cout << message << "\n";
                pauseScreen();
                break;
            }
            case 2: {
                std::string vehicleNumber = readLine("Enter vehicle number for exit: ");
                std::transform(vehicleNumber.begin(), vehicleNumber.end(), vehicleNumber.begin(), ::toupper);
                ExitReceipt receipt = parkingLot.removeVehicle(vehicleNumber);
                if (!receipt.success) {
                    std::cout << receipt.message << "\n";
                } else {
                    std::cout << "\nExit Receipt\n";
                    std::cout << "------------\n";
                    std::cout << "Vehicle Number : " << receipt.vehicleNumber << "\n";
                    std::cout << "Vehicle Type   : " << receipt.vehicleType << "\n";
                    std::cout << "Slot Number    : " << receipt.slotNumber << "\n";
                    std::cout << "Entry Time     : " << Ticket::formatTime(receipt.entryTime) << "\n";
                    std::cout << "Exit Time      : " << Ticket::formatTime(receipt.exitTime) << "\n";
                    std::cout << "Duration       : " << receipt.durationMinutes << " minute(s)\n";
                    std::cout << "Billable Hours : " << receipt.billableHours << "\n";
                    std::cout << "Total Fee      : " << std::fixed << std::setprecision(2) << receipt.fee << "\n";
                }
                pauseScreen();
                break;
            }
            case 3:
                parkingLot.showAvailableSlots();
                pauseScreen();
                break;
            case 4:
                parkingLot.showOccupiedSlots();
                pauseScreen();
                break;
            case 5: {
                std::string vehicleNumber = readLine("Enter vehicle number: ");
                std::transform(vehicleNumber.begin(), vehicleNumber.end(), vehicleNumber.begin(), ::toupper);
                printTicketDetails(parkingLot.searchByVehicleNumber(vehicleNumber));
                pauseScreen();
                break;
            }
            case 6: {
                int slotNumber = readInt("Enter slot number: ", 1, parkingLot.getTotalCapacity());
                printTicketDetails(parkingLot.searchBySlot(slotNumber));
                pauseScreen();
                break;
            }
            case 7:
                reportManager.showSummaryReport(parkingLot);
                pauseScreen();
                break;
            case 8:
                parkingLot.showParkingHistory();
                pauseScreen();
                break;
            case 9:
                parkingLot.showDailyRevenue();
                pauseScreen();
                break;
            case 10:
                parkingLot.saveRecords();
                std::cout << "Records saved successfully.\n";
                pauseScreen();
                break;
            case 0:
                parkingLot.saveRecords();
                running = false;
                std::cout << "Records saved. Thank you for using the system.\n";
                break;
            default:
                std::cout << "Invalid option.\n";
        }
    }

    return 0;
}
