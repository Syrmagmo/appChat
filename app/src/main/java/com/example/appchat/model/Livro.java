package com.example.appchat.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Livro {
    private String livroId;
    private String idUsuario;
    private String ano;
    private String autor;
    private String descricao;
    private String estado;
    private String foto;
    private String genero;
    private String titulo;

    public Livro() {
    }


    public Livro(String livroId, String idUsuario, String ano, String autor, String descricao, String estado, String foto, String genero, String titulo) {
        this.livroId = livroId;
        this.idUsuario = idUsuario;
        this.ano = ano;
        this.autor = autor;
        this.descricao = descricao;
        this.estado = estado;
        this.foto = foto;
        this.genero = genero;
        this.titulo = titulo;
    }

    // getters e setters

    public String getLivroId() {
        return livroId;
    }

    public void setLivroId(String livroId) {
        this.livroId = livroId;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo != null) {
            this.titulo = titulo;
        } else {
            this.titulo = ""; // Valor padrão quando o título é nulo
        }
    }
}
