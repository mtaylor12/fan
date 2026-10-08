package com.mydomain.fan;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private FanService fanService;
    private boolean bound = false;

    private ImageButton playButton;
    private ImageButton stopButton;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FanService.FanBinder binder = (FanService.FanBinder) service;
            fanService = binder.getService();
            bound = true;
            updateButtons();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playButton = findViewById(R.id.playButton);
        stopButton = findViewById(R.id.stopButton);

        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, FanService.class);
            intent.setAction(FanService.ACTION_PLAY);
            startService(intent);
            if (!bound) {
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
            }
            updateButtons();
        });

        stopButton.setOnClickListener(v -> {
            if (bound) {
                Intent intent = new Intent(this, FanService.class);
                intent.setAction(FanService.ACTION_STOP);
                startService(intent);
                updateButtons();
            }
        });

        // Bind to service if already running
        Intent intent = new Intent(this, FanService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void updateButtons() {
        boolean playing = bound && fanService != null && fanService.isPlaying();
        playButton.setEnabled(!playing);
        stopButton.setEnabled(playing);
        playButton.setAlpha(playing ? 0.4f : 1.0f);
        stopButton.setAlpha(playing ? 1.0f : 0.4f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButtons();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }
}
