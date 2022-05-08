package com.example.vegas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Delete extends AppCompatActivity {

    private static final String LOG_TAG = Delete.class.getName();

    private FirebaseFirestore db;
    private CollectionReference mItems;
    private List<Reserved_date> mReservedDates;
    private TextView none;
    private Spinner deleteSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        db = FirebaseFirestore.getInstance();
        mItems = db.collection("reserved_dates");
        mReservedDates = new ArrayList<Reserved_date>();
        none = findViewById(R.id.nincs);
        deleteSpinner = findViewById(R.id.deleteSpinner);

        Query userDates = mItems.whereEqualTo("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail()).orderBy("date", Query.Direction.ASCENDING);
        userDates.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Reserved_date tmp = document.toObject(Reserved_date.class);
                        mReservedDates.add(tmp);
                        Log.i(LOG_TAG, "Query Successful: " + tmp.getDate());
                    }
                    initData();
                }else{
                    Log.e(LOG_TAG, "Query Failed: " + task.getException().getMessage());
                }
            }
        });
    }

    public void initData(){
        Log.e(LOG_TAG, mReservedDates.isEmpty() ? "True" : "False");
        if (!mReservedDates.isEmpty()){
            List<String> foglaltak = new ArrayList<String>();

            for (int i = 0; i < mReservedDates.size(); i++){
                String tmp = createString(mReservedDates.get(i));
                foglaltak.add(tmp);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, foglaltak);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner sItems = (Spinner) findViewById(R.id.deleteSpinner);
            sItems.setAdapter(adapter);

            none.setVisibility(View.INVISIBLE);
        }else{
            none.setVisibility(View.VISIBLE);
        }
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

    public void back(View view){
        Intent intent = new Intent(this, scheduler.class);
        startActivity(intent);
    }

    public String createString(Reserved_date date){
        return "Időpont: " + date.getDate() + ", " + date.getTime() + "  Játék: " + date.getType();
    }

    public void deleteReservation(View view){

        Query query = mItems.whereEqualTo("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        Intent intent = new Intent(this, scheduler.class);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document:
                         task.getResult()) {
                        Reserved_date tmp = document.toObject(Reserved_date.class);
                        if (createString(tmp).equals(deleteSpinner.getSelectedItem())){
                            document.getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Log.i(LOG_TAG, "Delete Succesful");
                                        Toast.makeText(Delete.this, "Sikeres lemondás", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                    }else{
                                        Log.e(LOG_TAG, "Delete Failed: " + task.getException().getMessage());
                                        Toast.makeText(Delete.this, "Lemondás Sikertelen", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }else{
                    Log.e(LOG_TAG, "Query Failed: " + task.getException().getMessage());
                }
            }
        });

    }

}