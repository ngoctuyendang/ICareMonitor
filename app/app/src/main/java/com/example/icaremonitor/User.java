package com.example.icaremonitor;

import java.io.Serializable;

public class User implements Serializable {
    private Integer Id;
    private String Value;
    private String Time;
    private String Note;


    public User( Integer id, String value, String time, String note){
        this.setId(id);
        this.setValue(value);
        this.setTime(time);
        this.setNote(note);


    }
    public Integer getId() {
        return Id;
    }

    private void setId(Integer id) {Id = id; }

    public String getValue() { return Value; }

    public void setValue(String value) {
        Value = value;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

}