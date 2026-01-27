
package com.example.personalidad.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;



@Entity
public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "El nombre es obligatorio")
	private String nombre;

	@NotNull
	@Positive(message = "El precio debe ser mayor a 0")
	private Double precio;
	
	@NotBlank(message = "La descripción es obligatoria")
    @Column(length = 500)
    private String descripcion;

	@NotNull
	@Positive(message = "El stock debe ser mayor a 0")
	private Integer stock;

    public Producto() {
    }

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

    
}
