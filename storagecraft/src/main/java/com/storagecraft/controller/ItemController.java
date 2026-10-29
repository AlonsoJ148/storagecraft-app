package com.storagecraft.controller;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.storagecraft.model.Item;
import com.storagecraft.service.ItemService;
import com.storagecraft.service.TransaccionService;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final TransaccionService transaccionService;

    public ItemController(ItemService itemService, TransaccionService transaccionService) {
        this.itemService = itemService;
        this.transaccionService = transaccionService;
    }

    // 1. Catálogo principal e inventario
    @GetMapping
    public String listar(@RequestParam(value = "criterio", required = false) String criterio, Model model) {
        if (criterio != null && !criterio.trim().isEmpty()) {
            model.addAttribute("items", itemService.buscar(criterio));
        } else {
            model.addAttribute("items", itemService.listarTodos()); // <--- Carga todos los registros
        }
        model.addAttribute("criterio", criterio);
        model.addAttribute("nuevoItem", new Item());
        return "paginas/inventario";
    }


    // 2. Pantalla de consulta y filtros
    @GetMapping("/consultar")
    public String consultar(@RequestParam(value = "criterio", required = false) String criterio, Model model) {
        model.addAttribute("items", itemService.buscar(criterio));
        model.addAttribute("criterio", criterio);
        return "paginas/consultar";
    }

    // 3. Guardar o acumular ítem 

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("nuevoItem") Item item, Principal principal, RedirectAttributes redirectAttrs) {
        // Obtiene el nombre del usuario logueado desde la sesión; si no hay sesión, asume "Steve"
        String usuarioActual = (principal != null) ? principal.getName() : "Steve";

        boolean guardado = itemService.adicionar(item, usuarioActual);
        
        if (!guardado) {
            redirectAttrs.addFlashAttribute("errorCapacidad", "¡Error! Se excede el límite técnico de 3,456 ítems.");
        } else {
            redirectAttrs.addFlashAttribute("exito", "Material almacenado correctamente.");
        }
        return "redirect:/items";
    }

    // 4. Eliminar material por ID
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Integer id, RedirectAttributes redirectAttrs) {
        try {
            itemService.eliminar(id);
            redirectAttrs.addFlashAttribute("exito", "Material eliminado correctamente del almacén.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorCapacidad", "No se pudo eliminar el material: " + e.getMessage());
        }
        return "redirect:/items";
    }
}