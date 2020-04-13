package com.example.mymediaplayer;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderData {

    private String folderName, totalTime , totalSong ;
    private File folderPath;
    private List<MediaData> mediaDatas = new ArrayList<>();

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalSong() {
        return totalSong;
    }

    public void setTotalSong(String totalSong) {
        this.totalSong = totalSong;
    }

    public File getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(File folderPath) {
        this.folderPath = folderPath;
    }

    public List<MediaData> getMediaDatas() {
        return mediaDatas;
    }

    public void setMediaDatas(List<MediaData> mediaDatas) {
        this.mediaDatas = mediaDatas;
    }
}
