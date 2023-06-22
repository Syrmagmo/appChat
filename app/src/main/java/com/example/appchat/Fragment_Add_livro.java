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
    private EditText edTituloDoLivro, edAutorDoLivro, edAnoDoLivro, edDescricaoDoLivro;
    private Spinner spGeneroDoLivro, spEstadoDoLivro;
    private MaterialCardView Escolherfoto;
    private Uri ImageUri;
    private Bitmap bitmap;
    private ImageView FotoLivroImageView;
    private String FotoUrl;
    private FirebaseStorage Storage;
    private FirebaseDatabase BancoTempoReal;
    private DatabaseReference mRootRef;
    private StorageReference mStorageRef;
    private FirebaseAuth firebaseAuth;
    private String UsuarioAtualID;
    private ProgressDialog progressDialog;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQUEST_CODE = 1;

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


        mRootRef = FirebaseDatabase.getInstance().getReference();

        Spinner spinner = rootView.findViewById(R.id.EditSP_Genero_Livro);
        ArrayAdapter<CharSequence> generoAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.generos_livros, android.R.layout.simple_spinner_item);
        generoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGeneroDoLivro.setAdapter(generoAdapter);

        Escolherfoto = rootView.findViewById(R.id.escolher_foto);
        FotoLivroImageView = rootView.findViewById(R.id.Inserir_foto_imageView);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(getActivity());

        Escolherfoto = rootView.findViewById(R.id.escolher_foto);
        FotoLivroImageView = rootView.findViewById(R.id.Inserir_foto_imageView);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(getActivity());

        Button voltarBtn = rootView.findViewById(R.id.voltarBTN);
        voltarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Código para redirecionar para a activity desejada
                Intent intent = new Intent(getActivity(), TelaPrincipal.class);
                startActivity(intent);
            }
        });


        Escolherfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    } else {
                        chooseImage();
                    }
                } else {
                    chooseImage();
                }
            }
        });

        Button adicionarLivroBtn = rootView.findViewById(R.id.btn_publicar);
        adicionarLivroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarLivro();
            }
        });

        return rootView;
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImage();
            } else {
                Toast.makeText(getActivity(), "Permissão negada", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            ImageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), ImageUri);
                FotoLivroImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void adicionarLivro() {
        final String tituloLivro = edTituloDoLivro.getText().toString();
        final String autorLivro = edAutorDoLivro.getText().toString();
        final String generoLivro = spGeneroDoLivro.getSelectedItem().toString();
        final String anoLivro = edAnoDoLivro.getText().toString();
        final String estadoLivro = spEstadoDoLivro.getSelectedItem().toString();
        final String descricaoLivro = edDescricaoDoLivro.getText().toString();

        if (TextUtils.isEmpty(tituloLivro)) {
            Toast.makeText(getActivity(), "Digite o título do livro", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(autorLivro)) {
            Toast.makeText(getActivity(), "Digite o autor do livro", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(anoLivro)) {
            Toast.makeText(getActivity(), "Digite o ano do livro", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(descricaoLivro)) {
            Toast.makeText(getActivity(), "Digite a descrição do livro", Toast.LENGTH_SHORT).show();
        } else if (ImageUri == null) {
            Toast.makeText(getActivity(), "Escolha uma foto para o livro", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Adicionando Livro");
            progressDialog.setMessage("Por favor, aguarde enquanto adicionamos o livro");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            StorageReference filepath = mStorageRef.child("Imagens_Livros").child(ImageUri.getLastPathSegment() + ".jpg");
            filepath.putBytes(imageBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                    downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            FotoUrl = uri.toString();
                            DatabaseReference LivroRef = mRootRef.child("Livros").child(UsuarioAtualID).push();
                            String livroKey = LivroRef.getKey();

                            HashMap<String, String> livroMap = new HashMap<>();
                            livroMap.put("titulo", tituloLivro);
                            livroMap.put("IdUsuario", UsuarioAtualID);
                            livroMap.put("autor", autorLivro);
                            livroMap.put("genero", generoLivro);
                            livroMap.put("ano", anoLivro);
                            livroMap.put("estado", estadoLivro);
                            livroMap.put("descricao", descricaoLivro);
                            livroMap.put("foto", FotoUrl);

                            LivroRef.setValue(livroMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "Livro adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "Erro ao adicionar livro", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
    }
}