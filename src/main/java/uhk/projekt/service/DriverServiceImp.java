package uhk.projekt.service;

import org.springframework.stereotype.Service;
import uhk.projekt.model.Driver;

import java.util.ArrayList;

@Service
public class DriverServiceImp implements DriverService {
    ArrayList<Driver> drivers = new ArrayList<>();

    @Override
    public ArrayList<Driver> getAllDrivers() {
        return drivers;
    }

    @Override
    public Driver getDriverById(int id) {
        if(id > -1 && id < drivers.size()) {
            return drivers.get(id);
        }
        return null;
    }

    @Override
    public void deleteDriverById(int id) {
        if(id > -1 && id < drivers.size()) {
            drivers.remove(id);
        }
    }

    @Override
    public void saveDriver(Driver driver) {
        if(driver.getId() > -1) {
            drivers.remove(driver.getId());
        }
        drivers.add(driver);
    }
}
