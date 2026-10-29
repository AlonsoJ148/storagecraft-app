package com.storagecraft.service;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.storagecraft.model.Item;
import com.storagecraft.repository.ItemRepository;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final JdbcTemplate jdbcTemplate;

    public ItemService(ItemRepository itemRepository, JdbcTemplate jdbcTemplate) {
        this.itemRepository = itemRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<Item> listarTodos() {
        return itemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Item> consultarPorId(Integer id) {
        return itemRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Item> buscar(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return itemRepository.findAll();
        }
        return itemRepository.findByNombreContainingIgnoreCaseOrCategoriaContainingIgnoreCase(criterio, criterio);
    }

    @Transactional
    public boolean adicionar(Item item, String nombreUsuario) {
        // 1. Ejecuta el procedimiento almacenado para guardar/actualizar el inventario
        Integer resultado = itemRepository.registrarItemConSP(
            item.getNombre(),
            item.getCategoria(),
            item.getCantidad(),
            item.getCofreUbicacion()
        );

        boolean guardadoExitoso = (resultado != null && resultado == 1);

        // 2. Si se guardó correctamente, registrar la transacción en el historial
        if (guardadoExitoso) {
            try {
                // Obtener ID del jugador
                Integer idJugador = jdbcTemplate.queryForObject(
                    "SELECT id_jugador FROM JUGADORES WHERE nombre_usuario = ? LIMIT 1",
                    Integer.class,
                    nombreUsuario
                );

                // Obtener ID del ítem recién procesado
                Integer idItem = jdbcTemplate.queryForObject(
                    "SELECT id_item FROM ITEMS WHERE nombre_item = ? LIMIT 1",
                    Integer.class,
                    item.getNombre()
                );

                // Insertar en la tabla física TRANSACCIONES_INVENTARIO
                if (idJugador != null && idItem != null) {
                    jdbcTemplate.update(
                        "INSERT INTO TRANSACCIONES_INVENTARIO (id_jugador, id_item, tipo_operacion, cantidad) VALUES (?, ?, 'INGRESO', ?)",
                        idJugador, idItem, item.getCantidad()
                    );
                }
            } catch (Exception e) {
                System.err.println("Aviso al registrar transacción de auditoría: " + e.getMessage());
            }
        }

        return guardadoExitoso;
    }

    @Transactional
    public void eliminar(Integer id) {
        jdbcTemplate.update("DELETE FROM TRANSACCIONES_INVENTARIO WHERE id_item = ?", id);
        jdbcTemplate.update("DELETE FROM INVENTARIO_ALMACEN WHERE id_item = ?", id);
        jdbcTemplate.update("DELETE FROM ITEMS WHERE id_item = ?", id);
    }
}