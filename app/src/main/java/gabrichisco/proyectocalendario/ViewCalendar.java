package gabrichisco.proyectocalendario;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ViewCalendar extends Activity {
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference userDataDB, calendarDataDB;
    ProgressBar spinner;
    String calendarKey;
    CalendarView simpleCalendarView;
    Button okBtn;
    ImageView qrGenerator, share;

    String calendarKeyQR, calendarNameQR;
    String inputValue;
    Bitmap bitmap;
    List<Calendar> userEvents = new ArrayList<>();
    ValueEventListener postListener2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_calendar);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userDataDB = database.getReference("Users");
        calendarDataDB = database.getReference("Calendars");
        simpleCalendarView = findViewById(R.id.simpleCalendarView);
        okBtn = findViewById(R.id.OkBtn);
        qrGenerator = findViewById(R.id.QRGeneratorBtn);
        share = findViewById(R.id.shareBtn);

        if (getIntent().hasExtra("key")) {
            Bundle getData = getIntent().getExtras();
            calendarKey = getData.getString("key");
        }

        postListener2 = new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("MineCalendar", dataSnapshot.toString());
                ((TextView) findViewById(R.id.calendarTitle)).setText(dataSnapshot.child("CalendarName").getValue().toString());

                calendarKeyQR = dataSnapshot.getKey();
                calendarNameQR = dataSnapshot.child("CalendarName").getValue().toString();

                Calendar minDate = Calendar.getInstance();
                minDate.set(Integer.parseInt(dataSnapshot.child("MinDate").child("Year").getValue().toString()), Integer.parseInt(dataSnapshot.child("MinDate").child("Month").getValue().toString()), Integer.parseInt(dataSnapshot.child("MinDate").child("Day").getValue().toString()));
                Calendar maxDate = Calendar.getInstance();
                maxDate.set(Integer.parseInt(dataSnapshot.child("MaxDate").child("Year").getValue().toString()), Integer.parseInt(dataSnapshot.child("MaxDate").child("Month").getValue().toString()), Integer.parseInt(dataSnapshot.child("MaxDate").child("Day").getValue().toString()));

                simpleCalendarView.setMaximumDate(maxDate);
                simpleCalendarView.setMinimumDate(minDate);

                if (!dataSnapshot.child("UserOwner").getValue().toString().equals(currentUser.getUid())) {
                    qrGenerator.setVisibility(View.GONE);
                }

                List<Calendar> disbledDates = new ArrayList<>();

                List<EventDay> events = new ArrayList<>();
                for (DataSnapshot propertySnapshot : dataSnapshot.child("Users").getChildren()) {
                    for (DataSnapshot snapshotDates : propertySnapshot.child("SelectedDates").getChildren()) {
                        if (snapshotDates.getChildrenCount() == 3) {
                            Calendar calendar = Calendar.getInstance();

                            calendar.set(Integer.parseInt(snapshotDates.child("Year").getValue().toString()), Integer.parseInt(snapshotDates.child("Month").getValue().toString()), Integer.parseInt(snapshotDates.child("Day").getValue().toString()));
                            EventDay evDay = eventExists(events, calendar);

                            if (evDay != null) {
                                if (evDay.getImageDrawable().equals(R.drawable.sample_circle)) {
                                    events.remove(evDay);
                                    events.add(new EventDay(calendar, R.drawable.sample_two_icons));
                                } else if (evDay.getImageDrawable().equals(R.drawable.sample_two_icons)) {
                                    events.remove(evDay);
                                    events.add(new EventDay(calendar, R.drawable.sample_three_icons));
                                } else if (evDay.getImageDrawable().equals(R.drawable.sample_three_icons)) {
                                    events.remove(evDay);
                                    events.add(new EventDay(calendar, R.drawable.sample_four_icons));
                                }
                            } else {
                                events.add(new EventDay(calendar, R.drawable.sample_circle));
                            }

                            if (propertySnapshot.getKey().equals(currentUser.getUid())) {
                                userEvents.add(calendar);
                            }
                        }
                    }
                }

                simpleCalendarView.setDisabledDays(disbledDates);

                simpleCalendarView.setEvents(events);
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        calendarDataDB.child(calendarKey).addListenerForSingleValueEvent(postListener2);

        okBtn.setOnClickListener(view -> {
            for (Calendar calendar : simpleCalendarView.getSelectedDates()) {
                int exists = calendarExists(userEvents, calendar);
                if (exists != -1) {
                    userEvents.remove(exists);
                } else {
                    userEvents.add(calendar);
                }
            }

            calendarDataDB.child(calendarKey).child("Users").child(currentUser.getUid()).child("SelectedDates").removeValue();

                int j = 0;
                for (Calendar calendar : userEvents) {
                    System.out.println(calendar.getTime().toString());

                    calendarDataDB.child(calendarKey).child("Users").child(currentUser.getUid()).child("SelectedDates").child(Integer.toString(j)).child("Year").setValue(calendar.get(Calendar.YEAR));
                    calendarDataDB.child(calendarKey).child("Users").child(currentUser.getUid()).child("SelectedDates").child(Integer.toString(j)).child("Month").setValue(calendar.get(Calendar.MONTH));
                    calendarDataDB.child(calendarKey).child("Users").child(currentUser.getUid()).child("SelectedDates").child(Integer.toString(j)).child("Day").setValue(calendar.get(Calendar.DAY_OF_MONTH));
                    j++;
                }

            finish();
            startActivity(getIntent());
        });

        qrGenerator.setOnClickListener(view -> {
            inputValue = calendarKeyQR + "\\|" + calendarNameQR;

            QRCodeWriter writer = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = writer.encode(inputValue, BarcodeFormat.QR_CODE, 512, 512);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }

                AlertDialog.Builder ImageDialog = new AlertDialog.Builder(ViewCalendar.this);
                ImageDialog.setTitle("Calendario para compartir");
                ImageView showImage = new ImageView(ViewCalendar.this);
                showImage.setImageBitmap(bmp);
                ImageDialog.setView(showImage);

                ImageDialog.setNegativeButton("Ok", (arg0, arg1) -> {
                });
                ImageDialog.show();
            } catch (WriterException e) {
                e.printStackTrace();
            }
        });

        share.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Proyecto Calendario");
            i.putExtra(Intent.EXTRA_TEXT, "Aquí tienes el calendario: www.proyectocalendario.com/" + calendarKeyQR + "_" + calendarNameQR);
            startActivity(Intent.createChooser(i, "Elige"));

        });
    }

    private EventDay eventExists(List<EventDay> events, Calendar calendar) {
        for (EventDay eventDay : events) {
            if (eventDay.getCalendar().get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                if (eventDay.getCalendar().get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                    if (eventDay.getCalendar().get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                        return eventDay;
                    }
                }
            }
        }

        return null;
    }

    private int calendarExists(List<Calendar> dates, Calendar calendar) {
        int i = -1;
        for (Calendar date : dates) {
            if (date.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                if (date.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                    if (date.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                        return ++i;
                    }
                }
            }
            ++i;
        }

        return -1;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
