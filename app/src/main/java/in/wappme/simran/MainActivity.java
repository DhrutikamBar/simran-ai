package in.wappme.simran;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import in.wappme.simran.Adapters.MessageAdapterSimran;
import in.wappme.simran.Models.MessageModel;

public class MainActivity extends AppCompatActivity {

    SpeechRecognizer speechRecognizer;
    FloatingActionButton micButton;
    

    TextToSpeech textToSpeech;
    String BRAIN_SHOP_API = "http://api.brainshop.ai/get?bid=159275&key=Z6IHwWiNHCNi9n3T&uid=10&msg=";
    String bid = "159275";
    String key = "Z6IHwWiNHCNi9n3T";
    String uid = "10";
    String msg = "";
    String USER_KEY = "user",BOT_KEY ="bot";
    ArrayList<MessageModel> messageModelArrayList;
    MessageAdapterSimran messageAdapterSimran;
    RecyclerView recyclerView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageModelArrayList = new ArrayList<>();
        messageAdapterSimran = new MessageAdapterSimran(this,messageModelArrayList);
        getSupportActionBar().hide();
        findById();

        initializeTextToSpeech();
        initializeResult();

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdapterSimran);

    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i!= TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.UK);
                    textToSpeech.speak("Hii chikul How can i help u ?",TextToSpeech.QUEUE_FLUSH,null);
                   messageModelArrayList.add(new MessageModel("Hii chikul How can i help u ?",BOT_KEY));
                   messageAdapterSimran.notifyDataSetChanged();

                }
            }
        });
    }

    public void replyResponse(String query){
      //  messageModelArrayList.add(new MessageModel("Hii chikul How can i help u ?",USER_KEY));
        String q = query.toLowerCase();
        String[] qry = q.split(" ");

       if (qry[0].equals("open")&& qry.length>1 && qry.length<=5){
           if (q.contains("instagram")){
                   isAppInstalled("com.instagram.android");
           }else if (q.contains("facebook")){
               isAppInstalled("com.facebook.katana");

           }else if (q.contains("linkedin")){
              isAppInstalled("com.linkedin.android");

           }else if( q.contains("twitter")){
             isAppInstalled("com.twitter.android");

           }else if (q.contains("snapchat")){
              isAppInstalled("com.snapchat.android");

           }else if (q.contains("whatsapp")){
              isAppInstalled("com.whatsapp");

           }else if (q.contains("telegram")){
              isAppInstalled("org.telegram.messenger");

           }else if (q.contains("youtube")){
               isAppInstalled("com.google.android.youtube");

           }else{
               speakMassage("LOL!, I can't understand !");
               messageModelArrayList.add(new MessageModel("LOL!, I can't understand !",BOT_KEY));
           }

       }else{
           getResponseFromApi(q);
       }

    }

    public void isAppInstalled(String packageName){
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(packageName,PackageManager.GET_ACTIVITIES);
            Intent intent = this.getPackageManager().getLaunchIntentForPackage(packageName);
            this.startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+packageName)));
           // speakMassage("This app is not installed on device");
        }

    }

    public void getResponseFromApi(String query){
        messageAdapterSimran.notifyDataSetChanged();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, BRAIN_SHOP_API+query,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               // Toast.makeText(MainActivity.this, "Response"+response, Toast.LENGTH_SHORT).show();
                try {
                    String res = response.getString("cnt");
                    messageModelArrayList.add(new MessageModel(res,BOT_KEY));
                    speakMassage(res);
                    messageAdapterSimran.notifyDataSetChanged();

                   // Toast.makeText(MainActivity.this, "Response "+res, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error at MainAc 2 :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    messageModelArrayList.add(new MessageModel("No response", BOT_KEY));
                    messageAdapterSimran.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MainActivity.this, "Error at MainAc 1 :"+error.getMessage(), Toast.LENGTH_SHORT).show();
                messageModelArrayList.add(new MessageModel("Sorry No response found !", BOT_KEY));
                messageAdapterSimran.notifyDataSetChanged();
            }
        })

          ;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
    private void speakMassage(String massage){
        textToSpeech.speak(massage,TextToSpeech.QUEUE_FLUSH,null);
    }

    private void initializeResult() {
        if (SpeechRecognizer.isRecognitionAvailable(this)){
             speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
             speechRecognizer.setRecognitionListener(new RecognitionListener() {
                 @Override
                 public void onReadyForSpeech(Bundle bundle) {

                 }

                 @Override
                 public void onBeginningOfSpeech() {

                 }

                 @Override
                 public void onRmsChanged(float v) {
                    // micStatusTextView.setText("Listening...");
                 }

                 @Override
                 public void onBufferReceived(byte[] bytes) {

                 }

                 @Override
                 public void onEndOfSpeech() {

                 }

                 @Override
                 public void onError(int i) {

                 }

                 @Override
                 public void onResults(Bundle bundle) {
                     ArrayList<String> result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    // micStatusTextView.setText("Say anything...");
                   //  Toast.makeText(MainActivity.this, ""+result.get(0), Toast.LENGTH_SHORT).show();
                     // responseTextView.setText(result.get(0));
                    // getResponseFromApi(result.get(0));
                     messageModelArrayList.add(new MessageModel(result.get(0),USER_KEY));
                     replyResponse(result.get(0));



                 }

                 @Override
                 public void onPartialResults(Bundle bundle) {

                 }

                 @Override
                 public void onEvent(int i, Bundle bundle) {

                 }
             });
        }
    }

    public void startRecording(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        speechRecognizer.startListening(intent);
    }

    private void findById() {
        micButton = findViewById(R.id.micButton);
        recyclerView = findViewById(R.id.chatRecyclerView);

    }
}