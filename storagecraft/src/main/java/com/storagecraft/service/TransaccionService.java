package com.storagecraft.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.storagecraft.model.Transaccion;
import com.storagecraft.repository.TransaccionRepository;

@Service
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;

    public TransaccionService(TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
    }

    // 1. Listar todas las transacciones ordenadas de la más reciente a la más antigua
    @Transactional(readOnly = true)
    public List<Transaccion> listarTodas() {
        return transaccionRepository.findAllByOrderByIdDesc();
    }

    // 2. Registrar movimiento
    @Transactional
    public void registrarTransaccion(String usuario, String operacion, String item, Integer cantidad) {
        // La vista de MySQL 'vw_transacciones_inventario' ya formatea y proyecta las filas.
        // Si necesitas guardar directamente una entidad de auditoría, puedes instanciarla y persistirla:
        Transaccion t = new Transaccion();
        t.setUsuario(usuario);
        t.setTipo(operacion);
        t.setNombreItem(item);
        t.setCantidad(cantidad);
        
        try {
            transaccionRepository.save(t);
        } catch (Exception e) {
            // Previene que una falla de log detenga el flujo principal
            System.err.println("Aviso: No se pudo guardar la auditoría directa en la vista: " + e.getMessage());
        }
    }
}