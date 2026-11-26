package com.prime.jarvis;

import android.app.Activity;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.speech.tts.TextToSpeech;
import android.content.Intent;
import android.widget.*;
import android.graphics.Color;
import android.view.Gravity;
import java.util.*;

public class MainActivity extends Activity {

    TextToSpeech jarvis;
    SpeechRecognizer recognizer;
    TextView status;
    boolean ready = false;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.rgb(5, 12, 25));

        TextView title = new TextView(this);
        title.setText("J.A.R.V.I.S");
        title.setTextSize(36);
        title.setTextColor(Color.CYAN);
        title.setGravity(Gravity.CENTER);

        status = new TextView(this);
        status.setText("SYSTEM ONLINE");
        status.setTextColor(Color.WHITE);
        status.setTextSize(18);
        status.setGravity(Gravity.CENTER);

        Button listen = new Button(this);
        listen.setText("TALK TO JARVIS");

        layout.addView(title);
        layout.addView(status);
        layout.addView(listen);

        setContentView(layout);

        jarvis = new TextToSpeech(this, result -> {
            if (result == TextToSpeech.SUCCESS) {
                ready = true;
                jarvis.speak("Hello. JARVIS is online.", TextToSpeech.QUEUE_FLUSH, null, "start");
            }
        });

        recognizer = SpeechRecognizer.createSpeechRecognizer(this);

        listen.setOnClickListener(v -> listen());

    }

    void listen() {
        status.setText("LISTENING...");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        recognizer.setRecognitionListener(new RecognitionListener() {
            public void onReadyForSpeech(Bundle b) {}
            public void onBeginningOfSpeech() {}
            public void onRmsChanged(float r) {}
            public void onBufferReceived(byte[] b) {}
            public void onEndOfSpeech() {}

            public void onError(int e) {
                status.setText("TRY AGAIN");
            }

            public void onResults(Bundle b) {
                ArrayList<String> words =
                    b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (words != null && !words.isEmpty()) {
                    respond(words.get(0).toLowerCase());
                }
            }

            public void onPartialResults(Bundle b) {}
            public void onEvent(int a, Bundle b) {}
        });

        recognizer.startListening(intent);
    }

    void respond(String command) {
        status.setText("YOU: " + command);

        String reply = "I'm listening.";

        if (command.contains("hello")) {
            reply = "Hello. JARVIS is online.";
        }
        else if (command.contains("time")) {
            reply = "The time is " + new java.util.Date().toString();
        }
        else if (command.contains("chrome")) {
            openApp("com.android.chrome");
            reply = "Opening Chrome.";
        }
        else if (command.contains("youtube")) {
            openApp("com.google.android.youtube");
            reply = "Opening YouTube.";
        }
        else if (command.contains("settings")) {
            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
            reply = "Opening settings.";
        }
        else if (command.contains("stop")) {
            reply = "Standing by.";
        }

        status.setText(reply);

        if (ready) {
            jarvis.speak(reply, TextToSpeech.QUEUE_FLUSH, null, "reply");
        }
    }

    void openApp(String name) {
        Intent i = getPackageManager().getLaunchIntentForPackage(name);
        if (i != null) startActivity(i);
        else status.setText("App not found.");
    }

    @Override
    protected void onDestroy() {
        if (recognizer != null) recognizer.destroy();
        if (jarvis != null) jarvis.shutdown();
        super.onDestroy();
    }
}
