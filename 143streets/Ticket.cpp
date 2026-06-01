#include "Ticket.h"

#include <iomanip>
#include <sstream>

Ticket::Ticket(int slotNumber, std::unique_ptr<Vehicle> vehicle, std::time_t entryTime)
    : slotNumber(slotNumber), vehicle(std::move(vehicle)), entryTime(entryTime) {}

Ticket::Ticket(Ticket&& other) noexcept
    : slotNumber(other.slotNumber), vehicle(std::move(other.vehicle)), entryTime(other.entryTime) {}

Ticket& Ticket::operator=(Ticket&& other) noexcept {
    if (this != &other) {
        slotNumber = other.slotNumber;
        vehicle = std::move(other.vehicle);
        entryTime = other.entryTime;
    }
    return *this;
}

int Ticket::getSlotNumber() const {
    return slotNumber;
}

const Vehicle& Ticket::getVehicle() const {
    return *vehicle;
}

std::time_t Ticket::getEntryTime() const {
    return entryTime;
}

long Ticket::getDurationMinutes(std::time_t exitTime) const {
    double seconds = std::difftime(exitTime, entryTime);
    if (seconds < 0) {
        seconds = 0;
    }
    return static_cast<long>(seconds / 60);
}

int Ticket::getBillableHours(std::time_t exitTime) const {
    long minutes = getDurationMinutes(exitTime);
    if (minutes == 0) {
        return 1;
    }
    return static_cast<int>((minutes + 59) / 60);
}

double Ticket::calculateFee(std::time_t exitTime) const {
    return getBillableHours(exitTime) * vehicle->getHourlyRate();
}

std::string Ticket::getFormattedEntryTime() const {
    return formatTime(entryTime);
}

std::string Ticket::formatTime(std::time_t timeValue) {
    std::tm localTime;
#ifdef _WIN32
    localtime_s(&localTime, &timeValue);
#else
    localtime_r(&timeValue, &localTime);
#endif
    std::ostringstream output;
    output << std::put_time(&localTime, "%Y-%m-%d %H:%M:%S");
    return output.str();
}
