package com.example.personalidad.Controller;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.personalidad.Repository.*;
import com.example.personalidad.Service.ReportePdfService;
import com.example.personalidad.model.*;

import jakarta.transaction.Transactional;
@Controller
@RequestMapping("/ventas")
@SessionAttributes("carrito")
@Transactional
public class VentaController {

	@Autowired
	private ReportePdfService reportePdfService;

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private VentaRepository ventaRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @ModelAttribute("carrito")
    public List<DetalleVenta> carrito() {
        return new java.util.ArrayList<>();
    }
    @PostMapping("/agregar")
    public String agregarAlCarrito(
            @RequestParam Long productoId,
            @RequestParam int cantidad,
            @ModelAttribute("carrito") List<DetalleVenta> carrito,
            RedirectAttributes ra) {

        Producto producto = productoRepo.findById(productoId).orElse(null);

        if (producto == null || producto.getStock() < cantidad) {
            ra.addFlashAttribute("mensaje", "Stock insuficiente");
            ra.addFlashAttribute("tipo", "danger");
            return "redirect:/ventas/nueva";
        }

        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecio(producto.getPrecio());

        carrito.add(detalle);

        ra.addFlashAttribute("mensaje", "Producto agregado");
        ra.addFlashAttribute("tipo", "success");

        return "redirect:/ventas/nueva";
    }

    @PostMapping("/finalizar")
    public String finalizarVenta(
            @ModelAttribute("carrito") List<DetalleVenta> carrito,
            Principal principal,
            SessionStatus status,
            RedirectAttributes ra) {

        if (carrito.isEmpty()) {
            ra.addFlashAttribute("mensaje", "No hay productos en la venta");
            ra.addFlashAttribute("tipo", "warning");
            return "redirect:/ventas/nueva";
        }

        Usuario usuario = usuarioRepo.findByEmail(principal.getName())
                .orElseThrow();

        Venta venta = new Venta();
        venta.setUsuario(usuario);

        double total = 0;

        for (DetalleVenta d : carrito) {
            Producto p = d.getProducto();
            p.setStock(p.getStock() - d.getCantidad());
            productoRepo.save(p);

            d.setVenta(venta);
            total += d.getPrecio() * d.getCantidad();
        }

        venta.setTotal(total);
        venta.setDetalles(carrito);

        ventaRepo.save(venta);

        status.setComplete(); // limpia carrito

        ra.addFlashAttribute("mensaje", "Venta realizada correctamente");
        ra.addFlashAttribute("tipo", "success");

        return "redirect:/ventas";
    }
    @PostMapping("/quitar")
    public String quitarDelCarrito(
            @RequestParam int index,
            @ModelAttribute("carrito") List<DetalleVenta> carrito) {

        if (index >= 0 && index < carrito.size()) {
            carrito.remove(index);
        }
        return "redirect:/ventas/nueva";
    }


    // 🔹 LISTAR VENTAS
    @GetMapping
    public String listarVentas(Model model, Principal principal) {

        Usuario usuario = usuarioRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean esAdmin = usuario.getRol().equals("ADMIN");

        if (esAdmin) {
            // 👑 ADMIN VE TODO
            model.addAttribute("ventas", ventaRepo.findAll());
        } else {
            // 👤 EMPLEADO VE SOLO SUS VENTAS
            model.addAttribute("ventas", ventaRepo.findByUsuario(usuario));
        }

        return "ventas/listar";
    }

    @GetMapping("/nueva")
    public String nuevaVenta(
            @RequestParam(required = false) String q,
            Model model) {

        List<Producto> productos = (q == null || q.isEmpty())
                ? productoRepo.findAll()
                : productoRepo.findByNombreContainingIgnoreCase(q);

        model.addAttribute("productos", productos);
        model.addAttribute("q", q);

        return "ventas/nueva";
    }



    // 🔹 GUARDAR VENTA
    @PostMapping("/guardar")
    public String guardarVenta(
            @RequestParam Long productoId,
            @RequestParam int cantidad,
            Principal principal,
            RedirectAttributes ra) {

        Producto producto = productoRepo.findById(productoId).orElse(null);

        if (producto == null || producto.getStock() < cantidad) {
            ra.addFlashAttribute("mensaje", "Stock insuficiente");
            ra.addFlashAttribute("tipo", "danger");
            return "redirect:/ventas/nueva";
        }

        Usuario usuario = usuarioRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setTotal(producto.getPrecio() * cantidad);

        DetalleVenta detalle = new DetalleVenta();
        detalle.setVenta(venta);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecio(producto.getPrecio());

        venta.setDetalles(List.of(detalle));

        producto.setStock(producto.getStock() - cantidad);
        productoRepo.save(producto);

        ventaRepo.save(venta);

        ra.addFlashAttribute("mensaje", "Venta registrada correctamente");
        ra.addFlashAttribute("tipo", "success");

        return "redirect:/ventas";
    }
 // 🔍 VER DETALLE DE UNA VENTA
    @GetMapping("/detalle/{id}")
    public String detalleVenta(@PathVariable Long id, Model model) {
        Venta venta = ventaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        model.addAttribute("venta", venta);
        model.addAttribute("detalles", venta.getDetalles());
        return "ventas/detalle";
    }
    @GetMapping("/mis-ventas")
    public String ventasPorUsuario(Model model, Principal principal) {

        Usuario usuario = usuarioRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Venta> ventas = ventaRepo.findByUsuario(usuario);

        model.addAttribute("ventas", ventas);

        return "ventas/mis-ventas";
    }
   
    @GetMapping("/reporte/mis-ventas")
    public ResponseEntity<?> reporteMisVentas(Principal principal) {

        Usuario usuario = usuarioRepo.findByEmail(principal.getName())
                .orElseThrow();

        List<Venta> ventas = ventaRepo.findByUsuario(usuario);

        ByteArrayInputStream pdf = reportePdfService.ventasPdf(
                ventas,
                "Reporte de Mis Ventas"
        );

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=mis_ventas.pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(new org.springframework.core.io.InputStreamResource(pdf));
    }
    @GetMapping("/reporte/todas")
    public ResponseEntity<?> reporteTodasLasVentas() {

        List<Venta> ventas = ventaRepo.findAll();

        ByteArrayInputStream pdf = reportePdfService.ventasPdf(
                ventas,
                "Reporte General de Ventas"
        );

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=ventas_totales.pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(new org.springframework.core.io.InputStreamResource(pdf));
    }



}
