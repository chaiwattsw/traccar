package org.traccar.model;

import org.traccar.storage.StorageName;

@StorageName("car_register_types")
public class CarRegisterType {

    private String code;
    private String desc;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "CarRegisterType{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}