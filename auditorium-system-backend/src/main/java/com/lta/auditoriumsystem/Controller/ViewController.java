package com.lta.auditoriumsystem.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import com.lta.auditoriumsystem.Services.TestimonioService;

@Controller
public class ViewController {

    @Autowired
    private TestimonioService testimonioService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("testimonios", testimonioService.getAll());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }
}