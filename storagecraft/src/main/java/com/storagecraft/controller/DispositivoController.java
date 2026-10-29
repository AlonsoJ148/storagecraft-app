package com.storagecraft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.storagecraft.service.DispositivoService;

@Controller
@RequestMapping("/dispositivos")
public class DispositivoController {

    private final DispositivoService dispositivoService;

    public DispositivoController(DispositivoService dispositivoService) {
        this.dispositivoService = dispositivoService;
    }

    // Listar las tarjetas de dispositivos
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("dispositivos", dispositivoService.listarTodos());
        return "paginas/dispositivos";
    }

    // Acción al hacer clic en 'Alternar Señal'
    @GetMapping("/alternar/{id}")
    public String alternarSenal(@PathVariable("id") Integer id) {
        dispositivoService.alternarSenal(id);
        return "redirect:/dispositivos";
    }
}