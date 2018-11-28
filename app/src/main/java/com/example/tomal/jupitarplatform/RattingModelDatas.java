package com.example.tomal.jupitarplatform;

public class RattingModelDatas {

    private int lead_id;
    private int d_zip;

    public RattingModelDatas(int lead_id, int d_zip) {
        this.lead_id = lead_id;
        this.d_zip = d_zip;
    }

    public RattingModelDatas() {
    }

    public int getLead_id() {
        return lead_id;
    }

    public void setLead_id(int lead_id) {
        this.lead_id = lead_id;
    }

    public int getD_zip() {
        return d_zip;
    }

    public void setD_zip(int d_zip) {
        this.d_zip = d_zip;
    }
}
