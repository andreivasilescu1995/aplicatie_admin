package device_options_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

import com.google.android.material.tabs.TabLayout;

import misc_objects.Device;
import aplicatie.admin.R;

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
        setTitle("IP:" + selectedDevice.getIp() + " Ruta:" + selectedDevice.getRoute());
    }

    public static Device getSelectedDevice() {
        return selectedDevice;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DeviceOptionsFragment.flag_status = false;
        LocationFragment.flag_locatie = false;
        this.finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        DeviceOptionsFragment.flag_status = false;
        LocationFragment.flag_locatie = false;
        this.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        DeviceOptionsFragment.flag_status = true;
        LocationFragment.flag_locatie = true;
    }
}
