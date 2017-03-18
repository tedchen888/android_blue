package com.example.android.bluetoothctrl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.bluetoothchat.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class UUIDListActivity extends Activity {

    /**
     * Tag for Log
     */
    private static final String TAG = "UUIDListActivity";

    /**
     * Return Intent extra
     */
    public static String EXTRA_UUID_STR = "uuid_str";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.uuid_list);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        ArrayAdapter<String> uuidArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.uuid_name, getUuidFromFile());

        // Find and set up the ListView for paired devices
        ListView uuidListView = (ListView) findViewById(R.id.listview_uuids);
        uuidListView.setAdapter(uuidArrayAdapter);
        uuidListView.setOnItemClickListener(mUUIDClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * The on-click listener for all devices in the ListViews
     */
    private AdapterView.OnItemClickListener mUUIDClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_UUID_STR, info);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    private List<String> getUuidFromFile() {
        String fileName = "bluetooth_uuid"; //文件名字
        List<String> data = new ArrayList<String>();
        try{
            //得到资源中的asset数据流
            InputStream in = getResources().getAssets().open(fileName);
            InputStreamReader inputreader = new InputStreamReader(in);
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line;
            //分行读取
            while (( line = buffreader.readLine()) != null) {
                String [] tmp = null;
                tmp = line.split(",");
                if (tmp.length <2) {
                    continue;
                }
                data.add(line);

                String str = tmp[1].trim();
                UUID uuid = UUID.fromString(str);
                GlobalConfig.getInstance().addUUID(uuid);
            }
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return data;
    }
}
