package gabrichisco.proyectocalendario;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;


public class CreateCalendar extends Activity {
    CalendarView simpleCalendarView;
    Button nextStepBtn;
    TextView instructionsTV;
    SwitchButton horas;

    int count = 0;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_calendar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        horas = findViewById(R.id.Horas_Dias);
        nextStepBtn = findViewById(R.id.idNextCalendar);
        simpleCalendarView = findViewById(R.id.simpleCalendarView);
        instructionsTV = findViewById(R.id.instructions);

        nextStepBtn.setText("Aceptar");
        instructionsTV.setText("Selecciona el rango de fechas");

        List<EventDay> events = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        events.add(new EventDay(calendar, R.color.colorAccent));
        simpleCalendarView.setEvents(events);


        simpleCalendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();

        });

        nextStepBtn.setOnClickListener(v -> {
            if (simpleCalendarView.getSelectedDates().size() != 0) {
                List<Calendar> listDates = simpleCalendarView.getSelectedDates();
                for (Calendar calendar1 : simpleCalendarView.getSelectedDates()) {
                    System.out.println(calendar1.getTime().toString());

                }
                Toast.makeText(getApplicationContext(),
                        simpleCalendarView.getSelectedDates().size() + " días selecionados",
                        Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(CreateCalendar.this);
                builder.setTitle("Dale un nombre");

                final EditText input = new EditText(CreateCalendar.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", (dialog, which) -> {
                    String calendarTitle = input.getText().toString();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference calendarDataDB = database.getReference("Calendars");
                    DatabaseReference userDataDB = database.getReference("Users");
                    String uuid = UUID.randomUUID().toString();

                    calendarDataDB.child(uuid).child("CalendarName").setValue(calendarTitle);
                    calendarDataDB.child(uuid).child("UserOwner").setValue(currentUser.getUid());
                    calendarDataDB.child(uuid).child("Date").setValue(System.currentTimeMillis());

                    calendarDataDB.child(uuid).child("MinDate").child("Year").setValue(listDates.get(0).get(Calendar.YEAR));
                    calendarDataDB.child(uuid).child("MinDate").child("Month").setValue(listDates.get(0).get(Calendar.MONTH));
                    calendarDataDB.child(uuid).child("MinDate").child("Day").setValue(listDates.get(0).get(Calendar.DAY_OF_MONTH));

                    calendarDataDB.child(uuid).child("MaxDate").child("Year").setValue(listDates.get(listDates.size() - 1).get(Calendar.YEAR));
                    calendarDataDB.child(uuid).child("MaxDate").child("Month").setValue(listDates.get(listDates.size() - 1).get(Calendar.MONTH));
                    calendarDataDB.child(uuid).child("MaxDate").child("Day").setValue(listDates.get(listDates.size() - 1).get(Calendar.DAY_OF_MONTH));

                    if (horas.isChecked()) {
                        calendarDataDB.child(uuid).child("CalendarType").setValue("Hours");
                        Intent myIntent = new Intent(CreateCalendar.this, ViewWeek.class);
                        CreateCalendar.this.startActivity(myIntent);
                        finish();
                    } else {
                        calendarDataDB.child(uuid).child("CalendarType").setValue("Days");
                        Intent myIntent = new Intent(CreateCalendar.this, ViewCalendar.class);
                        CreateCalendar.this.startActivity(myIntent);
                        finish();

                    }


                    calendarDataDB.child(uuid).child("Users").child(currentUser.getUid()).child("UserName").setValue(currentUser.getEmail());

                    userDataDB.child(currentUser.getUid()).child("Calendars").child(uuid).setValue(calendarTitle);


                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                builder.show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Tienes que seleccionar una fecha",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
