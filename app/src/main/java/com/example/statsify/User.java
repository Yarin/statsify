package com.example.statsify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String displayName;
    private String Id;
    private Map<String, String[]> stats;

    public User(String displayName, String Id, HashMap<String, String[]> stats) {
        this.displayName = displayName;
        this.Id = Id;
        this.stats = stats;
    }

    public String getDisplayName() {

        return displayName;
    }

    public String getId() {

        return Id;
    }

    public void setId(String id) {

        this.Id = id;
    }
   public void setDisplayName(String displayName) {

        this.displayName = displayName;
    }

    public void setMyDict(Map<String, String[]> myDict) {

        this.stats = myDict;
    }

    public String[] getValuesForKey(String key) {

        return stats.get(key);
    }

    public void addValueForKey(String key, String value) {
        String[] values = stats.getOrDefault(key, new String[0]);
        String[] newValues = Arrays.copyOf(values, values.length + 1);
        newValues[values.length] = value;
        stats.put(key, newValues);
    }

    public void removeValueForKey(String key, String value) {
        String[] values = stats.get(key);
        if (values == null) {
            return;
        }
        List<String> list = new ArrayList<>(Arrays.asList(values));
        list.remove(value);
        stats.put(key, list.toArray(new String[0]));
    }

}
