package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FormLogin extends AppCompatActivity {

    private TextView text_tela_Cadastro;
    private EditText edit_email_login, edit_senha_login;
    private Button btnLogin;
    private ProgressBar progressLogin;

    String[] mensagens = {"Preencha todos os campos!", "Login efetuado com sucesso!!"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);

        IniciarComponentes();

        text_tela_Cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormLogin.this, FormCadastro.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edit_email_login.getText().toString();
                String senha = edit_senha_login.getText().toString();

                if (email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(FormLogin.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                } else {
                    AutenticarUsuario(email, senha);
                }
            }
        });

    }

    protected void onStart(){
        super.onStart();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioAtual != null){
            //caso o usuário esteja logado, vá para a tela principal
            TelaPrincipal();
        }
    }

    private void AutenticarUsuario(String email, String senha){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    progressLogin.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TelaPrincipal();
                        }
                    }, 3000);
                } else {
                    String erro = "Erro ao logar usuário";

                    if (task.getException() != null) {
                        erro = task.getException().getMessage();
                    }

                    Toast.makeText(FormLogin.this, erro, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void TelaPrincipal(){
        Intent intent = new Intent(FormLogin.this, TelaPrincipal.class);
        startActivity(intent);
        finish();
    }

    private void IniciarComponentes(){
        text_tela_Cadastro = findViewById(R.id.text_tela_Cadastro);
        edit_email_login = findViewById(R.id.edit_email_login);
        edit_senha_login = findViewById(R.id.edit_senha_login);
        btnLogin = findViewById(R.id.btnLogin);
        progressLogin = findViewById(R.id.progressLogin);
    }
}
