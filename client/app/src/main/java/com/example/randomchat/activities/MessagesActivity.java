package com.example.randomchat.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.randomchat.R;
import com.example.randomchat.activities.adapters.MessageAdapter;
import com.example.randomchat.activities.dialogs.LeaveChatDialog;
import com.example.randomchat.controller.Controller;
import com.example.randomchat.entities.Message;
import com.example.randomchat.entities.User;
import com.google.android.material.appbar.MaterialToolbar;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class MessagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editMessage;
    private SpeechRecognizer speechRecognizer;

    private final int MIC_REQ_CODE = 1;

    private final ArrayList<Message> messages = new ArrayList<>();

    private final ActivityResultLauncher<Intent> getTextFromSpeech = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> strings = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editMessage.setText(strings.get(0));
                }
            }
    );

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        MaterialToolbar materialToolbar = findViewById(R.id.topAppBar);
        recyclerView = findViewById(R.id.recyclerView);
        editMessage = findViewById(R.id.editMessage);
        ImageButton buttonSend = findViewById(R.id.buttonSend);
        ImageButton buttonSpeech = findViewById(R.id.voice_recognition);

        Controller controller = Controller.getInstance();
        controller.setMessagesActivity(this);

        Bundle bundle = getIntent().getExtras();
        User currentUser = (User) bundle.getSerializable("currentUser");

        materialToolbar.setTitle(getString(R.string.chatting_text)+" "+bundle.getString("friend"));
        materialToolbar.setNavigationOnClickListener(view -> {
            LeaveChatDialog dialog = new LeaveChatDialog();
            dialog.show(getSupportFragmentManager(), "leaveChat");
        });

        TextView timerText = findViewById(R.id.time);
        new CountDownTimer(TimeUnit.MINUTES.toMillis(10), 1000) {

            @Override
            public void onTick(long l) {
                int minutes = (int) (l/1000)/60;
                int second = (int) (l/1000) % 60;

                String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, second);
                timerText.setText(time);
            }

            @Override
            public void onFinish() {
                finish();
                controller.leaveChat();
                controller.searchFriend(currentUser.getRoom().getNumber());
            }

        }.start();

        MessageAdapter messageAdapter = new MessageAdapter(messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messageAdapter);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        buttonSpeech.setOnClickListener(view -> {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},MIC_REQ_CODE);
            }else {
                getTextFromSpeech.launch(speechRecognizerIntent);
            }
        });

        buttonSend.setOnClickListener(view1 -> {
            String body = editMessage.getText().toString();
            if(body.length() > 0 && body.length() <= 150) {
                Message message = new Message(body, LocalTime.now(), currentUser);
                updateChat(message);
                controller.sendToFriend(message);
                editMessage.getText().clear();
            }else if(body.length() > 150) {
                Toast.makeText(this, getString(R.string.message_too_long), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        LeaveChatDialog dialog = new LeaveChatDialog();
        dialog.show(getSupportFragmentManager(), "leaveChat");
    }

    @Override
    protected void onDestroy() {
        speechRecognizer.destroy();
        super.onDestroy();
    }


    public void updateChat(Message message){
        runOnUiThread(()-> {
            messages.add(message);
            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemInserted(messages.size() - 1);
            recyclerView.scrollToPosition(messages.size() - 1);
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MIC_REQ_CODE && grantResults.length > 0) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.request_mic_permission), Toast.LENGTH_SHORT).show();
            }
        }
    }


}