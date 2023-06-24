package com.example.appchat.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.appchat.R;
import com.example.appchat.model.Livro;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LivroAdapter extends RecyclerView.Adapter<LivroAdapter.ViewHolder> {
    private List<Livro> listaLivros;

    public LivroAdapter(List<Livro> listaLivros) {
        this.listaLivros = listaLivros;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Livro livro = listaLivros.get(position);

        // Preencha os elementos do layout com os dados do livro
        holder.textTituloLivro.setText(livro.getTitulo());
        holder.textAutor.setText(livro.getAutor());
        holder.textAno.setText(String.valueOf(livro.getAno()));
        holder.textGenero.setText(livro.getGenero());

        Glide.with(holder.itemView.getContext())
                .load(livro.getFoto())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return listaLivros.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTituloLivro;
        TextView textAutor;
        TextView textAno;
        TextView textGenero;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTituloLivro = itemView.findViewById(R.id.textTituloLivro);
            textAutor = itemView.findViewById(R.id.TextAutor);
            textAno = itemView.findViewById(R.id.textAno);
            textGenero = itemView.findViewById(R.id.TextGenero);
            imageView = itemView.findViewById(R.id.imageView_recicler_veiw);
        }
    }
}
