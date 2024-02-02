package com.codigo.msregister.aggregates.enums;

import lombok.Getter;

@Getter
public enum ETypeDocuments {
    DNI(01),
    RUC(03);

    private final int value;
    ETypeDocuments(int value) {
        this.value = value;
    }
}
