package com.example.vegas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class scheduler extends AppCompatActivity {

    private static final String LOG_TAG = scheduler.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);
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
                Intent logout = new Intent(this, MainActivity.class);
                startActivity(logout);
                finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu){
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    public void poolPicked(View view){
        Intent intent = new Intent(this, dates.class);
        intent.putExtra("which", "pool");
        startActivity(intent);
    }

    public void snookerPicked(View view){
        Intent intent = new Intent(this, dates.class);
        intent.putExtra("which", "snooker");
        startActivity(intent);
    }

    public void dartsPicked(View view){
        Intent intent = new Intent(this, dates.class);
        intent.putExtra("which", "darts");
        startActivity(intent);
    }

}