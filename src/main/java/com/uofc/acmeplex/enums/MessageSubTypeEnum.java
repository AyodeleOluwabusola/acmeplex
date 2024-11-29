package com.uofc.acmeplex.enums;

import lombok.Getter;

@Getter
public enum MessageSubTypeEnum {
    WELCOME("movie-alert.flth", "images/acmeplex-logo.png"),
    NEW_MOVIE_ALERT("movie-alert.flth", "images/acmeplex-logo.png"),
    TICKER_PURCHASE("ticket-purchase.flth", "images/acmeplex-logo.png"),
    PAYMENT_CONFIRMATION("receipt.flth", "images/acmeplex-logo.png"),
    ;

    private String templateName;
    private String actionImgName;
    MessageSubTypeEnum(String templateName, String actionImgName){

        this.templateName= templateName;
        this.actionImgName = actionImgName;
    }



}
