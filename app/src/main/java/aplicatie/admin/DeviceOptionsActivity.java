package aplicatie.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import aplicatie.admin.controllers.device_options.SectionsPagerAdapter;
import aplicatie.admin.models.Device;

public class DeviceOptionsActivity extends AppCompatActivity {
    private final String TAG = DeviceOptionsActivity.class.getName();
    private static Device selectedDevice;
    private TextView device_ip;
    private TextView device_route;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_options);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        Intent intent = getIntent();
        selectedDevice = (Device)intent.getSerializableExtra("Device");

        device_ip = findViewById(R.id.device_ip);
        device_route = findViewById(R.id.device_route);
        device_ip.setText("IP: " + selectedDevice.getIp());
        device_route.setText("Ruta: " + selectedDevice.getRoute());
    }

    public static Device getSelectedDevice() {
        return selectedDevice;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().hide();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getSupportActionBar().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
