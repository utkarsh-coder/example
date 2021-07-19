package com.test.listennotification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private int status;
    private int language;
    private Switch aSwitch;
    private SharedPreferences sharedPreferences;
    RadioGroup radioGroup;
    private RadioButton radioButtonEnglish;
    private RadioButton radioButtonHindi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        aSwitch = findViewById(R.id.switch_enable_disable);
        radioGroup = findViewById(R.id.radioGroup_language);
        radioButtonEnglish = findViewById(R.id.radioButton_English);
        radioButtonHindi = findViewById(R.id.radioButton_Hindi);

        sharedPreferences=getSharedPreferences("notificationListener_sharedPreferences", Context.MODE_PRIVATE);
        status = sharedPreferences.getInt("status",1);
        language = sharedPreferences.getInt("language",0);

        if(status == 0){
            aSwitch.setChecked(false);
        }
        else{
            aSwitch.setChecked(true);
        }

        if(language == 0) {
            radioGroup.check(R.id.radioButton_English);
        }
        else {
            radioGroup.check(R.id.radioButton_Hindi);
        }

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(aSwitch.isChecked()) {
                    editor.putInt("status", 1);
                }
                else{
                    editor.putInt("status", 0);
                }
                editor.commit();

            }
        });

        radioButtonEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("language",0);
                editor.commit();
            }
        });

        radioButtonHindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("language",1);
                editor.commit();
            }
        });
    }
}