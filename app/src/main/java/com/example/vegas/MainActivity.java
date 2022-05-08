package com.example.vegas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    EditText emailEditText;
    EditText passwordEditText;
    FirebaseAuth mAuth;
    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.loginFelhasznalo);
        passwordEditText = findViewById(R.id.loginJelszo);
        error = findViewById(R.id.logError);

        mAuth = FirebaseAuth.getInstance();
    }

    public void login(View view) {
        String emailStr = emailEditText.getText().toString();
        String passStr = passwordEditText.getText().toString();

        Intent intent = new Intent(this, scheduler.class);

        mAuth.signInWithEmailAndPassword(emailStr, passStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "Logged in Successfully");
                    error.setText("");
                    startActivity(intent);
                }else{
                    Log.e(LOG_TAG, "Invalid credentials: " + task.getException().getMessage());
                    error.setText("Helytelen jelszó vagy e-mail cím");
                }
            }
        });
    }

    public void register(View view) {
        Intent reg = new Intent(this, RegisterActivity.class);
        startActivity(reg);
    }
}