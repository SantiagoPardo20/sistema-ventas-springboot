package com.example.personalidad.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.personalidad.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
	List<Producto> findByNombreContainingIgnoreCase(String nombre);

}
