package com.example.tomal.jupitarplatform;

public class DashboardDataProfile {
    private String Website;
    private String Name;

    public String getWebsite() {
        return Website;
    }

    public void setWebsite(String website) {
        Website = website;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public DashboardDataProfile(String website, String name) {

        Website = website;
        Name = name;
    }

    public DashboardDataProfile() {

    }
}
