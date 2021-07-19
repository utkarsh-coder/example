package com.test.listennotification;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.Locale;

public class NotificationListener extends NotificationListenerService {

    private int status;
    private int language;
    private TextToSpeech tts;
    private SharedPreferences sharedPreferences;
    public String speakText;

    public void translate(String text, String fromLanguage, String toLanguage){
        TranslatorOptions options = new TranslatorOptions.Builder()
                        .setSourceLanguage(fromLanguage)
                        .setTargetLanguage(toLanguage)
                        .build();

                Translator translator = Translation.getClient(options);
                DownloadConditions conditions = new DownloadConditions.Builder().build();
                translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        translator.translate(text).addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                speakText = s;
                                tts.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, "");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("info", "Text not Translated");
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("info", "Error while downloading required file");
                    }
                });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences=getSharedPreferences("notificationListener_sharedPreferences", Context.MODE_PRIVATE);
        status = sharedPreferences.getInt("status",1);
        language = sharedPreferences.getInt("language",0);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS){
                    int result;
                    if(language==0)
                        result = tts.setLanguage(new Locale("en"));
                    else
                        result = tts.setLanguage(new Locale("hi"));

                    if( result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("testing", "language not supported");
                    } else {
                    }
                } else {
                    Log.d("testing", "initialization failed");
                }
            }
        });
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        String text = extras.getCharSequence("android.text").toString();
        if((title!=null || text!=null) && (title!="" || text!="")) {
            speakText = text + "from " + title;
        }
        else{
            speakText = "";
        }

        status = sharedPreferences.getInt("status",1);
        language = sharedPreferences.getInt("language",0);

        if(speakText != null && speakText != "") {
            if (status == 1) {
                if (language == 0) {
                    tts.setLanguage(new Locale("en"));
                    tts.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, "");
                } else {
                    tts.setLanguage(new Locale("hi"));
                    translate(speakText, TranslateLanguage.ENGLISH, TranslateLanguage.HINDI);
                }
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("testing", "Service Destroyed");
    }
}
