package gabrichisco.proyectocalendario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MineCalendar extends Activity {
    ListView listView;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference userDataDB, calendarDataDB;
    List<String> calendarListNames = new ArrayList<>();
    List<String> calendarListKeys = new ArrayList<>();
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_calendar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userDataDB = database.getReference("Users");
        calendarDataDB = database.getReference("Calendars");
        spinner = findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        ValueEventListener postListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("MineCalendar", dataSnapshot.child("Calendars").toString());
                for (DataSnapshot propertySnapshot : dataSnapshot.child("Calendars").getChildren()) {
                    System.out.println(propertySnapshot.getKey() + ": " + propertySnapshot.getValue(String.class));
                    calendarListNames.add(propertySnapshot.getValue(String.class));
                    calendarListKeys.add(propertySnapshot.getKey());
                }

                listView = findViewById(R.id.listview);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MineCalendar.this, android.R.layout.simple_list_item_1, android.R.id.text1, calendarListNames);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    ValueEventListener postListener3 = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Intent myIntent;
                            if (dataSnapshot.child("CalendarType").getValue().toString().equals("Hours")) {
                                myIntent = new Intent(MineCalendar.this, ViewWeek.class);
                            } else {
                                myIntent = new Intent(MineCalendar.this, ViewCalendar.class);
                            }
                            myIntent.putExtra("key", calendarListKeys.get(position));
                            MineCalendar.this.startActivity(myIntent);
                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    };

                    calendarDataDB.child(calendarListKeys.get(position)).addValueEventListener(postListener3);
                });
                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        userDataDB.child(currentUser.getUid()).addValueEventListener(postListener2);

        FloatingActionButton fab = findViewById(R.id.addCalendar);
        fab.setOnClickListener(view -> {
            Intent myIntent = new Intent(MineCalendar.this, CreateCalendar.class);
            MineCalendar.this.startActivity(myIntent);
        });
    }
}
