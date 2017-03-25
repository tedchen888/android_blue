package com.example.android.bluetoothctrl;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothchat.R;
import com.example.android.common.VibratorUtil;
import com.example.android.common.logger.Log;
import com.example.android.game.GameActivity;

import java.util.UUID;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothCtrlFragment extends Fragment {

    private static final String TAG = "BluetoothCtrlFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int REQUEST_CHOOSE_UUID = 4;
    private static final int REQUEST_ROCKER_CTRL = 5;
    private static final int REQUEST_PLAY_GAME = 6;

    // Layout Views
    //private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    private ImageButton mBtnRocker;
    private ImageButton mBtnGamer;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (AppContexts.getInstance().mChatService== null) {
            setupChat();
            setupControler();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (AppContexts.getInstance().mChatService != null) {
            AppContexts.getInstance().mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (AppContexts.getInstance().mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (AppContexts.getInstance().mChatService.getState() == BluetoothCtrlService.STATE_NONE) {
                // Start the Bluetooth chat services
                AppContexts.getInstance().mChatService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_ctrl, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //mConversationView = (ListView) view.findViewById(R.id.in);
        mOutEditText = (EditText) view.findViewById(R.id.edit_text_out);
        mSendButton = (Button) view.findViewById(R.id.button_send);
        mOutEditText.setEnabled(false);
        mSendButton.requestFocus();


        mBtnRocker = (ImageButton) view.findViewById(R.id.button_rocker);
        mBtnGamer = (ImageButton) view.findViewById(R.id.button_game);

    }

    //控制按键事件类
    public class ControlButtonOnClickListener implements /*View.OnClickListener, */View.OnTouchListener {
        private String m_cmd;
        private String m_cmd_over;

        public ControlButtonOnClickListener(String cmd, String cmd_over) {
            // keep references for your onClick logic
            m_cmd = cmd;
            m_cmd_over = cmd_over;
        }
        /*
        @Override public void onClick(View v) {
            Toast.makeText(getActivity(), "on click:" + m_message, Toast.LENGTH_SHORT).show();
            VibratorUtil.Vibrate(getActivity(), 100);
            GlobalConfig.SendBluetoothCmd(m_message);
        }*/

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:{
                    //Toast.makeText(getActivity(), "on touch down:" + m_cmd, Toast.LENGTH_SHORT).show();
                    VibratorUtil.Vibrate(getActivity(), 100);
                    GlobalConfig.SendBluetoothCmd(m_cmd);
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    //Toast.makeText(getActivity(), "on touch up:" + m_cmd_over, Toast.LENGTH_SHORT).show();
                    //VibratorUtil.Vibrate(getActivity(), 50);
                    GlobalConfig.SendBluetoothCmd(m_cmd_over);
                    break;
                }
                case MotionEvent.ACTION_CANCEL:{
                    //Toast.makeText(getActivity(), "on touch cancle:" + m_message, Toast.LENGTH_SHORT).show();
                    //VibratorUtil.Vibrate(getActivity(), 50);
                    GlobalConfig.SendBluetoothCmd(m_cmd_over);
                    break;
                }
                default:
                    break;
            }
            return false;
        }
    }

    private void setupControler() {
        mBtnRocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开虚拟方向键
                Intent serverIntent = new Intent(getActivity(), VirtualRockerCtrlActivity.class);
                startActivityForResult(serverIntent, REQUEST_ROCKER_CTRL);
            }
        });

        mBtnGamer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开虚拟方向键
                Intent serverIntent = new Intent(getActivity(), GameActivity.class);
                startActivityForResult(serverIntent, REQUEST_PLAY_GAME);
            }
        });
        //mBtnUp.setOnTouchListener(new ControlButtonOnClickListener(GlobalConfig.CMD_UP, GlobalConfig.CMD_MOVE_STOP));
    }
    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);

        //mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                View view = getView();
                if (null != view) {
                    TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
                    String message = textView.getText().toString();
                    GlobalConfig.SendBluetoothCmd(message);
                }
            }
        });

        // Initialize the BluetoothCtrlService to perform bluetooth connections
        AppContexts.getInstance().mChatService = new BluetoothCtrlService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                GlobalConfig.SendBluetoothCmd(message);
            }
            return true;
        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothCtrlService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothCtrlService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothCtrlService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothCtrlService.STATE_LISTEN:
                        case BluetoothCtrlService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            case REQUEST_CHOOSE_UUID: {
                if (resultCode == Activity.RESULT_OK) {
                    //change cur UUID
                    changeUUID(data);
                } else {

                }
                break;
            }
        }
    }

    private void changeUUID(Intent data) {
        String uuid_str = data.getExtras()
                .getString(UUIDListActivity.EXTRA_UUID_STR);
        String [] tmp = null;
        tmp = uuid_str.split(",");
        if (tmp.length == 2) {
            try {
                String str = tmp[1].trim();
                Toast.makeText(getActivity(), "choose uuid:" + str, Toast.LENGTH_LONG).show();
                UUID uuid = UUID.fromString(str);
                GlobalConfig.getInstance().setBlueUuid(uuid);
                //重启服务
                AppContexts.getInstance().mChatService.stop();
                AppContexts.getInstance().mChatService.start();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "invalid uuid:" + tmp[1], Toast.LENGTH_LONG).show();
            }
        }
    }
    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        Toast.makeText(getActivity(), "device address is:" + address, Toast.LENGTH_LONG).show();
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        AppContexts.getInstance().mChatService.connect(device, secure);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_ctrl, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
            case R.id.change_uuid: {
                // Launch the UUID Activity to see uuid and choose one
                Intent serverIntent = new Intent(getActivity(), UUIDListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CHOOSE_UUID);
                return true;
            }
            case R.id.rocker_ctrl: {
                // 打开虚拟方向键
                Intent serverIntent = new Intent(getActivity(), VirtualRockerCtrlActivity.class);
                startActivityForResult(serverIntent, REQUEST_ROCKER_CTRL);
                return true;
            }
        }
        return false;
    }

}
