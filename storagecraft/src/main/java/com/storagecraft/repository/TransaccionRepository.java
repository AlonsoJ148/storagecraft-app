package com.storagecraft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.storagecraft.model.Transaccion;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {

    // Método para listar de más reciente a más antiguo
    List<Transaccion> findAllByOrderByIdDesc();
}