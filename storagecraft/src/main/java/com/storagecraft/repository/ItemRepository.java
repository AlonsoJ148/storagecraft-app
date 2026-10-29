package com.storagecraft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.storagecraft.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Procedure(procedureName = "sp_adicionar_item")
    Integer registrarItemConSP(
        @Param("p_nombre") String nombre,
        @Param("p_categoria") String categoria,
        @Param("p_cantidad") Integer cantidad,
        @Param("p_ubicacion") String ubicacion
    );

    List<Item> findByNombreContainingIgnoreCaseOrCategoriaContainingIgnoreCase(String nombre, String categoria);
}