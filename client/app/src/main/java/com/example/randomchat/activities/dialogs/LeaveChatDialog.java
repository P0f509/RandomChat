package com.example.randomchat.activities.dialogs;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.res.ResourcesCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.randomchat.R;
import com.example.randomchat.controller.Controller;

public class LeaveChatDialog extends AppCompatDialogFragment {

    private Button btnPositive;
    private Button btnNegative;

    private boolean wantsToClose = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_leave_chat, null);

        LottieAnimationView lottieAnimationView = view.findViewById(R.id.deleteAnimation);
        lottieAnimationView.setFrame(150);
        lottieAnimationView.setSpeed(1.5F);

        builder.setView(view).setNegativeButton(R.string.no_text, null);
        builder.setView(view).setPositiveButton(R.string.yes_text, null);

        final AlertDialog leaveChatDialog = builder.create();

        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {

                btnNegative.setClickable(true);
                btnPositive.setClickable(true);

                leaveChatDialog.dismiss();

                if(wantsToClose) {
                    requireActivity().finish();
                    Controller.getInstance().leaveChat();
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}

        });

        leaveChatDialog.setOnShowListener(dialog -> {

            btnPositive = leaveChatDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnPositive.setTextSize(16);

            Typeface typefacePositive = ResourcesCompat.getFont(requireContext(), R.font.euclid_circular_medium);
            btnPositive.setTypeface(typefacePositive);

            btnNegative = leaveChatDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            btnNegative.setTextSize(16);

            Typeface typefaceNegative = ResourcesCompat.getFont(requireContext(), R.font.euclid_circular_medium);
            btnPositive.setTypeface(typefaceNegative);
            btnPositive.setTextColor(ResourcesCompat.getColor(getResources(), R.color.error, null));

            btnNegative.setOnClickListener(view1 -> {
                btnNegative.setClickable(false);
                btnPositive.setClickable(false);
                lottieAnimationView.setMaxFrame(150);
                lottieAnimationView.setFrame(150);
                lottieAnimationView.setSpeed(-1.5F);
                lottieAnimationView.playAnimation();
            });

            btnPositive.setOnClickListener(view1 -> {
                btnNegative.setClickable(false);
                btnPositive.setClickable(false);
                lottieAnimationView.setMinAndMaxFrame(150, 270);
                lottieAnimationView.setSpeed(1.5F);
                lottieAnimationView.playAnimation();
                wantsToClose = true;
            });

        });

        leaveChatDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        return leaveChatDialog;

    }

}
