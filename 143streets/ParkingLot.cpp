#include "ParkingLot.h"

#include <fstream>
#include <iomanip>
#include <iostream>
#include <sstream>

ParkingSlot::ParkingSlot(int slotNumber) : slotNumber(slotNumber), occupied(false) {}

int ParkingSlot::getSlotNumber() const {
    return slotNumber;
}

bool ParkingSlot::isOccupied() const {
    return occupied;
}

void ParkingSlot::occupy() {
    occupied = true;
}

void ParkingSlot::free() {
    occupied = false;
}

ParkingLot::ParkingLot(int capacity)
    : totalEarnings(0.0),
      activeFileName("active_records.txt"),
      historyFileName("history_records.txt"),
      revenueFileName("revenue_records.txt") {
    if (capacity < 1) {
        capacity = 20;
    }
    for (int i = 1; i <= capacity; ++i) {
        slots.emplace_back(i);
    }
    rebuildAvailableSlots();
}

void ParkingLot::rebuildAvailableSlots() {
    std::queue<int> empty;
    std::swap(availableSlots, empty);
    for (const ParkingSlot& slot : slots) {
        if (!slot.isOccupied()) {
            availableSlots.push(slot.getSlotNumber());
        }
    }
}

bool ParkingLot::addVehicle(const std::string& vehicleNumber, const std::string& vehicleType, std::string& message) {
    if (vehicleNumber.empty()) {
        message = "Vehicle number cannot be empty.";
        return false;
    }

    if (activeTickets.find(vehicleNumber) != activeTickets.end()) {
        message = "This vehicle is already parked.";
        return false;
    }

    std::unique_ptr<Vehicle> vehicle = Vehicle::createVehicle(vehicleType, vehicleNumber);
    if (!vehicle) {
        message = "Invalid vehicle type. Use Car, Bike, or Bus.";
        return false;
    }

    if (availableSlots.empty()) {
        message = "Parking lot is full.";
        return false;
    }

    int slotNumber = availableSlots.front();
    availableSlots.pop();
    slots[slotNumber - 1].occupy();
    slotVehicleMap[slotNumber] = vehicleNumber;
    activeTickets.emplace(vehicleNumber, Ticket(slotNumber, std::move(vehicle), std::time(nullptr)));
    saveRecords();

    std::ostringstream output;
    output << "Vehicle parked successfully at slot " << slotNumber << ".";
    message = output.str();
    return true;
}

ExitReceipt ParkingLot::removeVehicle(const std::string& vehicleNumber) {
    ExitReceipt receipt{};
    receipt.success = false;

    auto ticketIterator = activeTickets.find(vehicleNumber);
    if (ticketIterator == activeTickets.end()) {
        receipt.message = "Vehicle not found in active parking records.";
        return receipt;
    }

    std::time_t exitTime = std::time(nullptr);
    Ticket& ticket = ticketIterator->second;
    receipt.success = true;
    receipt.message = "Vehicle exited successfully.";
    receipt.vehicleNumber = ticket.getVehicle().getNumber();
    receipt.vehicleType = ticket.getVehicle().getType();
    receipt.slotNumber = ticket.getSlotNumber();
    receipt.entryTime = ticket.getEntryTime();
    receipt.exitTime = exitTime;
    receipt.durationMinutes = ticket.getDurationMinutes(exitTime);
    receipt.billableHours = ticket.getBillableHours(exitTime);
    receipt.fee = ticket.calculateFee(exitTime);

    totalEarnings += receipt.fee;
    dailyRevenue[getDateKey(exitTime)] += receipt.fee;
    slots[receipt.slotNumber - 1].free();
    slotVehicleMap.erase(receipt.slotNumber);
    activeTickets.erase(ticketIterator);
    rebuildAvailableSlots();
    appendHistoryRecord(receipt);
    saveRecords();
    return receipt;
}

const Ticket* ParkingLot::searchByVehicleNumber(const std::string& vehicleNumber) const {
    auto ticketIterator = activeTickets.find(vehicleNumber);
    if (ticketIterator == activeTickets.end()) {
        return nullptr;
    }
    return &ticketIterator->second;
}

const Ticket* ParkingLot::searchBySlot(int slotNumber) const {
    auto mapIterator = slotVehicleMap.find(slotNumber);
    if (mapIterator == slotVehicleMap.end()) {
        return nullptr;
    }
    return searchByVehicleNumber(mapIterator->second);
}

void ParkingLot::showAvailableSlots() const {
    std::cout << "\nAvailable Slots\n";
    std::cout << "---------------\n";
    bool found = false;
    for (const ParkingSlot& slot : slots) {
        if (!slot.isOccupied()) {
            std::cout << "Slot " << slot.getSlotNumber() << "\n";
            found = true;
        }
    }
    if (!found) {
        std::cout << "No slots available.\n";
    }
}

