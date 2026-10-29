package com.storagecraft.service;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.storagecraft.model.Dispositivo;
import com.storagecraft.repository.DispositivoRepository;

@Service
public class DispositivoService {

    private final DispositivoRepository dispositivoRepository;
    private final JdbcTemplate jdbcTemplate;

    public DispositivoService(DispositivoRepository dispositivoRepository, JdbcTemplate jdbcTemplate) {
        this.dispositivoRepository = dispositivoRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    // Listar sigue leyendo de la vista 'vw_dispositivos_redstone' sin problemas
    @Transactional(readOnly = true)
    public List<Dispositivo> listarTodos() {
        return dispositivoRepository.findAll();
    }

    // Actualiza directamente en la tabla física DISPOSITIVOS_REDSTONE
    @Transactional
    public void alternarSenal(Integer id) {
        // 1. Conmutar el estado en la tabla física real
        String sqlUpdate = "UPDATE DISPOSITIVOS_REDSTONE " +
                           "SET estado_operativo = CASE " +
                           "    WHEN estado_operativo = 'Activo' THEN 'Inactivo' " +
                           "    ELSE 'Activo' " +
                           "END " +
                           "WHERE id_dispositivo = ?";
        jdbcTemplate.update(sqlUpdate, id);

        // 2. Registrar el evento técnico en CONTENEDOR_DATO_LOG para la auditoría
        String sqlLog = "INSERT INTO CONTENEDOR_DATO_LOG (id_dispositivo, tipo_evento, mensaje_detalle) " +
                        "VALUES (?, 'CAMBIO_ESTADO', 'Conmutación manual de señal redstone desde el panel web')";
        try {
            jdbcTemplate.update(sqlLog, id);
        } catch (Exception e) {
            System.err.println("Aviso al registrar log técnico: " + e.getMessage());
        }
    }
}