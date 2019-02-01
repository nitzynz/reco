package com.example.kannanb.reco;


import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import java.util.ArrayList;
import java.util.Locale;
import java.io.IOException;

import android.speech.RecognizerIntent;
import android.speech.RecognitionListener;

import android.util.Log;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;


public class reco extends AppCompatActivity {

    Button bstrt1,brstop,bplay,bstop;
    private SpeechRecognizer speerec;
    private Intent intent;
    TextView tv;
    MediaRecorder mrec;
    MediaPlayer mplay;
    String path=null;
    static int count=0;
    public static final int RequestPermissionCode = 1;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private boolean islistening;
    public static final String TAG = "YOUR-TAG-NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reco);
        //final SimpleDateFormat sm = new SimpleDateFormat("dd-yyyy-MM HHmm");
        tv = findViewById(R.id.text1);
        bstrt1 = findViewById(R.id.button);
        brstop = findViewById(R.id.button2);
        bplay = findViewById(R.id.button3);
        bstop = findViewById(R.id.button4);
        brstop.setEnabled(false);
        bplay.setEnabled(false);
        bstop.setEnabled(false);
        speerec = SpeechRecognizer.createSpeechRecognizer(this);
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,this.getPackageName());
        //intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        final SpeechRecognitionListener listener = new SpeechRecognitionListener();
        speerec.setRecognitionListener(listener);
        bstrt1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(checkPermission()){
                    path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+count+"audrec.3gp";


                    mrecready();
                    count++;
                    try{
                        //startVoiceInput();
                        if(!islistening){
                            speerec.startListening(intent);
                        }
                        mrec.prepare();
                        mrec.start();
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IllegalStateException e){
                        e.printStackTrace();
                    }
                    Toast.makeText(reco.this, "Recording started", Toast.LENGTH_LONG).show();

                    brstop.setEnabled(true);
                    bstrt1.setEnabled(false);

                }
                else {
                    requestPermission();
                }
            }
        });

        brstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    mrec.stop();


                Toast.makeText(reco.this, "Recording stopped", Toast.LENGTH_LONG).show();
                bstrt1.setEnabled(true);
                brstop.setEnabled(false);
                bplay.setEnabled(true);
                bstop.setEnabled(false);
            }


        });

        bplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException{

                bstrt1.setEnabled(false);
                brstop.setEnabled(false);
                bstop.setEnabled(true);
                bplay.setEnabled(false);
                mplay = new MediaPlayer();
                try{
                    mplay.setDataSource(path);
                    mplay.prepare();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                mplay.start();
                Toast.makeText(reco.this, "Recording Playing", Toast.LENGTH_LONG).show();
            }
        });


        bstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bstrt1.setEnabled(true);
                brstop.setEnabled(false);
                bplay.setEnabled(true);
                bstop.setEnabled(false);
                if(mplay!= null){
                    mplay.stop();
                    mplay.release();
                    mrecready();
                }
            }
        });






    }
    public void mrecready(){
        mrec = new MediaRecorder();
        mrec.setAudioSource(MediaRecorder.AudioSource.MIC);
        mrec.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mrec.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mrec.setOutputFile(path);
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(reco.this, new String[]{WRITE_EXTERNAL_STORAGE,RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(reco.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(reco.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void startVoiceInput() {


        /* try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        }
        catch (ActivityNotFoundException a) {

        }*/
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tv.setText(result.get(0));
                }
                break;
            }

        }
    }*/


    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onBeginningOfSpeech()
        {
            //Log.d(TAG, "onBeginingOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            //Log.d(TAG, "onEndOfSpeech");
        }

        @Override
        public void onError(int error)
        {
            speerec.startListening(intent);

            //Log.d(TAG, "error = " + error);
        }
        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results)
        {
            //Log.d(TAG, "onResults"); //$NON-NLS-1$
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String temp = matches.get(0);
            if(temp.toLowerCase().contains("leave")|| temp.toLowerCase().contains("absent"))
            tv.setText("Leave Granted!");
            // matches are the return values of speech recognition engine
            // Use these values for whatever you wish to do
        }

        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(speerec != null){
            speerec.destroy();
        }
    }
}
