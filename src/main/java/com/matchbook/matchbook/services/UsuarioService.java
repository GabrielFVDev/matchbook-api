package com.matchbook.matchbook.services;

import com.matchbook.matchbook.entities.UsuarioEntity;
import com.matchbook.matchbook.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    public UsuarioEntity createUsuario(UsuarioEntity usuario) {
        if (usuario.getFotoUrl() == null || usuario.getFotoUrl().isEmpty()) {
            usuario.setFotoUrl(generateDefaultPhoto(usuario.getNome()));
        }
        return usuarioRepository.save(usuario);
    }

    public UsuarioEntity updateUsuario(UsuarioEntity usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deleteUsuario(Integer id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<UsuarioEntity> getUsuario(@RequestParam Integer id) {
        return usuarioRepository.findById(id);
    }

    public List<UsuarioEntity> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public UsuarioEntity updateFotoUsuario(Integer id, String fotoUrl) {
        Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            UsuarioEntity usuario = usuarioOpt.get();
            usuario.setFotoUrl(fotoUrl);
            return usuarioRepository.save(usuario);
        }
        throw new RuntimeException("Usuário não encontrado");
    }

    private String generateDefaultPhoto(String nome) {
        if (nome != null && !nome.isEmpty()) {
            String primeiraLetra = nome.substring(0, 1).toUpperCase();
            return String.format("https://ui-avatars.com/api/?name=%s&background=random&color=fff&size=200", primeiraLetra);
        }
        return "https://ui-avatars.com/api/?name=?&background=random&color=fff&size=200";
    }
}
