package com.turismea.exception;

import com.turismea.model.enumerations.Province;

public class MissingProvinceException extends RuntimeException {
    private final Province province;
    public MissingProvinceException(Province province) {
        super("Province is required but missing. Provided: " + (province != null ? province.name() : "null"));
        this.province = province;
    }

     public Province getProvince(){
        return this.province;
     }

}
