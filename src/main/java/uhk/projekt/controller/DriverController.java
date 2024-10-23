package uhk.projekt.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uhk.projekt.model.Car;
import uhk.projekt.model.Driver;
import uhk.projekt.service.DriverService;
import uhk.projekt.service.DriverServiceImp;

import java.util.ArrayList;

@Controller
@RequestMapping("/drivers/")
public class DriverController {
    private final DriverService driverService;

    @Autowired
    public DriverController(@Qualifier("driverService") DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("/")
    public String index(Model model) {
        ArrayList<Driver> drivers = driverService.getAllDrivers();

        model.addAttribute("drivers", drivers);

        return "car_index";
    }

    @GetMapping("/detail/{index}")
    public String detail(Model model, @PathVariable int index) {
        Driver driver = driverService.getDriverById(index);
        if(driver != null) {
            model.addAttribute("driver", driver);
            return "car_detail";
        }

        return "redirect:/drivers/";
    }

    @GetMapping("/delete/{index}")
    public String delete(Model model, @PathVariable int index) {
        driverService.deleteDriverById(index);
        return "redirect:/drivers/";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("driver", new Car());
        model.addAttribute("edit", false);
        return "car_edit";
    }

    @GetMapping("/edit/{index}")
    public String edit(Model model, @PathVariable int index) {
        Driver driver = driverService.getDriverById(index);
        if(driver != null) {
            driver.setId(index);
            model.addAttribute("car", driver);
            model.addAttribute("edit", true);
            return "car_edit";
        }

        return "redirect:/drivers/";
    }

    @PostMapping("/save")
    public String save(@Valid Driver driver, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("edit", true);
            return "driver_edit";
        }
        driverService.saveDriver(driver);
        return "redirect:/drivers/";
    }

}
