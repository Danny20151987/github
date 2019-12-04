package com.study.springframework.example07;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/24 9:02 AM
 */
public class DatePropertyEditor extends PropertyEditorSupport {

    private String format = "yyyyMMdd";

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        System.out.println("text:"+text);

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try{
            Date d = sdf.parse(text);
            this.setValue(d);
        }catch (Exception e){

        }
    }


}
