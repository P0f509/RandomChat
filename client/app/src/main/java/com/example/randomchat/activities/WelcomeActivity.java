package com.example.randomchat.activities;

import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.randomchat.R;
import com.example.randomchat.controller.Controller;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class WelcomeActivity extends AppCompatActivity {

    private TextInputLayout textInputLayout;
    private TextInputEditText nicknameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Controller controller = Controller.getInstance();
        controller.setWelcomeActivity(this);

        textInputLayout = findViewById(R.id.textInput);

        nicknameEditText = findViewById(R.id.editText);
        nicknameEditText.setOnClickListener(view -> textInputLayout.setError(null));
        nicknameEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(view -> {
            if(isNicknameValid()) {
                controller.setUp(Objects.requireNonNull(nicknameEditText.getText()).toString());
            }
        });

    }

    private boolean isNicknameValid() {
        if(nicknameEditText.getText() == null || nicknameEditText.getText().length() < 1) {
            textInputLayout.setError(getString(R.string.nickname_length_error));
            return false;
        }

        if(nicknameEditText.getText().toString().matches(".*\\s+.*")) {
            textInputLayout.setError(getString(R.string.nickname_spaces_error));
            return false;
        }

        return true;
    }

}