package com.example.personalidad.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.personalidad.Repository.UsuarioRepository;
import com.example.personalidad.model.Usuario;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // LISTAR
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", repo.findAll());
        return "usuarios/listar";
    }

    // NUEVO
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Usuario usuario, BindingResult result) {

        if (result.hasErrors()) {
            return "usuarios/form";
        }

        if (usuario.getId() != null) {
            Usuario original = repo.findById(usuario.getId()).orElseThrow();
            usuario.setPassword(original.getPassword());
        } else {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        repo.save(usuario);
        return "redirect:/usuarios";
    }


    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/usuarios";
    }
   

    @GetMapping("/estado/{id}")
    public String cambiarEstado(@PathVariable Long id) {

        Usuario usuario = repo.findById(id).orElseThrow();
        usuario.setActivo(!usuario.isActivo()); // 🔥 toggle

        repo.save(usuario);
        return "redirect:/usuarios";
    }


}
