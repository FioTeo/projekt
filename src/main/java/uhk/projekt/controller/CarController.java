package uhk.projekt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uhk.projekt.model.Car;
import uhk.projekt.service.CarService;

import java.util.ArrayList;

@Controller
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
            cars.add(new Car("3AX1683", "Red", "20l", "4"));
        }

        model.addAttribute("cars", cars);

        return "index";
    }

    @GetMapping("/detail/{index}")
    public String detail(Model model, @PathVariable int index) {
        Car car = carService.getCarById(index);
        if(car != null) {
            model.addAttribute("car", car);
            return "detail";
        }

        return "redirect:/";
    }

    @GetMapping("/delete/{index}")
    public String delete(Model model, @PathVariable int index) {
        carService.deleteCarById(index);
        return "redirect:/";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("car", new Car());
        model.addAttribute("edit", false);
        return "edit";
    }

    @GetMapping("/edit/{index}")
    public String edit(Model model, @PathVariable int index) {
        Car car = carService.getCarById(index);
        if(car != null) {
            car.setId(index);
            model.addAttribute("car", car);
            model.addAttribute("edit", true);
            return "edit";
        }

        return "redirect:/";
    }

    @PostMapping("/save")
    public String save(Model model, @ModelAttribute Car car) {
        carService.saveCar(car);
        return "redirect:/";
    }
}
