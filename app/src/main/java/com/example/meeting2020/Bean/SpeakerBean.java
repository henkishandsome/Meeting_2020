package com.example.meeting2020.Bean;

public class SpeakerBean {
    private String meeting_theme;
    private String speaker_name;
    private int speaker_SupNum;
    private int speaker_DisNum;
    public SpeakerBean(){
        super();
    }

    public String getMeeting_theme() {
        return meeting_theme;
    }

    public void setMeeting_theme(String meeting_theme) {
        this.meeting_theme = meeting_theme;
    }

    public String getSpeaker_name() {
        return speaker_name;
    }

    public void setSpeaker_name(String speaker_name) {
        this.speaker_name = speaker_name;
    }

    public int getSpeaker_SupNum() {
        return speaker_SupNum;
    }

    public void setSpeaker_SupNum(int speaker_SupNum) {
        this.speaker_SupNum = speaker_SupNum;
    }

    public int getSpeaker_DisNum() {
        return speaker_DisNum;
    }

    public void setSpeaker_DisNum(int speaker_DisNum) {
        this.speaker_DisNum = speaker_DisNum;
    }
}
