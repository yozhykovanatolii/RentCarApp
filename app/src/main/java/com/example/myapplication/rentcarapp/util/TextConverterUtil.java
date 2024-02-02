package com.example.myapplication.rentcarapp.util;

public class TextConverterUtil {
    private String text;

    public String getText(String oldText) {
        if(oldText.contains("\n")){
            int index = oldText.indexOf('\n');
            text = oldText.substring(0, index);
        }else{
            text = oldText;
        }
        return text;
    }
}
