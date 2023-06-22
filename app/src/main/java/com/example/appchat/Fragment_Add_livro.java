package com.example.appchat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class Fragment_Add_livro extends Fragment {
    // Dados do livro
    private EditText edTituloDoLivro, edAutorDoLivro, edAnoDoLivro, edDescricaoDoLivro;
    private Spinner spGeneroDoLivro, spEstadoDoLivro;
    private MaterialCardView Escolherfoto;
    private Uri ImageUri;
    private Bitmap bitmap;
    private ImageView FotoLivroImageView;
    private String FotoUrl;
    private FirebaseStorage Storage;
    private FirebaseDatabase BancoTempoReal;
    private StorageReference mStorageRef;
    private FirebaseAuth firebaseAuth;
    private String UsuarioAtualID;
    private ProgressDialog progressDialog;

    public Fragment_Add_livro() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_livro, container, false);

        edTituloDoLivro = rootView.findViewById(R.id.EditText_Titulo_Livro);
        edAutorDoLivro = rootView.findViewById(R.id.EditText_Autor_Livro);
        spGeneroDoLivro = rootView.findViewById(R.id.EditSP_Genero_Livro);
        edAnoDoLivro = rootView.findViewById(R.id.EditText_Ano_Livro);
        spEstadoDoLivro = rootView.findViewById(R.id.Spinner_Estado_Livro);
        edDescricaoDoLivro = rootView.findViewById(R.id.editTextTextMultiLine_Descricao);

        BancoTempoReal = FirebaseDatabase.getInstance();
        Storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        UsuarioAtualID = firebaseAuth.getCurrentUser().getUid();

        Spinner spinner = rootView.findViewById(R.id.EditSP_Genero_Livro);
        ArrayAdapter<CharSequence> generoAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.generos_livros, android.R.layout.simple_spinner_item);
        generoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGeneroDoLivro.setAdapter(generoAdapter);

        Escolherfoto = rootView.findViewById(R.id.escolher_foto);
        FotoLivroImageView = rootView.findViewById(R.id.Inserir_foto_imageView);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(getActivity());

        Escolherfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
        Button enviarButton = rootView.findViewById(R.id.btn_publicar);
        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnviarDadosDoLivro();
            }
        });


        return rootView;
    }
    private void LimparCampos() {
        edTituloDoLivro.setText("");
        edAutorDoLivro.setText("");
        edAnoDoLivro.setText("");
        edDescricaoDoLivro.setText("");
        spGeneroDoLivro.setSelection(0);
        spEstadoDoLivro.setSelection(0);
        FotoLivroImageView.setImageResource(R.drawable.ic_addfoto); // Substitua "placeholder_image" pelo ID do seu recurso de imagem padrão
        ImageUri = null;
        bitmap = null;
        FotoUrl = null;
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                pickImage();
            }
        } else {
            pickImage();
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                Toast.makeText(getActivity(), "Permissão negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            ImageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), ImageUri);
                FotoLivroImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void EnviarDadosDoLivro() {
        String TituloDoLivro = edTituloDoLivro.getText().toString();
        String AutorDoLivro = edAutorDoLivro.getText().toString();
        String GeneroDoLivro = spGeneroDoLivro.getSelectedItem().toString();
        String AnoDoLivro = edAnoDoLivro.getText().toString();
        String EstadoDoLivro = spEstadoDoLivro.getSelectedItem().toString();
        String DescricaoDoLivro = edDescricaoDoLivro.getText().toString();

        if (TextUtils.isEmpty(TituloDoLivro)) {
            Toast.makeText(getActivity(), "Por favor, insira o título do livro", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(AutorDoLivro)) {
            Toast.makeText(getActivity(), "Por favor, insira o nome do autor do livro", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(AnoDoLivro)) {
            Toast.makeText(getActivity(), "Por favor, insira o ano do livro", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(DescricaoDoLivro)) {
            Toast.makeText(getActivity(), "Por favor, insira uma descrição do livro", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Enviando livro");
            progressDialog.setMessage("Aguarde enquanto enviamos o livro...");
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.show();

            DatabaseReference livroRef = BancoTempoReal.getReference().child("Livros").push();
            String livroId = livroRef.getKey();

            // Salvar a imagem no Firebase Storage
            if (ImageUri != null) {
                StorageReference imagemRef = mStorageRef.child("imagens_livros").child(livroId + ".jpg");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();

                UploadTask uploadTask = imagemRef.putBytes(imageData);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Obter a URL da imagem salva
                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        urlTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    FotoUrl = downloadUri.toString();

                                    // Criar um mapa com os dados do livro
                                    Map<String, Object> livroMap = new HashMap<>();
                                    livroMap.put("titulo", TituloDoLivro);
                                    livroMap.put("autor", AutorDoLivro);
                                    livroMap.put("genero", GeneroDoLivro);
                                    livroMap.put("ano", AnoDoLivro);
                                    livroMap.put("estado", EstadoDoLivro);
                                    livroMap.put("descricao", DescricaoDoLivro);
                                    livroMap.put("fotoUrl", FotoUrl);

                                    livroRef.setValue(livroMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getActivity(), "Livro enviado com sucesso", Toast.LENGTH_SHORT).show();
                                                LimparCampos();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(getActivity(), "Falha ao enviar o livro", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Falha ao obter a URL da imagem", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Falha ao enviar a imagem", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Criar um mapa com os dados do livro
                Map<String, Object> livroMap = new HashMap<>();
                livroMap.put("titulo", TituloDoLivro);
                livroMap.put("autor", AutorDoLivro);
                livroMap.put("genero", GeneroDoLivro);
                livroMap.put("ano", AnoDoLivro);
                livroMap.put("estado", EstadoDoLivro);
                livroMap.put("descricao", DescricaoDoLivro);

                livroRef.setValue(livroMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Livro enviado com sucesso", Toast.LENGTH_SHORT).show();
                            LimparCampos();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Falha ao enviar o livro", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }
    }




    /*
    private void EnviarDadosDoLivro() {
        String TituloDoLivro = edTituloDoLivro.getText().toString();
        String AutorDoLivro = edAutorDoLivro.getText().toString();
        String GeneroDoLivro = spGeneroDoLivro.getSelectedItem().toString();
        String AnoDoLivro = edAnoDoLivro.getText().toString();
        String EstadoDoLivro = spEstadoDoLivro.getSelectedItem().toString();
        String DescricaoDoLivro = edDescricaoDoLivro.getText().toString();

        if (TextUtils.isEmpty(TituloDoLivro)) {
            Toast.makeText(getActivity(), "Por favor, insira o título do livro", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(AutorDoLivro)) {
            Toast.makeText(getActivity(), "Por favor, insira o nome do autor do livro", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(AnoDoLivro)) {
            Toast.makeText(getActivity(), "Por favor, insira o ano do livro", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(DescricaoDoLivro)) {
            Toast.makeText(getActivity(), "Por favor, insira uma descrição do livro", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Enviando livro");
            progressDialog.setMessage("Aguarde enquanto enviamos o livro...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            ArmazenarImagemNoFirebase();
        }
    }

 */
    private void ArmazenarImagemNoFirebase() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imageData = byteArrayOutputStream.toByteArray();

        StorageReference filepath = mStorageRef.child("Imagens").child(edTituloDoLivro.getText().toString() + ".jpg");
        UploadTask uploadTask = filepath.putBytes(imageData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                FotoUrl = downloadUrl.toString();

                SalvarDadosDoLivroNoFirebase();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Erro ao enviar imagem", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void SalvarDadosDoLivroNoFirebase() {
        DatabaseReference LivrosRef = BancoTempoReal.getReference().child("Livros");
        String LivroID = LivrosRef.push().getKey();

        Map<String, Object> livroMap = new HashMap<>();
        livroMap.put("livroID", LivroID);
        livroMap.put("titulo", edTituloDoLivro.getText().toString());
        livroMap.put("autor", edAutorDoLivro.getText().toString());
        livroMap.put("genero", spGeneroDoLivro.getSelectedItem().toString());
        livroMap.put("ano", edAnoDoLivro.getText().toString());
        livroMap.put("estado", spEstadoDoLivro.getSelectedItem().toString());
        livroMap.put("descricao", edDescricaoDoLivro.getText().toString());
        livroMap.put("foto", FotoUrl);
        livroMap.put("userID", UsuarioAtualID);

        LivrosRef.child(LivroID).updateChildren(livroMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Livro enviado com sucesso", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    String mensagem = task.getException().toString();
                    Toast.makeText(getActivity(), "Erro ao enviar livro: " + mensagem, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

}