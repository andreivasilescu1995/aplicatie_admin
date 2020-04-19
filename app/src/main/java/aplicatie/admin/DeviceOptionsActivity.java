package aplicatie.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import aplicatie.admin.ui.device_options.SectionsPagerAdapter;
import aplicatie.admin.misc_objects.Device;

public class DeviceOptionsActivity extends AppCompatActivity {
    private final String TAG = DeviceOptionsActivity.class.getName();
    private static Device selectedDevice;
    
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
//        setTitle("IP:" + selectedDevice.getIp() + " Ruta:" + selectedDevice.getRoute());
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
