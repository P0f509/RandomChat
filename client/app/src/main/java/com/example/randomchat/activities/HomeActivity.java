package com.example.randomchat.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.randomchat.R;
import com.example.randomchat.controller.Controller;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;


public class HomeActivity extends AppCompatActivity {

    private ConstraintLayout layout;

    private final HomeFragment homeFragment = new HomeFragment();
    private final LastChatsFragment lastChatsFragment = new LastChatsFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        layout = findViewById(R.id.layout);

        Controller controller = Controller.getInstance();
        controller.setHomeActivity(this);

        Bundle homeBundle = new Bundle();
        homeBundle.putSerializable("rooms", getIntent().getExtras().getSerializable("rooms"));
        homeFragment.setArguments(homeBundle);

        Bundle profileBundle = new Bundle();
        profileBundle.putString("nickname", getIntent().getExtras().getString("nickname"));
        profileFragment.setArguments(profileBundle);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {

                    case 0:
                        changeFragment(homeFragment);
                        break;

                    case 1:
                        changeFragment(lastChatsFragment);
                        break;

                    case 2:
                        changeFragment(profileFragment);
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}

        });

        changeFragment(homeFragment);

    }


    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }


    public void onFail(String msg) {
        runOnUiThread(()-> {
            Snackbar snackbar = Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.error));

            TextView tv = (snackbar.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.euclid_circular_regular);
            tv.setTypeface(typeface);

            snackbar.show();
        });
    }

}
