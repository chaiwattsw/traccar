package org.traccar.model;

import org.traccar.storage.StorageName;

@StorageName("truck_types")
public class TruckType {

    private long id;
    private String type;
    private String description;
    private int axleCount;
    private double maxWeight;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAxleCount() {
        return axleCount;
    }

    public void setAxleCount(int axleCount) {
        this.axleCount = axleCount;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    @Override
    public String toString() {
        return "TruckType{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", axleCount=" + axleCount +
                ", maxWeight=" + maxWeight +
                '}';
    }
}