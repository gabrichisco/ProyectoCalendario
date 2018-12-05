package gabrichisco.proyectocalendario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends Activity {
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    EditText email, password;
    Button loginBtn, forgotBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        email = findViewById(R.id.idLoginEmail);
        password = findViewById(R.id.idLoginPassword);
        loginBtn = findViewById(R.id.idLoginButton);
        forgotBtn = findViewById(R.id.idLoginForgot);


        if (currentUser != null) {
            goToMain();
        }

        loginBtn.setOnClickListener(v -> {
            // Comprobar el email y la contraseña
            if (isValidEmail(email.getText().toString())) {
                login();
            } else {
                // Igual es mejor comprobar los dos campos por separado y mostrar un mensaje por cada uno
//                    Toast.makeText(LoginActivity.this, "Email no válido", Toast.LENGTH_LONG);
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.mensaje_cuando_la_cagas), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void login() {
        this.mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Login", "Task: " + task.getException());
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();


                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference userDataDB = database.getReference("Users");

                            userDataDB.child(user.getUid()).child("UserEmail").setValue(email.getText().toString());
                            userDataDB.child(user.getUid()).child("Date").setValue(System.currentTimeMillis());

                            goToMain();
                        } else {
                            if (task.getException().getMessage().contains("The email address is already in use")) {
                                LoginActivity.this.mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            goToMain();
                                        } else
                                            Toast.makeText(LoginActivity.this,getResources().getString(R.string.error_incorrect_password), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                );
                            }

                                }
                         if(task.getException().getMessage().contains("The given password is invalid")) {
                            Toast.makeText(LoginActivity.this,getResources().getString(R.string.error_invalid_password), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                 );
                }

    private void goToMain() {
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(myIntent);
        finish();
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static boolean isValidPassword(String password) {
        // Completar aqui las reglas de la contraseña
        return true;
    }
}

