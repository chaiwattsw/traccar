package org.traccar.model;

import org.traccar.storage.StorageName;

@StorageName("pay_types")
public class PayType {

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
        return "PayType{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}