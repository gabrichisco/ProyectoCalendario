package gabrichisco.proyectocalendario;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Date;

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

        simpleCalendarView.setFirstDayOfWeek(2);

        nextStepBtn.setText("Elige mes");
        instructionsTV.setText("Selecciona la fecha inicial");

        simpleCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                if(count == 0){
                    Toast.makeText(getApplicationContext(), ""+dayOfMonth + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
                    simpleCalendarView.setMinDate(simpleCalendarView.getDate());
                    instructionsTV.setText("Selecciona la fecha final");
                    count++;
                } else if(count == 1) {
                    Toast.makeText(getApplicationContext(), ""+dayOfMonth + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
                    simpleCalendarView.setMaxDate(simpleCalendarView.getDate());
                    count++;
                }
            }
        });

        nextStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long dateLong = simpleCalendarView.getDate();
                Date date = new Date(dateLong);
                date.getMonth();
            }
        });
    }
}
