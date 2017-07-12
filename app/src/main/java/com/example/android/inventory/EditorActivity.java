package com.example.android.inventory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class EditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
    }

    @Override
    public  void onBackPressed(){
        finish();
    }
}
