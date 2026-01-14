package com.example.personalidad.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.personalidad.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
