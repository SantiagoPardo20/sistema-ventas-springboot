package com.example.personalidad.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.personalidad.model.Usuario;
import com.example.personalidad.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {
	List<Venta> findByUsuario(Usuario usuario);
	
}
