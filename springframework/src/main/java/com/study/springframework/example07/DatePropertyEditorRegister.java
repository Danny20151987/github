package com.study.springframework.example07;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.propertyeditors.CustomDateEditor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hudejian
 * @DESC
 * @date 2019/10/24 9:17 AM
 */
public class DatePropertyEditorRegister implements PropertyEditorRegistrar {

    @Override
    public void registerCustomEditors(PropertyEditorRegistry registry) {
        registry.registerCustomEditor(Date.class,new CustomDateEditor(new SimpleDateFormat("yyyyMMdd"),true));
    }
}
