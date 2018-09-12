package com.viettelpost.remoteconfig.myremoteconfig;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends AppCompatActivity {

    private static final String LOADING_PHRASE_CONFIG_KEY = "loading_phrase";
    private static final String WELCOME_MESSAGE_KEY = "welcome_message";
   // private static final String WELCOME_MESSAGE_CAPS_KEY = "welcome_message_caps";
    private static final String CHECK_ACTION = "check_action_bar";
    private static final String CHECK_THEME = "set_theme";

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private Button btnLoad;
    private ImageView imgTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoad = findViewById(R.id.btnLoad);
        imgTheme = findViewById(R.id.imageView);

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToatLoading();
            }
        });

        // Goị yêu cầu RemoteConfig.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();


        //Bật chế độ dev_mode
        FirebaseRemoteConfigSettings ConfigSettings = new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        mFirebaseRemoteConfig.setConfigSettings(ConfigSettings);

        //Connect giao dien voi remote, set defau
        mFirebaseRemoteConfig.setDefaults(R.xml.set_defaults);

        ToatLoading();
    }
    private void ToatLoading() {
        long ex = 3600;

        //nếu đang để chế độ nhà phát triển thì "ex" sẽ được gán giá trị = 0.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            ex = 0;
        }
        //Bắt đầu lấy giá trị từ remote
        mFirebaseRemoteConfig.fetch(ex).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Fetch Succeeded",
                            Toast.LENGTH_SHORT).show();

                    mFirebaseRemoteConfig.activateFetched();
                } else {
                    Toast.makeText(MainActivity.this, "Fetch Failed",
                            Toast.LENGTH_SHORT).show();
                }

                getDataUpdate();

            }
        });

    }

    private void getDataUpdate() {

        Boolean OnOffActionBar =  mFirebaseRemoteConfig.getBoolean(CHECK_ACTION);
        Boolean OnOffTheme =  mFirebaseRemoteConfig.getBoolean(CHECK_THEME);
        String o = mFirebaseRemoteConfig.getString(WELCOME_MESSAGE_KEY);

        Log.e("bbbb", OnOffActionBar.toString() + OnOffTheme.toString() + o);
        Theme(OnOffTheme);
        Action(OnOffActionBar);
    }

    private void Theme(Boolean B) {
        Boolean in = false;
        if (B == true) {
            imgTheme.setImageDrawable(getResources().getDrawable(R.drawable.city));
            btnLoad.setBackgroundColor(Color.parseColor("#ccffff"));
            in = B;
        } else if (B == false) {
            imgTheme.setImageDrawable(getResources().getDrawable(R.drawable.japan));
            btnLoad.setBackgroundColor(Color.parseColor("#000000"));
            btnLoad.setTextColor(Color.parseColor("#ffffff"));
            in = B;
        } else {
            Toast.makeText(MainActivity.this, "Khong co gi thay doi", Toast.LENGTH_SHORT).show();
        }
    }

    private void Action(Boolean A) {
        if (A == true) {
            try {
                View decorView = getWindow().getDecorView();

                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                this.getSupportActionBar().hide();
            } catch (NullPointerException e) {
                Log.e("Error: ", e.getMessage());
            }

        } else {
            try {
                this.getSupportActionBar().show();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void SetTheme(Boolean ST ) {
        if(ST == true){
            btnLoad.setTextColor(Color.parseColor("#000"));
        }
    }
}
