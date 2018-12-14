package gabrichisco.proyectocalendario;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ViewWeek extends Activity implements WeekView.EmptyViewClickListener { //implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {
    private FirebaseAuth mAuth;
    private WeekView mWeekView;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference userDataDB, CalendarDataDB;
    int Numero_Inicial;
    int Numero_Actual;
    int Restantes;
    Button addhour;
    String calendarKey;
    CalendarView simpleCalendarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_week);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userDataDB = database.getReference("Users");
        CalendarDataDB = database.getReference("Calendars");
        simpleCalendarView = findViewById(R.id.simpleCalendarView);
        Numero_Inicial = 0;
        Numero_Actual = Numero_Inicial;
        Restantes = 0;


        setContentView(R.layout.activity_view_week);
        // Get a reference for the week view in the layout.
        mWeekView = findViewById(R.id.weekView);
        // Show a toast message about the touched event.
//        mWeekView.setOnEventClickListener(this);
        if (Numero_Inicial > 0) {
            if (Numero_Actual > 7) {
                Restantes = 7;
                Numero_Actual = (Numero_Inicial - 7);
            } else if (Numero_Actual > 0 && Numero_Actual < 8) {
                Restantes = Numero_Actual;
                Numero_Actual = Numero_Actual - Restantes;
            } else {
                addhour = findViewById(R.id.Finish);
            }

        }
        addhour.setOnClickListener();
        mWeekView.setColumnGap(Restantes);
        mWeekView.setXScrollingSpeed(0);
        mWeekView.setShowDistinctWeekendColor(true);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener((newYear, newMonth) -> {
            List<WeekViewEvent> events = new ArrayList<>();

            ValueEventListener postListener2 = new ValueEventListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("MineCalendar", dataSnapshot.toString());
                    ((TextView) findViewById(R.id.calendarTitle)).setText(dataSnapshot.child("CalendarName").getValue().toString());

                    Calendar minDate = Calendar.getInstance();
                    minDate.set(Integer.parseInt(dataSnapshot.child("MinDate").child("Year").getValue().toString()), Integer.parseInt(dataSnapshot.child("MinDate").child("Month").getValue().toString()), Integer.parseInt(dataSnapshot.child("MinDate").child("Day").getValue().toString()));
                    Calendar maxDate = Calendar.getInstance();
                    maxDate.set(Integer.parseInt(dataSnapshot.child("MaxDate").child("Year").getValue().toString()), Integer.parseInt(dataSnapshot.child("MaxDate").child("Month").getValue().toString()), Integer.parseInt(dataSnapshot.child("MaxDate").child("Day").getValue().toString()));

                    simpleCalendarView.setMaximumDate(maxDate);
                    simpleCalendarView.setMinimumDate(minDate);

                    List<Calendar> disbledDates = new ArrayList<>();

                    List<EventDay> events = new ArrayList<>();
                    for (DataSnapshot propertySnapshot : dataSnapshot.child("Users").getChildren()) {
                        for (DataSnapshot snapshotDates : propertySnapshot.child("SelectedDates").getChildren()) {
                            Calendar calendar = Calendar.getInstance();

                            calendar.set(Integer.parseInt(snapshotDates.child("Year").getValue().toString()), Integer.parseInt(snapshotDates.child("Month").getValue().toString()), Integer.parseInt(snapshotDates.child("Day").getValue().toString()));
                         /*   EventDay evDay = eventExists(events, calendar);

                            if (evDay != null) {
                                if (evDay.getImageDrawable().equals(R.drawable.sample_circle)) {
                                    events.remove(evDay);
                                    events.add(new EventDay(calendar, R.drawable.sample_two_icons));
                                }
//                        } else if (events.contains(new EventDay(calendar, R.drawable.sample_two_icons))) {
//                            events.add(new EventDay(calendar, R.drawable.sample_three_icons));
//                        } else if (events.contains(new EventDay(calendar, R.drawable.sample_three_icons))) {
//                            events.add(new EventDay(calendar, R.drawable.sample_four_icons));
//                        } else if (events.contains(new EventDay(calendar, R.drawable.sample_four_icons))) {

                            } else {
                                events.add(new EventDay(calendar, R.drawable.sample_circle));
                            }
                        }
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
            CalendarDataDB.child(calendarKey).addValueEventListener(postListener2);*/
/*
            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, 3);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);
            Calendar endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR, 1);
            endTime.set(Calendar.MONTH, newMonth-1);
            WeekViewEvent event = new WeekViewEvent(1, getEventTitle(startTime), startTime, endTime);
            event.setColor(getResources().getColor(R.color.colorAccent));
            events.add(event);

            startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, 3);
            startTime.set(Calendar.MINUTE, 30);
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);
            endTime = (Calendar) startTime.clone();
            endTime.set(Calendar.HOUR_OF_DAY, 4);
            endTime.set(Calendar.MINUTE, 30);
            endTime.set(Calendar.MONTH, newMonth-1);
            event = new WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
            event.setColor(getResources().getColor(R.color.colorAccent));
            events.add(event);

            startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, 4);
            startTime.set(Calendar.MINUTE, 20);
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);
            endTime = (Calendar) startTime.clone();
            endTime.set(Calendar.HOUR_OF_DAY, 5);
            endTime.set(Calendar.MINUTE, 0);
            event = new WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
            event.setColor(getResources().getColor(R.color.colorAccent));
            events.add(event);

            startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, 5);
            startTime.set(Calendar.MINUTE, 30);
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);
            endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR_OF_DAY, 2);
            endTime.set(Calendar.MONTH, newMonth-1);
            event = new WeekViewEvent(2, getEventTitle(startTime), startTime, endTime);
            event.setColor(getResources().getColor(R.color.colorAccent));
            events.add(event);

            startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, 5);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);
            startTime.add(Calendar.DATE, 1);
            endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR_OF_DAY, 3);
            endTime.set(Calendar.MONTH, newMonth - 1);
            event = new WeekViewEvent(3, getEventTitle(startTime), startTime, endTime);
            event.setColor(getResources().getColor(R.color.colorAccent));
            events.add(event);

            startTime = Calendar.getInstance();
            startTime.set(Calendar.DAY_OF_MONTH, 15);
            startTime.set(Calendar.HOUR_OF_DAY, 3);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);
            endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR_OF_DAY, 3);
            event = new WeekViewEvent(4, getEventTitle(startTime), startTime, endTime);
            event.setColor(getResources().getColor(R.color.colorAccent));
            events.add(event);

            startTime = Calendar.getInstance();
            startTime.set(Calendar.DAY_OF_MONTH, 1);
            startTime.set(Calendar.HOUR_OF_DAY, 3);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);
            endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR_OF_DAY, 3);
            event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
            event.setColor(getResources().getColor(R.color.colorAccent));
            events.add(event);

            startTime = Calendar.getInstance();
            startTime.set(Calendar.DAY_OF_MONTH, startTime.getActualMaximum(Calendar.DAY_OF_MONTH));
            startTime.set(Calendar.HOUR_OF_DAY, 15);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.MONTH, newMonth-1);
            startTime.set(Calendar.YEAR, newYear);
            endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR_OF_DAY, 3);
            event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
            event.setColor(getResources().getColor(R.color.colorAccent));
            events.add(event);*/

            return events;
        });

//        mWeekView.

        // Set long press listener for events.
//        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
//        mWeekView.setEmptyViewLongPressListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(true);

        mWeekView.setEmptyViewClickListener(this);
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     *
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" d/M", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
    }

    public WeekView getWeekView() {
        return mWeekView;
    }

    @Override
    public void onEmptyViewClicked(Calendar time) {
        Toast.makeText(ViewWeek.this, "Date clicked: " + time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE) + " " + time.get(Calendar.DAY_OF_MONTH) + "/" + (time.get(Calendar.MONTH) + 1) + "/" + time.get(Calendar.YEAR), Toast.LENGTH_LONG).show();
    }

//    @Override
//    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
//        return null;
//    }
}
