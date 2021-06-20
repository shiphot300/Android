package com.example.mainpage;

public class MainList {
    private  String Name;
    private  String text;
    private  String list;

    public MainList(String name, String text, String list) {
        Name = name;
        this.text = text;
        this.list = list;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "List{" +
                "Name='" + Name + '\'' +
                ", text='" + text + '\'' +
                ", list='" + list + '\'' +
                '}';
    }
}
