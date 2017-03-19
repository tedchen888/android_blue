package com.example.android.bluetoothctrl;

import android.widget.Toast;

import com.example.android.bluetoothchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tedchen on 2017/3/8.
 */
public class GlobalConfig {
    private static GlobalConfig ourInstance = new GlobalConfig();

    public static GlobalConfig getInstance() {
        return ourInstance;
    }

    private GlobalConfig() {
        allUUID = new ArrayList<UUID>();
    }

    //
    public UUID blueUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public UUID getBlueUuid() {
        return blueUuid;
    }

    public void setBlueUuid(UUID uuid_str) {
        this.blueUuid = uuid_str;
    }

    List<UUID> allUUID;
    public List<UUID> getAllUUID() {
        return allUUID;
    }

    public void addUUID(UUID uuid) {
        this.allUUID.add(uuid);
    }

    public static final String CMD_SPLIT = "\n";
    public static final String CMD_UP = "up";
    public static final String CMD_DOWN = "down";
    public static final String CMD_LEFT = "left";
    public static final String CMD_RIGHT = "right";
    public static final String CMD_SPEED = "speed";
    public static final String CMD_STOP = "stop";
    public static final String CMD_LIGHT = "light";
    public static final String CMD_SOUND = "sound";

    public static final String CMD_RELEASE = "release";  //松开按键
    public static final String CMD_CANCLE = "cancle";  //松开按键
    public static final String CMD_MOVE_START = "start";
    public static final String CMD_MOVE_STOP = "move0";

    //发送串口命令
    public static void SendBluetoothCmd(String cmd) {
        if (null == AppContexts.getInstance().mChatService) {
            return;
        }
        // Check that we're actually connected before trying anything
        if (AppContexts.getInstance().mChatService.getState() != BluetoothCtrlService.STATE_CONNECTED) {
            //Toast.makeText(getContext(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        //Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

        // Check that there's actually something to send
        if (cmd.length() > 0) {
            cmd += GlobalConfig.CMD_SPLIT;
            // Get the message bytes and tell the BluetoothCtrlService to write
            byte[] send = cmd.getBytes();
            AppContexts.getInstance().mChatService.write(send);
        }
    }
}
