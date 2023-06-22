package com.example.appchat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class FormCadastro extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText edit_nome, edit_email, edit_senha, edit_confirma_senha;
    private Button btnCadastro;

    private TextView text_Entrar;

    String UsuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        InciarComponentes();

        mAuth = FirebaseAuth.getInstance();
        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nome = edit_nome.getText().toString();
                String email = edit_email.getText().toString();
                String senha = edit_senha.getText().toString();
                String confirmaSenha = edit_confirma_senha.getText().toString();

                if(nome.isEmpty() || email.isEmpty()  || senha.isEmpty()){

                    Toast.makeText(FormCadastro.this, "Preencha todos os campos",Toast.LENGTH_SHORT).show();

                }
                if (!senha.equals(confirmaSenha)){
                    Toast.makeText(FormCadastro.this, "As senhas não são iguais",Toast.LENGTH_SHORT).show();
                }

                if (senha.equals(confirmaSenha)){
                    CadastrarUsuario();

                    //Incaminha o usuario para a tela de login apos o cadastro
                    Intent intent = new Intent(FormCadastro.this, FormLogin.class);
                    startActivity(intent);
                }

            }
        });

    }
    public void abrirLogin(View view) {
        Intent intent = new Intent(FormCadastro.this, FormLogin.class);
        startActivity(intent);
    }

    private  void CadastrarUsuario(){
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    //Salva o nome do usuario no bancoStorege
                    SalvarDadosUsuarios();

                    Toast.makeText(FormCadastro.this, "Cadastro realizado com sucesso!",Toast.LENGTH_SHORT).show();

                }else {

                    String erro;

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erro = "Digite uma senha com no mínimo 6 caracteres";
                    } catch (FirebaseAuthUserCollisionException e) {
                        erro = "Esta conta já foi cadastrada";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "E-mail inválido";
                    } catch (Exception e) {
                        erro = "Erro ao cadastrar usuário";

                    }
                    Toast.makeText(FormCadastro.this, erro, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SalvarDadosUsuarios(){
        String nome = edit_nome.getText().toString();


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> usuarios = new HashMap<>();
        usuarios.put("nome",nome);

        // Vai salvar o ID do usuario no banco
        UsuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //cada usuario cadastrado tera um documento especifico

        DocumentReference documenteReference = db.collection("usuarios").document(UsuarioID);
        documenteReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db", "Sucesso ao salvar os dados");

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("db_error", "Erro ao salvar os dados" + e.toString());
                    }
                });
    }

    private void InciarComponentes(){
        edit_nome = findViewById(R.id.edit_nome);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        edit_confirma_senha = findViewById(R.id.edit_confirma_senha);
        btnCadastro = findViewById(R.id.btnCadastro);
    }
}