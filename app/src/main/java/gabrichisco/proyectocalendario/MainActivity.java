package gabrichisco.proyectocalendario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    Button createBtn, mineBtn, SearchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createBtn = findViewById(R.id.idCreateSchelude);
        mineBtn = findViewById(R.id.idMineSchelude);


        createBtn.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, CreateCalendar.class);
            MainActivity.this.startActivity(myIntent);
        });

        mineBtn.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, MineCalendar.class);
            MainActivity.this.startActivity(myIntent);
        });
    }
}
