package uhk.projekt.service;

import org.springframework.stereotype.Service;
import uhk.projekt.model.Car;

import java.util.ArrayList;

@Service
public interface CarService {

    ArrayList<Car> getAllCars();
    Car getCarById(int id);
    void deleteCarById(int id);
    void saveCar(Car car);
}