void ParkingLot::showOccupiedSlots() const {
    std::cout << "\nOccupied Slots\n";
    std::cout << "--------------\n";
    if (activeTickets.empty()) {
        std::cout << "No vehicles are currently parked.\n";
        return;
    }

    std::cout << std::left << std::setw(10) << "Slot"
              << std::setw(18) << "Vehicle"
              << std::setw(10) << "Type"
              << "Entry Time\n";

    for (const auto& record : activeTickets) {
        const Ticket& ticket = record.second;
        std::cout << std::left << std::setw(10) << ticket.getSlotNumber()
                  << std::setw(18) << ticket.getVehicle().getNumber()
                  << std::setw(10) << ticket.getVehicle().getType()
                  << ticket.getFormattedEntryTime() << "\n";
    }
}

void ParkingLot::showParkingHistory() const {
    std::ifstream file(historyFileName);
    std::cout << "\nParking History\n";
    std::cout << "---------------\n";
    if (!file) {
        std::cout << "No completed parking history found.\n";
        return;
    }

    std::string line;
    bool found = false;
    while (std::getline(file, line)) {
        if (!line.empty()) {
            std::cout << line << "\n";
            found = true;
        }
    }
    if (!found) {
        std::cout << "No completed parking history found.\n";
    }
}

void ParkingLot::showDailyRevenue() const {
    std::cout << "\nDaily Revenue\n";
    std::cout << "-------------\n";
    if (dailyRevenue.empty()) {
        std::cout << "No revenue recorded yet.\n";
        return;
    }

    for (const auto& record : dailyRevenue) {
        std::cout << record.first << " : " << std::fixed << std::setprecision(2) << record.second << "\n";
    }
}

int ParkingLot::getTotalCapacity() const {
    return static_cast<int>(slots.size());
}

int ParkingLot::getOccupiedCount() const {
    return static_cast<int>(activeTickets.size());
}

int ParkingLot::getAvailableCount() const {
    return getTotalCapacity() - getOccupiedCount();
}

double ParkingLot::getTotalEarnings() const {
    return totalEarnings;
}

void ParkingLot::loadRecords() {
    activeTickets.clear();
    slotVehicleMap.clear();
    totalEarnings = 0.0;
    dailyRevenue.clear();

    for (ParkingSlot& slot : slots) {
        slot.free();
    }

    std::ifstream activeFile(activeFileName);
    std::string line;
    while (std::getline(activeFile, line)) {
        std::stringstream reader(line);
        std::string slotText;
        std::string number;
        std::string type;
        std::string entryText;

        if (std::getline(reader, slotText, '|') &&
            std::getline(reader, number, '|') &&
            std::getline(reader, type, '|') &&
            std::getline(reader, entryText)) {
            int slotNumber = std::stoi(slotText);
            std::time_t entryTime = static_cast<std::time_t>(std::stoll(entryText));
            if (slotNumber >= 1 && slotNumber <= getTotalCapacity()) {
                std::unique_ptr<Vehicle> vehicle = Vehicle::createVehicle(type, number);
                if (vehicle) {
                    slots[slotNumber - 1].occupy();
                    slotVehicleMap[slotNumber] = number;
                    activeTickets.emplace(number, Ticket(slotNumber, std::move(vehicle), entryTime));
                }
            }
        }
    }

    std::ifstream revenueFile(revenueFileName);
    while (std::getline(revenueFile, line)) {
        std::stringstream reader(line);
        std::string date;
        std::string amountText;
        if (std::getline(reader, date, '|') && std::getline(reader, amountText)) {
            double amount = std::stod(amountText);
            dailyRevenue[date] = amount;
            totalEarnings += amount;
        }
    }

    rebuildAvailableSlots();
}

void ParkingLot::saveRecords() const {
    writeActiveRecords();
    writeRevenueRecords();
}

void ParkingLot::writeActiveRecords() const {
    std::ofstream file(activeFileName);
    for (const auto& record : activeTickets) {
        const Ticket& ticket = record.second;
        file << ticket.getSlotNumber() << "|"
             << ticket.getVehicle().getNumber() << "|"
             << ticket.getVehicle().getType() << "|"
             << ticket.getEntryTime() << "\n";
    }
}

void ParkingLot::appendHistoryRecord(const ExitReceipt& receipt) const {
    std::ofstream file(historyFileName, std::ios::app);
    file << "Vehicle: " << receipt.vehicleNumber
         << " | Type: " << receipt.vehicleType
         << " | Slot: " << receipt.slotNumber
         << " | Entry: " << Ticket::formatTime(receipt.entryTime)
         << " | Exit: " << Ticket::formatTime(receipt.exitTime)
         << " | Duration: " << receipt.durationMinutes << " minute(s)"
         << " | Fee: " << std::fixed << std::setprecision(2) << receipt.fee << "\n";
}

void ParkingLot::writeRevenueRecords() const {
    std::ofstream file(revenueFileName);
    for (const auto& record : dailyRevenue) {
        file << record.first << "|" << std::fixed << std::setprecision(2) << record.second << "\n";
    }
}

std::string ParkingLot::getDateKey(std::time_t timeValue) const {
    std::tm localTime;
#ifdef _WIN32
    localtime_s(&localTime, &timeValue);
#else
    localtime_r(&timeValue, &localTime);
#endif
    std::ostringstream output;
    output << std::put_time(&localTime, "%Y-%m-%d");
    return output.str();
}
