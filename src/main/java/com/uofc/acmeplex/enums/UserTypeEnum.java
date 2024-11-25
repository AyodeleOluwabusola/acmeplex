package com.uofc.acmeplex.enums;

import lombok.Getter;

@Getter
public enum UserTypeEnum {
    REGISTERED_USER("RU"), ORDINARY_USER("OU");

    private final String name;

    UserTypeEnum(String name) {
        this.name = name;
    }
}

