package com.uofc.acmeplex.enums;

import lombok.Getter;

@Getter
public enum MessageSubTypeEnum {
    WELCOME("welcome.flth", "images/acmeplex-logo.png"),
    BASIC_EMAIL("welcome.flth", "images/acmeplex-logo.png"),
    ;

    private String templateName;
    private String actionImgName;
    MessageSubTypeEnum(String templateName, String actionImgName){

        this.templateName= templateName;
        this.actionImgName = actionImgName;
    }



}
