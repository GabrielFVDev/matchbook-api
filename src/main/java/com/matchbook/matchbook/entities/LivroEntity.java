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
@Table(name = "livros")
public class LivroEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "autor")
    private String autor;

    @Column(name = "editora")
    private String editora;

    @Column(name = "genero")
    private String genero;

    @Column(name = "ano_publicacao")
    private int anoPublicacao;

    @Column(name = "numero_paginas")
    private int numeroPaginas;

    @Column(name = "idioma")
    private String idioma;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "sinopse")
    private String sinopse;

    @Column(name = "capa_url")
    private String capa;

    @Column(name = "avaliacao_media")
    private double avaliacaoMedia;

    @Column(name = "disponivel")
    private boolean disponivel;

    @Column(name = "data_cadastro")
    private Date dataCadastro;

    @Column(name = "foto_url")
    private String fotoUrl;
}
