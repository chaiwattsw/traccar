package org.traccar.model;

import org.traccar.storage.StorageName;

@StorageName("cells")
public class Cell {

    private String code;
    private String desc;
    private String descEn;

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

    public String getDescEn() {
        return descEn;
    }

    public void setDescEn(String descEn) {
        this.descEn = descEn;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                ", descEn='" + descEn + '\'' +
                '}';
    }
}