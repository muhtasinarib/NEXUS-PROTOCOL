#ifndef PARKING_LOT_H
#define PARKING_LOT_H

#include "Ticket.h"

#include <map>
#include <queue>
#include <string>
#include <vector>

class ParkingSlot {
private:
    int slotNumber;
    bool occupied;

public:
    explicit ParkingSlot(int slotNumber = 0);
    int getSlotNumber() const;
    bool isOccupied() const;
    void occupy();
    void free();
};

struct ExitReceipt {
    bool success;
    std::string message;
    std::string vehicleNumber;
    std::string vehicleType;
    int slotNumber;
    std::time_t entryTime;
    std::time_t exitTime;
    long durationMinutes;
    int billableHours;
    double fee;
};

class ParkingLot {
private:
    std::vector<ParkingSlot> slots;
    std::queue<int> availableSlots;
    std::map<std::string, Ticket> activeTickets;
    std::map<int, std::string> slotVehicleMap;
    double totalEarnings;
    std::map<std::string, double> dailyRevenue;
    std::string activeFileName;
    std::string historyFileName;
    std::string revenueFileName;

    void rebuildAvailableSlots();
    void writeActiveRecords() const;
    void appendHistoryRecord(const ExitReceipt& receipt) const;
    void writeRevenueRecords() const;
    std::string getDateKey(std::time_t timeValue) const;

public:
    explicit ParkingLot(int capacity = 20);
    bool addVehicle(const std::string& vehicleNumber, const std::string& vehicleType, std::string& message);
    ExitReceipt removeVehicle(const std::string& vehicleNumber);
    const Ticket* searchByVehicleNumber(const std::string& vehicleNumber) const;
    const Ticket* searchBySlot(int slotNumber) const;
    void showAvailableSlots() const;
    void showOccupiedSlots() const;
    void showParkingHistory() const;
    void showDailyRevenue() const;
    int getTotalCapacity() const;
    int getOccupiedCount() const;
    int getAvailableCount() const;
    double getTotalEarnings() const;
    void loadRecords();
    void saveRecords() const;
};

#endif
