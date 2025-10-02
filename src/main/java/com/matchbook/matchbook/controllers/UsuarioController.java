package com.matchbook.matchbook.controllers;

import com.matchbook.matchbook.entities.UsuarioEntity;
import com.matchbook.matchbook.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @PostMapping("/create")
    public ResponseEntity<UsuarioEntity> createUsuario(@RequestBody UsuarioEntity usuario) {
        UsuarioEntity novoUsuario = usuarioService.createUsuario(usuario);
        return ResponseEntity.ok(novoUsuario);
    }

    @PutMapping("/update")
    public ResponseEntity<UsuarioEntity> updateUsuario(@RequestBody UsuarioEntity usuario) {
        UsuarioEntity updatedUsuario = usuarioService.updateUsuario(usuario);
        return ResponseEntity.ok(updatedUsuario);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUsuario(@RequestParam Integer id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get")
    public ResponseEntity<UsuarioEntity> getUsuario(@RequestParam Integer id) {
        Optional<UsuarioEntity> usuario = usuarioService.getUsuario(id);
        return ResponseEntity.of(usuario);
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<UsuarioEntity>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.getAllUsuarios());
    }

    @PutMapping("/{id}/foto")
    public ResponseEntity<UsuarioEntity> updateFoto(@PathVariable Integer id, @RequestBody String fotoUrl) {
        UsuarioEntity usuario = usuarioService.updateFotoUsuario(id, fotoUrl);
        return ResponseEntity.ok(usuario);
    }
}
