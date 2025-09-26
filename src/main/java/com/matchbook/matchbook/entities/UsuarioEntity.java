package com.matchbook.matchbook.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


@Entity
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    private String senha;

    @Column(name = "data_nascimento")
    private String dataNascimento;

    @Column(name = "genero_favorito")
    private String generoFavorito;

    @Column(name = "livros_lidos")
    private int livrosLidos;

    @Column(name = "nivel_leitura")
    private String nivelLeitura;

    @Column(name = "receber_recomendacoes")
    private boolean receberRecomendacoes;

    @Column(name = "data_cadastro")
    private Date dataCadastro;

    @Column(name = "biografia")
    private String biografia;
}
