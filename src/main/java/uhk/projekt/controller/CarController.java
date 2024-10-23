package uhk.projekt.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uhk.projekt.model.Car;
import uhk.projekt.service.CarService;

import java.util.ArrayList;

@Controller
@RequestMapping("/cars/")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(@Qualifier("carService") CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/")
    public String index(Model model) {
        ArrayList<Car> cars = carService.getAllCars();
        if(cars.isEmpty()) {
            cars.add(new Car("3AX1683", "Red", 20, 4));
        }

        model.addAttribute("cars", cars);

        return "car_index";
    }

    @GetMapping("/detail/{index}")
    public String detail(Model model, @PathVariable int index) {
        Car car = carService.getCarById(index);
        if(car != null) {
            model.addAttribute("car", car);
            return "car_detail";
        }

        return "redirect:/cars/";
    }

    @GetMapping("/delete/{index}")
    public String delete(Model model, @PathVariable int index) {
        carService.deleteCarById(index);
        return "redirect:/cars/";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("car", new Car());
        model.addAttribute("edit", false);
        return "car_edit";
    }

    @GetMapping("/edit/{index}")
    public String edit(Model model, @PathVariable int index) {
        Car car = carService.getCarById(index);
        if(car != null) {
            car.setId(index);
            model.addAttribute("car", car);
            model.addAttribute("edit", true);
            return "car_edit";
        }

        return "redirect:/cars/";
    }

    @PostMapping("/save")
    public String save(@Valid Car car, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("edit", true);
            return "car_edit";
        }
        carService.saveCar(car);
        return "redirect:/cars/";
    }
}
