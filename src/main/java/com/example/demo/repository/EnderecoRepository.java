package com.example.demo.repository;

import com.example.demo.model.Endereco;
import com.example.demo.model.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EnderecoRepository extends JpaRepository<Endereco, UUID> {
    List<Endereco> findByUsuario(Usuario usuario);

    @Modifying
    @Transactional
    @Query("DELETE FROM Endereco e WHERE e.usuario.id = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") UUID usuarioId);
}
