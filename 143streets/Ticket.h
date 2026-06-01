#ifndef TICKET_H
#define TICKET_H

#include "Vehicle.h"

#include <ctime>
#include <memory>
#include <string>

class Ticket {
private:
    int slotNumber;
    std::unique_ptr<Vehicle> vehicle;
    std::time_t entryTime;

public:
    Ticket(int slotNumber, std::unique_ptr<Vehicle> vehicle, std::time_t entryTime);
    Ticket(Ticket&& other) noexcept;
    Ticket& operator=(Ticket&& other) noexcept;
    Ticket(const Ticket& other) = delete;
    Ticket& operator=(const Ticket& other) = delete;

    int getSlotNumber() const;
    const Vehicle& getVehicle() const;
    std::time_t getEntryTime() const;
    long getDurationMinutes(std::time_t exitTime) const;
    int getBillableHours(std::time_t exitTime) const;
    double calculateFee(std::time_t exitTime) const;
    std::string getFormattedEntryTime() const;
    static std::string formatTime(std::time_t timeValue);
};

#endif
