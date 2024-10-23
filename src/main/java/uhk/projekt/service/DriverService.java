package uhk.projekt.service;

import org.springframework.stereotype.Service;
import uhk.projekt.model.Car;
import uhk.projekt.model.Driver;

import java.util.ArrayList;

@Service
public interface DriverService {
    ArrayList<Driver> getAllDrivers();
    Driver getDriverById(int id);
    void deleteDriverById(int id);
    void saveDriver(Driver driver);
}
