package com.example.clinica.controller;

import com.example.clinica.entity.Mascota;
import com.example.clinica.repository.DuenoRepository;
import com.example.clinica.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private DuenoRepository duenoRepository;

    @GetMapping(value = {"", "/"})
    public String listaMascotas(Model model) {
        model.addAttribute("lista", mascotaRepository.findAll());
        return "mascotas/lista";
    }

    @GetMapping("/nuevo")
    public String nuevaMascota(Model model) {
        model.addAttribute("listaDuenos", duenoRepository.findAll());
        return "mascotas/formulario";
    }

    @PostMapping("/guardar")
    public String guardarMascota(Mascota mascota, RedirectAttributes redirectAttributes) {
        mascota.setEstado(true); // asumiendo activo por defecto
        mascotaRepository.save(mascota);
        
        // Uso de RedirectAttributes para el mensaje Flash
        redirectAttributes.addFlashAttribute("mensaje", "La mascota '" + mascota.getNombre() + "' fue guardada exitosamente!");
        
        return "redirect:/mascotas";
    }
}
