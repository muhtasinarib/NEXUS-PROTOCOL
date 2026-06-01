#ifndef VEHICLE_H
#define VEHICLE_H

#include <memory>
#include <string>

class Vehicle {
private:
    std::string number;

public:
    explicit Vehicle(const std::string& vehicleNumber);
    virtual ~Vehicle();
    std::string getNumber() const;
    virtual std::string getType() const = 0;
    virtual double getHourlyRate() const = 0;
    static std::unique_ptr<Vehicle> createVehicle(const std::string& type, const std::string& number);
};

class Car : public Vehicle {
public:
    explicit Car(const std::string& vehicleNumber);
    std::string getType() const override;
    double getHourlyRate() const override;
};

class Bike : public Vehicle {
public:
    explicit Bike(const std::string& vehicleNumber);
    std::string getType() const override;
    double getHourlyRate() const override;
};

class Bus : public Vehicle {
public:
    explicit Bus(const std::string& vehicleNumber);
    std::string getType() const override;
    double getHourlyRate() const override;
};

#endif
