package com.example.personalidad.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.personalidad.Repository.ProductoRepository;
import com.example.personalidad.model.Producto;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/productos")
public class ProductoController {

	@Autowired
    private ProductoRepository repo;

    // LISTAR
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", repo.findAll());
        return "productos/listar";
    }

    // NUEVO
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        return "productos/form";
    }

    // GUARDAR (sirve para crear y editar)
    @PostMapping("/guardar")
    public String guardar(
        @Valid Producto producto,
        BindingResult result,
        RedirectAttributes ra
    ) {
        if (result.hasErrors()) {
            return "productos/form";
        }

        boolean editando = (producto.getId() != null);

        repo.save(producto);

        if (editando) {
            ra.addFlashAttribute("mensaje", "Producto editado correctamente");
            ra.addFlashAttribute("tipo", "warning");
        } else {
            ra.addFlashAttribute("mensaje", "Producto guardado correctamente");
            ra.addFlashAttribute("tipo", "success");
        }

        return "redirect:/productos";
    }



    // EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Producto producto = repo.findById(id).orElse(null);
        model.addAttribute("producto", producto);
        
        return "productos/form";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        repo.deleteById(id);
        ra.addFlashAttribute("mensaje", "Producto eliminado correctamente");
        ra.addFlashAttribute("tipo", "danger");
        return "redirect:/productos";
    }

}