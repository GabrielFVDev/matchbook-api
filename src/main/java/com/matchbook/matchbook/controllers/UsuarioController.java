package com.matchbook.matchbook.controllers;

import com.matchbook.matchbook.entities.UsuarioEntity;
import com.matchbook.matchbook.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController("/api/usuario")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    public ResponseEntity createUsuario() {
        UsuarioEntity usuario = usuarioService.createUsuario();
        return ResponseEntity.ok(usuario);
    }

    public ResponseEntity updateUsuario(UsuarioEntity usuario) {
        UsuarioEntity updatedUsuario = usuarioService.updateUsuario(usuario);
        return ResponseEntity.ok(updatedUsuario);
    }

    public ResponseEntity deleteUsuario(Integer id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity getUsuario(@RequestParam Integer id) {
        Optional<UsuarioEntity> usuario = usuarioService.getUsuario(id);
        return ResponseEntity.of(usuario);
    }

    public ResponseEntity getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.getAllUsuarios());
    }
}
