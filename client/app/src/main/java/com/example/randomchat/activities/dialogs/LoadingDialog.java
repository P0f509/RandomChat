package com.example.randomchat.activities.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.randomchat.R;
import com.example.randomchat.controller.Controller;


public class LoadingDialog {

    private final Activity activity;
    private AlertDialog dialog;
    private final String loadingMessage;

    public LoadingDialog(Activity activity, String loadingMessage){
        this.loadingMessage = loadingMessage;
        this.activity = activity;
    }

    @SuppressLint("InflateParams")
    public void startLoading() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_loading, null);
        TextView loadingText = view.findViewById(R.id.loadingTextView2);
        loadingText.setText(loadingMessage);

        Button exitButton = view.findViewById(R.id.exit_button);
        exitButton.setOnClickListener(view1 -> {
            Controller.getInstance().leaveRoom();
            dialog.dismiss();
        });

        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    public void dismissDialog() {
        dialog.dismiss();
    }

}
