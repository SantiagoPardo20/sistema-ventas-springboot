package com.example.personalidad.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.personalidad.Repository.UsuarioRepository;
import com.example.personalidad.Repository.VentaRepository;
import com.example.personalidad.model.Venta;


@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private VentaRepository ventaRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @GetMapping
    public String dashboard(Model model) {

        List<Venta> ventas = ventaRepo.findAll();

        double totalGeneral = ventas.stream()
                .mapToDouble(Venta::getTotal)
                .sum();

        long totalVentas = ventas.size();

        model.addAttribute("totalGeneral", totalGeneral);
        model.addAttribute("totalVentas", totalVentas);

        return "dashboard/index";
    }
}
