package gabrichisco.proyectocalendario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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

public class LoginActivity extends Activity {
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    EditText email, password;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        email = findViewById(R.id.idLoginEmail);
        password = findViewById(R.id.idLoginPassword);
        loginBtn = findViewById(R.id.idLoginButton);

        if (currentUser != null) {
            goToMain();
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    login();
                } else {
//                    Toast.makeText(LoginActivity.this, "Email no válido", Toast.LENGTH_LONG);
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.mensaje_cuando_la_cagas), Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void login() {
        this.mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();

                            /*
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference userDataDB = database.getReference("Users");

                            Long currentTime = System.currentTimeMillis();

                            userDataDB.child(user.getUid()).child("UserEmail").setValue(email);
                            userDataDB.child(user.getUid()).child("Date").setValue(currentTime);
                             */

                            goToMain();
                        } else {

                        }
                    }
                });
    }

    private void goToMain() {
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(myIntent);
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

