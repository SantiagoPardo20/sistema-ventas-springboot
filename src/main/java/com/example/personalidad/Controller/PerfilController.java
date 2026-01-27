package com.example.personalidad.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.personalidad.Repository.UsuarioRepository;
import com.example.personalidad.Repository.VentaRepository;
import com.example.personalidad.model.*;

@Controller
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private VentaRepository ventaRepo;

    @GetMapping("/perfil")
    public String perfil(Model model, Principal principal) {

        Usuario usuario = usuarioRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Venta> ventas = ventaRepo.findByUsuario(usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("ventas", ventas);

        return "perfil/index";
    }
}