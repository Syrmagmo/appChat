package com.example.appchat;

import android.os.Bundle;

import com.example.appchat.Adapter.LivroAdapter;
import com.example.appchat.model.Livro;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LivroActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LivroAdapter livroAdapter;
    private List<Livro> listaLivros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livro);


        recyclerView = findViewById(R.id.rvRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaLivros = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("post").child("G5vxQHfg6SdolUW5D7UxBRDynxF2");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaLivros.clear(); // Limpa a lista antes de adicionar os livros atualizados

                for (DataSnapshot livroSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot livroDataSnapshot : livroSnapshot.getChildren()) {
                        Livro livro = livroDataSnapshot.getValue(Livro.class);
                        if (livro != null) {
                            livro.setLivroId(livroDataSnapshot.getKey());
                            livro.setIdUsuario(livroDataSnapshot.child("IdUsuario").getValue(String.class));
                            livro.setAno(livroDataSnapshot.child("ano").getValue(String.class));
                            livro.setAutor(livroDataSnapshot.child("autor").getValue(String.class));
                            livro.setDescricao(livroDataSnapshot.child("descricao").getValue(String.class));
                            livro.setEstado(livroDataSnapshot.child("estado").getValue(String.class));
                            livro.setFoto(livroDataSnapshot.child("foto").getValue(String.class));
                            livro.setGenero(livroDataSnapshot.child("genero").getValue(String.class));
                            livro.setTitulo(livroDataSnapshot.child("titulo").getValue(String.class));

                            listaLivros.add(livro);
                        }

                    }
                }


                livroAdapter.notifyDataSetChanged(); // Notifica o adapter sobre a mudança na lista de livros
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Trate o erro de carregamento dos dados, se necessário
            }
        });

        livroAdapter = new LivroAdapter(listaLivros);
        recyclerView.setAdapter(livroAdapter);
    }
}
