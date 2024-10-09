package uhk.projekt.service;

import org.springframework.stereotype.Service;
import uhk.projekt.model.Car;

import java.util.ArrayList;

@Service
public class CarServiceImp implements CarService {

    ArrayList<Car> cars = new ArrayList<>();

    @Override
    public ArrayList<Car> getAllCars() {
        return cars;
    }

    @Override
    public Car getCarById(int id) {
        if(id > -1 && id < cars.size()) {
            return cars.get(id);
        }
        return null;
    }

    @Override
    public void deleteCarById(int id) {
        if(id > -1 && id < cars.size()) {
            cars.remove(id);
        }
    }

    @Override
    public void saveCar(Car car) {
        if(car.getId() > -1) {
            cars.remove(car.getId());
        }
        cars.add(car);
    }
}
