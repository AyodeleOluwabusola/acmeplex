package com.uofc.acmeplex.enums;

public enum CardTypeEnum {

    CREDIT_CARD("CREDIT"),
    DEBIT_CARD("DEBIT");

    private final String type;

    CardTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}