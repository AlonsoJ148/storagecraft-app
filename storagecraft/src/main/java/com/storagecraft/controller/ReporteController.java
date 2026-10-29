package com.storagecraft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    // Opción 1 del menú: Barras y Círculo
    @GetMapping("/inventario")
    public String reporteInventario() {
        return "paginas/graficos-inventario";
    }

    // Opción 2 del menú: Gráfico Lineal
    @GetMapping("/movimientos")
    public String reporteMovimientos() {
        return "paginas/graficos-movimientos";
    }
}