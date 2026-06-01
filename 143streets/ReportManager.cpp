#include "ReportManager.h"

#include <iomanip>
#include <iostream>

void ReportManager::showSummaryReport(const ParkingLot& parkingLot) const {
    std::cout << "\nParking Lot Report\n";
    std::cout << "------------------\n";
    std::cout << "Total Capacity        : " << parkingLot.getTotalCapacity() << "\n";
    std::cout << "Total Parked Vehicles : " << parkingLot.getOccupiedCount() << "\n";
    std::cout << "Current Active Vehicles: " << parkingLot.getOccupiedCount() << "\n";
    std::cout << "Available Slots       : " << parkingLot.getAvailableCount() << "\n";
    std::cout << "Total Earnings        : " << std::fixed << std::setprecision(2) << parkingLot.getTotalEarnings() << "\n";
}
