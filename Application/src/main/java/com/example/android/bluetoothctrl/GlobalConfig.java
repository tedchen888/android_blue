package com.example.android.bluetoothctrl;

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


    public static final String CMD_UP = "up";
    public static final String CMD_MOVE_STOP = "move0";
    public static final String CMD_DOWN = "down";
    public static final String CMD_LEFT = "left";
    public static final String CMD_RIGHT = "right";
    public static final String CMD_SPEED = "speed";
    public static final String CMD_STOP = "stop";
    public static final String CMD_LIGHT = "light";
    public static final String CMD_SOUND = "sound";

    public static final String CMD_RELEASE = "release";  //松开按键
    public static final String CMD_CANCLE = "cancle";  //松开按键
}
