package com.gradlic.fts.erp.enumeration;

public enum FirmType {
    PRIVATE_FINANCE("PRIVATE_FINANCE"),
    HARDWARE("HARDWARE"),
    CLOTH_SHOP("CLOTH_SHOP"),
    GROCERY_STORE("GROCERY_STORE"),
    GENERAL_STORE("GENERAL_STORE"),
    SWEETS_SNACKS("SWEETS_SNACKS"),
    PIZZA_STORE("PIZZA_STORE"),
    SHOE_STORE("SHOE_STORE"),
    OTHER_STORE("OTHER_STORE"),
    SERVICE_PROVIDER("SERVICE_PROVIDER");

    private final String type;

    FirmType(String type) {
        this.type = type;
    }

    public String getType(){
        return this.type;
    }
}
