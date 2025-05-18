package com.lta.auditoriumsystem.Controller;

import com.lta.auditoriumsystem.Entities.Testimonio;
import com.lta.auditoriumsystem.Services.TestimonioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/testimonios")
public class TestimonioController {

    @Autowired
    private TestimonioService testimonioService;

    // Para mostrar formulario
    @GetMapping("/form")
    public String mostrarFormulario(Model model) {
        model.addAttribute("testimonio", new Testimonio());
        return "testimonios"; // vista testimonios.html
    }

    // Esto recibe el formulario y lo guarda en BD
    @PostMapping("/form")
    public String guardarDesdeFormulario(@ModelAttribute Testimonio testimonio) {
        testimonioService.saveTestimonio(testimonio);
        return "redirect:/";
    }

    //  Obtener todos los testimonios 
    @ResponseBody
    @GetMapping
    public List<Testimonio> getAll() {
        return testimonioService.getAll();
    }

    //  Crear testimonio desde Postman (API)
    @ResponseBody
    @PostMapping
    public ResponseEntity<String> createTestimonio(@RequestBody Testimonio testimonio) {
        testimonioService.saveTestimonio(testimonio);
        return ResponseEntity.ok("Testimonio creado exitosamente");
    }
}
