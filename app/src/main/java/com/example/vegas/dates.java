package com.example.vegas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class dates extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String LOG_TAG = dates.class.getName();

    private Spinner spinner;
    private String selectedDate;
    private String picked;

    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private Button reserveButton;
    private TextView hint;
    private NotificationHandler mNotificationHandler;


    private ArrayList<Reserved_date> mDates;

    private FirebaseFirestore db;
    private CollectionReference mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dates);


        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        selectedDate = getTodaysDate();
        dateButton.setText(selectedDate);
        mDates = new ArrayList<Reserved_date>();

        reserveButton = findViewById(R.id.reserve);
        TextView type = findViewById(R.id.type);
        hint = findViewById(R.id.textView6);

        spinner = findViewById(R.id.spinner);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            picked = extras.getString("which");
        }else {
            Log.e(LOG_TAG, "Could not find picked option");
            finish();
        }
        type.setText(picked);
        spinner.setOnItemSelectedListener(this);

        db = FirebaseFirestore.getInstance();
        mItems = db.collection("reserved_dates");

        mNotificationHandler = new NotificationHandler(this);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.vegas_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.lemond:
                Log.d(LOG_TAG, "Lemond is clicked");
                Intent delete = new Intent(this, Delete.class);
                startActivity(delete);
                return true;
            case R.id.logout:
                Log.d(LOG_TAG, "Logout is clicked");
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                selectedDate = makeDateString(day, month, year);
                dateButton.setText(selectedDate);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        switch (month){
            case 2: return "FEB";
            case 3: return "MAR";
            case 4: return "APR";
            case 5: return "MAY";
            case 6: return "JUN";
            case 7: return "JUL";
            case 8: return "AUG";
            case 9: return "SEP";
            case 10: return "OKT";
            case 11: return "NOV";
            case 12: return "DEC";
            default: return "JAN";
        }
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }

    public void show(View view){
        Query datesQuery = mItems.whereEqualTo("date", selectedDate).orderBy("type");
        Log.e(LOG_TAG, "Selected date: " + selectedDate);
        mDates.clear();
        datesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Reserved_date reserved_date = document.toObject(Reserved_date.class);

                        mDates.add(reserved_date);
                        Log.d(LOG_TAG, "Query Successful");

                    }
                }else{
                    Log.e(LOG_TAG, "Query Failed: " + task.getException().getMessage());

                }
                showAvailable();
            }
        });
    }

    public void showAvailable(){
        List<String> free_periods = new ArrayList<String>();
        boolean match;
        for (int i = 16; i < 24; i++) {
            if (!mDates.isEmpty()){
                match = false;
                for (int j = 0; j < mDates.size(); j++){
                    if (mDates.get(j).getTime().equals(i + ":00") && mDates.get(j).getType().equals(picked)){
                        match = true;
                    }
                }
                if (!match){
                    free_periods.add(i + ":00");
                }
            }else{
                free_periods.add(i + ":00");
            }
        }

        for (String item:
             free_periods) {
            Log.e(LOG_TAG, item);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, free_periods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spinner);
        sItems.setAdapter(adapter);

        hint.setVisibility(View.VISIBLE);
        reserveButton.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
    }

    public void reserve(View view){
        Intent intent = new Intent(this, scheduler.class);
        mItems.add(new Reserved_date(selectedDate, spinner.getSelectedItem().toString(), picked, FirebaseAuth.getInstance().getCurrentUser().getEmail())).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    Toast.makeText(dates.this, "Sikeres Foglalás", Toast.LENGTH_SHORT).show();
                    mNotificationHandler.send("Köszönjük foglalásod, várunk: " + selectedDate + ", " + spinner.getSelectedItem().toString() + "-kor");
                    startActivity(intent);
                }else{
                    Toast.makeText(dates.this, "Foglalás sikertelen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void back(View view){
        Intent intent = new Intent(this, scheduler.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {}

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
}