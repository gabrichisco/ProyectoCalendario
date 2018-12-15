package gabrichisco.proyectocalendario;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.notbytes.barcode_reader.BarcodeReaderActivity;

import static com.notbytes.barcode_reader.BarcodeReaderActivity.getLaunchIntent;

public class MainActivity extends Activity {
    Button createBtn, mineBtn, SearchBtn;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createBtn = findViewById(R.id.idCreateSchelude);
        mineBtn = findViewById(R.id.idMineSchelude);
        SearchBtn = findViewById(R.id.idSearchSchelude);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        createBtn.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, CreateCalendar.class);
            MainActivity.this.startActivity(myIntent);
        });

        mineBtn.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, MineCalendar.class);
            MainActivity.this.startActivity(myIntent);
        });

        SearchBtn.setOnClickListener(v -> {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("PERMISO NO CONCEDIDO");
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                            REQUEST_WRITE_PERMISSION);
                } else {
                    readQR();
                }
            } else {
                readQR();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    readQR();

                } else {
                    Toast.makeText(MainActivity.this, "Permiso denegado!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "error in  scanning", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == 20 && data != null) {
            Barcode barcode = data.getParcelableExtra(BarcodeReaderActivity.KEY_CAPTURED_BARCODE);
//            Toast.makeText(this, barcode.rawValue, Toast.LENGTH_SHORT).show();

            String UUID = barcode.rawValue.split("\\|")[0];
            String NAME = barcode.rawValue.split("\\|")[1];

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(MainActivity.this);
            }

            builder.setTitle("Añadir calendario")
                    .setMessage("¿Quieres añadir el calendario \'" + NAME + "\' a tu lista de calendarios?")
                    .setPositiveButton("Sí", (dialog, which) -> {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference calendarDataDB = database.getReference("Calendars");
                        DatabaseReference userDataDB = database.getReference("Users");

                        userDataDB.child(currentUser.getUid()).child("Calendars").child(UUID).setValue(NAME);
                        calendarDataDB.child(UUID).child("Users").child(currentUser.getUid()).child("UserName").setValue(currentUser.getEmail());

                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void readQR() {
        Intent launchIntent = getLaunchIntent(this, true, false);
        startActivityForResult(launchIntent, 20);
    }
}
