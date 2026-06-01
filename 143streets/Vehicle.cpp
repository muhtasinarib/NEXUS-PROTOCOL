#include "Vehicle.h"

#include <algorithm>

Vehicle::Vehicle(const std::string& vehicleNumber) : number(vehicleNumber) {}

Vehicle::~Vehicle() {}

std::string Vehicle::getNumber() const {
    return number;
}

Car::Car(const std::string& vehicleNumber) : Vehicle(vehicleNumber) {}

std::string Car::getType() const {
    return "Car";
}

double Car::getHourlyRate() const {
    return 50.0;
}

Bike::Bike(const std::string& vehicleNumber) : Vehicle(vehicleNumber) {}

std::string Bike::getType() const {
    return "Bike";
}

double Bike::getHourlyRate() const {
    return 20.0;
}

Bus::Bus(const std::string& vehicleNumber) : Vehicle(vehicleNumber) {}

std::string Bus::getType() const {
    return "Bus";
}

double Bus::getHourlyRate() const {
    return 100.0;
}

std::unique_ptr<Vehicle> Vehicle::createVehicle(const std::string& type, const std::string& number) {
    std::string normalized = type;
    std::transform(normalized.begin(), normalized.end(), normalized.begin(), ::tolower);

    if (normalized == "car") {
        return std::make_unique<Car>(number);
    }
    if (normalized == "bike") {
        return std::make_unique<Bike>(number);
    }
    if (normalized == "bus") {
        return std::make_unique<Bus>(number);
    }

    return nullptr;
}
