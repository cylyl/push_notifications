package com.github.push.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Tab extends Doc {

    @JsonProperty("order")
    private int order;

    @JsonProperty("last_updated")
    private long last_updated;

    @JsonProperty("tabs")
    private List<Tab> tabs;

    public static boolean consist(List<Tab> tabs, String tabUuid) {

        for (Tab tab : tabs) {
            if (tab.getUuid().equals(tabUuid)) {
                return true;
            }
        }
        return false;
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    @JsonProperty("level")
    private long level;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public long getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(long last_updated) {
        this.last_updated = last_updated;
    }

    public void setTabs(List<Tab> tabs) {
        this.tabs = tabs;
    }

    public List<Tab> getTabs() {
        return tabs;
    }
}