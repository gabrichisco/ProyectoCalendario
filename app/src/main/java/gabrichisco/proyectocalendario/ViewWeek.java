package gabrichisco.proyectocalendario;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewLoader;
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


public class ViewWeek extends Activity implements WeekView.EmptyViewClickListener, WeekView.EventClickListener { //implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {
    private FirebaseAuth mAuth;
    private WeekView mWeekView;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference userDataDB, calendarDataDB;
    int Numero_Inicial;
    Button finish;
    String calendarKey;
    TextView calendarTitle;
    List<WeekViewEvent> events = new ArrayList<>();
    WeekViewLoader mWeekLoader;
    Calendar lastDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_week);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userDataDB = database.getReference("Users");
        calendarDataDB = database.getReference("Calendars");
        mWeekView = findViewById(R.id.weekView);
        finish = findViewById(R.id.Finish);

        mWeekLoader = new WeekViewLoader() {
            @Override
            public double toWeekViewPeriodIndex(Calendar instance) {
                return 1;
            }

            @Override
            public List<? extends WeekViewEvent> onLoad(int periodIndex) {
                return null;
            }
        };
        mWeekLoader.onLoad(1);
        mWeekView.setWeekViewLoader(mWeekLoader);

        if (getIntent().hasExtra("key")) {
            Bundle getData = getIntent().getExtras();
            calendarKey = getData.getString("key");
        }

        mWeekView.setMonthChangeListener((newYear, newMonth) -> events);

        mWeekView.setOnEventClickListener(this);

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
                Calendar maxDateForWeek = Calendar.getInstance();
                maxDateForWeek.set(Integer.parseInt(dataSnapshot.child("MaxDate").child("Year").getValue().toString()), Integer.parseInt(dataSnapshot.child("MaxDate").child("Month").getValue().toString()), Integer.parseInt(dataSnapshot.child("MaxDate").child("Day").getValue().toString()) - 5);

                Numero_Inicial = ViewWeek.this.getTimeRemaining(minDate, maxDate);

                for (DataSnapshot propertySnapshot : dataSnapshot.child("Users").getChildren()) {
                    for (DataSnapshot snapshotDates : propertySnapshot.child("SelectedDates").getChildren()) {
                        if (snapshotDates.getChildrenCount() == 4) {
                            if (propertySnapshot.getKey().equals(currentUser.getUid())) {
                                Calendar startTime = Calendar.getInstance();
                                startTime.set(Calendar.MINUTE, 0);
                                startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(snapshotDates.child("Hour").getValue().toString()));
                                startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(snapshotDates.child("Day").getValue().toString()));
                                startTime.set(Calendar.MONTH, Integer.parseInt(snapshotDates.child("Month").getValue().toString()));
                                startTime.set(Calendar.YEAR, Integer.parseInt(snapshotDates.child("Year").getValue().toString()));

                                Calendar endTime = (Calendar) startTime.clone();
                                endTime.set(Calendar.MINUTE, 59);
                                startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(snapshotDates.child("Hour").getValue().toString()));
                                startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(snapshotDates.child("Day").getValue().toString()));
                                startTime.set(Calendar.MONTH, Integer.parseInt(snapshotDates.child("Month").getValue().toString()));
                                startTime.set(Calendar.YEAR, Integer.parseInt(snapshotDates.child("Year").getValue().toString()));

                                WeekViewEvent event = new WeekViewEvent(1, getEventTitle(startTime), startTime, endTime);
                                event.setColor(getResources().getColor(R.color.colorAccent));

                                if (eventExists(events, event) != null) {
                                    try {
                                        event.setName(Integer.toString(Integer.parseInt(event.getName()) + 1));
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                        event.setName("1");
                                    }
                                    events.remove(eventExists(events, event));
                                } else {
                                    event.setName("1");
                                }
                                event.setId(0);
                                events.add(event);
                            } else {
                                Calendar startTime = Calendar.getInstance();
                                startTime.set(Calendar.MINUTE, 0);
                                startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(snapshotDates.child("Hour").getValue().toString()));
                                startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(snapshotDates.child("Day").getValue().toString()));
                                startTime.set(Calendar.MONTH, Integer.parseInt(snapshotDates.child("Month").getValue().toString()));
                                startTime.set(Calendar.YEAR, Integer.parseInt(snapshotDates.child("Year").getValue().toString()));

                                Calendar endTime = (Calendar) startTime.clone();
                                endTime.set(Calendar.MINUTE, 59);
                                startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(snapshotDates.child("Hour").getValue().toString()));
                                startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(snapshotDates.child("Day").getValue().toString()));
                                startTime.set(Calendar.MONTH, Integer.parseInt(snapshotDates.child("Month").getValue().toString()));
                                startTime.set(Calendar.YEAR, Integer.parseInt(snapshotDates.child("Year").getValue().toString()));

                                WeekViewEvent event = new WeekViewEvent(1, getEventTitle(startTime), startTime, endTime);
                                event.setColor(getResources().getColor(R.color.colorSecundary));

                                if (eventExists(events, event) != null) {
                                    if (eventExists(events, event).getId() == 0) {
                                        event.setColor(getResources().getColor(R.color.colorAccent));
                                        event.setId(0);
                                    }
                                    event.setName(Integer.toString(Integer.parseInt(eventExists(events, event).getName()) + 1));
                                    events.remove(eventExists(events, event));
                                } else {
                                    event.setName("1");
                                }
                                events.add(event);
                            }
                        }
                    }
                }

                runOnUiThread(() -> {
                    mWeekView.goToDate(minDate);

                    if (Numero_Inicial < 6) {
                        mWeekView.setNumberOfVisibleDays(Numero_Inicial + 1);
                        mWeekView.setXScrollingSpeed(0);
                    } else {
                        mWeekView.setNumberOfVisibleDays(6);
                        mWeekView.setXScrollingSpeed(1);

                        mWeekView.setScrollListener((newFirstVisibleDay, oldFirstVisibleDay) -> {
                            System.out.println("NEW: " + newFirstVisibleDay);
                            System.out.println("OLD: " + oldFirstVisibleDay);
                            lastDate = newFirstVisibleDay;
                            if (calendarIsLessThan(newFirstVisibleDay, minDate)) {
                                System.out.println("SAME");
                                mWeekView.goToDate(minDate);
                                mWeekView.setXScrollingSpeed(0);

                                final Handler handler = new Handler();
                                handler.postDelayed(() -> {
                                    mWeekView.setXScrollingSpeed(1);
                                    mWeekView.goToDate(minDate);
                                }, 10);
                            }

                            if (calendarIsMoreThan(newFirstVisibleDay, maxDateForWeek)) {
                                System.out.println("SAME2");
                                mWeekView.goToDate(maxDateForWeek);
                                mWeekView.setXScrollingSpeed(0);

                                final Handler handler = new Handler();
                                handler.postDelayed(() -> {
                                    mWeekView.setXScrollingSpeed(1);
                                    mWeekView.goToDate(maxDateForWeek);
                                }, 10);
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        calendarDataDB.child(calendarKey).addListenerForSingleValueEvent(postListener2);

        setupDateTimeInterpreter(true);

        mWeekView.setEmptyViewClickListener(this);

        finish.setOnClickListener(v -> {
            calendarDataDB.child(calendarKey).child("Users").child(currentUser.getUid()).child("SelectedDates").removeValue();

            int j = 0;
            for (WeekViewEvent event : events) {
                System.out.println(event);

                if (event.getId() != 1) {
                    calendarDataDB.child(calendarKey).child("Users").child(currentUser.getUid()).child("SelectedDates").child(Integer.toString(j)).child("Year").setValue(event.getStartTime().get(Calendar.YEAR));
                    calendarDataDB.child(calendarKey).child("Users").child(currentUser.getUid()).child("SelectedDates").child(Integer.toString(j)).child("Month").setValue(event.getStartTime().get(Calendar.MONTH));
                    calendarDataDB.child(calendarKey).child("Users").child(currentUser.getUid()).child("SelectedDates").child(Integer.toString(j)).child("Day").setValue(event.getStartTime().get(Calendar.DAY_OF_MONTH));
                    calendarDataDB.child(calendarKey).child("Users").child(currentUser.getUid()).child("SelectedDates").child(Integer.toString(j)).child("Hour").setValue(event.getStartTime().get(Calendar.HOUR_OF_DAY));
                }
                j++;
            }

            Toast.makeText(ViewWeek.this, "Todo correcto", Toast.LENGTH_LONG).show();

        });
    }

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
//        Toast.makeText(ViewWeek.this, "Date clicked: " + time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE) + " " + time.get(Calendar.DAY_OF_MONTH) + "/" + (time.get(Calendar.MONTH) + 1) + "/" + time.get(Calendar.YEAR), Toast.LENGTH_LONG).show();

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
        startTime.set(Calendar.DAY_OF_MONTH, time.get(Calendar.DAY_OF_MONTH));
        startTime.set(Calendar.MONTH, time.get(Calendar.MONTH));
        startTime.set(Calendar.YEAR, time.get(Calendar.YEAR));

        Calendar endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.MINUTE, 59);
        endTime.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
        startTime.set(Calendar.DAY_OF_MONTH, time.get(Calendar.DAY_OF_MONTH));
        endTime.set(Calendar.MONTH, time.get(Calendar.MONTH));
        endTime.set(Calendar.YEAR, time.get(Calendar.YEAR));

        WeekViewEvent event = new WeekViewEvent(1, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.colorAccent));
        event.setName("");
        event.setId(0);

        mWeekView.goToDate(lastDate);

        events.add(event);
    }

    public int getTimeRemaining(Calendar sDate, Calendar eDate) {
        // Get the represented date in milliseconds
        long milis1 = sDate.getTimeInMillis();
        long milis2 = eDate.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = Math.abs(milis2 - milis1);

        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    public WeekViewEvent eventExists(List<WeekViewEvent> events, WeekViewEvent event) {
        for (WeekViewEvent eventA : events) {
            if (eventA.getStartTime().get(Calendar.YEAR) == event.getStartTime().get(Calendar.YEAR)) {
                if (eventA.getStartTime().get(Calendar.MONTH) == event.getStartTime().get(Calendar.MONTH)) {
                    if (eventA.getStartTime().get(Calendar.DAY_OF_MONTH) == event.getStartTime().get(Calendar.DAY_OF_MONTH)) {
                        if (eventA.getStartTime().get(Calendar.HOUR_OF_DAY) == event.getStartTime().get(Calendar.HOUR_OF_DAY)) {
                            return eventA;
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean calendarIsLessThan(Calendar calendarA, Calendar calendarB) {
        if (calendarA.getTimeInMillis() <= calendarB.getTimeInMillis()) {
            return true;
        }

        return false;
    }

    private boolean calendarIsMoreThan(Calendar calendarA, Calendar calendarB) {
        if (calendarA.getTimeInMillis() >= calendarB.getTimeInMillis()) {
            return true;
        }

        return false;
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        System.out.println(event);

            if (event.getId() != 0) {
                event.setName(Integer.toString(Integer.parseInt(eventExists(events, event).getName()) + 1));
                event.setId(0);
                events.remove(eventExists(events, event));
                events.add(event);
            }

    }
}
