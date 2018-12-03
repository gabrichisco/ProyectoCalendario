package gabrichisco.proyectocalendario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViewCalendar extends Activity {
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference userDataDB, calendarDataDB;
    ProgressBar spinner;
    String calendarKey;
    CalendarView simpleCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_calendar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userDataDB = database.getReference("Users");
        calendarDataDB = database.getReference("Calendars");
        simpleCalendarView = findViewById(R.id.simpleCalendarView);

        if(getIntent().hasExtra("key")){
            Bundle getData = getIntent().getExtras();
            calendarKey = getData.getString("key");
        }

        ValueEventListener postListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("MineCalendar", dataSnapshot.toString());
                ((TextView) findViewById(R.id.calendarTitle)).setText(dataSnapshot.child("CalendarName").getValue().toString());

                Calendar minDate = Calendar.getInstance();
                minDate.set(Integer.parseInt(dataSnapshot.child("MinDate").child("Year").getValue().toString()), Integer.parseInt(dataSnapshot.child("MinDate").child("Month").getValue().toString()), Integer.parseInt(dataSnapshot.child("MinDate").child("Day").getValue().toString()));
                Calendar maxDate = Calendar.getInstance();
                maxDate.set(Integer.parseInt(dataSnapshot.child("MaxDate").child("Year").getValue().toString()), Integer.parseInt(dataSnapshot.child("MaxDate").child("Month").getValue().toString()), Integer.parseInt(dataSnapshot.child("MaxDate").child("Day").getValue().toString()));

//                simpleCalendarView.setMaximumDate(maxDate);
//                simpleCalendarView.setMinimumDate(minDate);

                List<Calendar> disbledDates = new ArrayList<>();
                List<EventDay> events = new ArrayList<>();

                for(int i = minDate.get(Calendar.DAY_OF_MONTH)-1; i >= minDate.getActualMinimum(Calendar.DAY_OF_MONTH); i--){
                    Calendar e = Calendar.getInstance();
                    e.set(minDate.get(Calendar.YEAR), minDate.get(Calendar.MONTH), i);
                    disbledDates.add(e);
                    events.add(new EventDay(e, R.color.colorSecundary));
                }

                for(int i = maxDate.get(Calendar.DAY_OF_MONTH) + 1; i >= maxDate.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
                    Calendar e = Calendar.getInstance();
                    e.set(maxDate.get(Calendar.YEAR), maxDate.get(Calendar.MONTH), i);
                    disbledDates.add(e);
                }

                runOnUiThread(() -> {
                    simpleCalendarView.setDisabledDays(disbledDates);

                    simpleCalendarView.setEvents(events);
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        calendarDataDB.child(calendarKey).addValueEventListener(postListener2);

    }
}
