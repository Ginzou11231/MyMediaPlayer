package com.example.mymediaplayer;

enum OptionItemEnum {Base, Media}

class OptionItemBase {
    private String itemString;
    protected OptionItemEnum typeEnum;

    OptionItemBase(String string) {
        itemString = string;
        typeEnum = OptionItemEnum.Base;
    }

    public String getItemString() {
        return itemString;
    }

    public OptionItemEnum getTypeEnum() {
        return typeEnum;
    }

}

class OptionItemMedia extends OptionItemBase {
    private MediaLoopMode playMode;

    public OptionItemMedia(String string, MediaLoopMode playMode) {
        super(string);
        this.playMode = playMode;
        typeEnum = OptionItemEnum.Media;
    }

    public MediaLoopMode getPlayMode() {
        return playMode;
    }
}
