package uhk.projekt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uhk.projekt.model.Car;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CarController {
    List<Car> cars = new ArrayList<Car>();

    @GetMapping("/")
    public String index(Model model) {
        if(cars.isEmpty()) {
            cars.add(new Car("3AX1683", "Red", "20l", "4"));
        }

        model.addAttribute("cars", cars);

        return "index";
    }
}
