package com.storagecraft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.storagecraft.service.TransaccionService;

@Controller
public class DashboardController {

    private final TransaccionService transaccionService;

    public DashboardController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    @GetMapping("/")
    public String index() {
        return "paginas/index";
    }

    @GetMapping("/transacciones")
    public String transacciones(Model model) {
        model.addAttribute("transacciones", transaccionService.listarTodas());
        return "paginas/transacciones";
    }
}