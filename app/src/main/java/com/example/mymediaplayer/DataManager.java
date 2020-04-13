package com.example.mymediaplayer;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class DataManager {

    private List<FolderData> allMediaFolderList;
    private FolderData currentFolder;

    private static DataManager dataManager;
    public synchronized static DataManager instance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    public List<FolderData> getAllMediaFolderList() {
        return allMediaFolderList;
    }

    public void setAllMediaFolderList(List<FolderData> allMediaFolderList) {
        this.allMediaFolderList = allMediaFolderList;
    }

    public FolderData getCurrentFolder() {
        return currentFolder;
    }

    public void setCurrentFolder(FolderData currentFolder) {
        this.currentFolder = currentFolder;
    }

    public String timeToString(int time) {
        int sec = time / 1000 % 60;
        int min = time / 1000 / 60 % 60;
        int hour = time / 1000 / 60 / 60;

        String str = "";

        if (hour < 1) {
            str = stringFormart(min) + ":" + stringFormart(sec);
        } else if (hour < 1 && min < 1) {
            str = "00 : " + stringFormart(sec);
        } else if (hour < 1 && min < 1 && sec < 1) {
            str = "00 : 00";
        } else {
            str = hour + " : " + stringFormart(min) + ":" + stringFormart(sec);
        }

        return str;
    }

    private String stringFormart(int time) {
        String str = "";
        if (time < 10) {
            str = "0" + time;
        } else {
            str = String.valueOf(time);
        }
        return str;
    }
}
