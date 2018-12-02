package gabrichisco.proyectocalendario;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CreateCalendar extends Activity {
    CalendarView simpleCalendarView;
    Button nextStepBtn;
    TextView instructionsTV;
    int count = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_calendar);

        nextStepBtn = findViewById(R.id.idNextCalendar);
        simpleCalendarView = findViewById(R.id.simpleCalendarView);
        instructionsTV = findViewById(R.id.instructions);

        nextStepBtn.setText("Aceptar");
        instructionsTV.setText("Selecciona el rango de fechas");

        List<EventDay> events = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        events.add(new EventDay(calendar, R.color.colorAccent));
        simpleCalendarView.setEvents(events);


        simpleCalendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();

            }
        });

        nextStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Calendar calendar : simpleCalendarView.getSelectedDates()) {
                    System.out.println(calendar.getTime().toString());

                }
                Toast.makeText(getApplicationContext(),
                        simpleCalendarView.getSelectedDates().size() + " días selecionados",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
