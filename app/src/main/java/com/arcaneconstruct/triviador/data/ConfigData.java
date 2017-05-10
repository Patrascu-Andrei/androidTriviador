package com.arcaneconstruct.triviador.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by Archangel on 4/14/2016.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility =
        JsonAutoDetect.Visibility.NONE)
public class ConfigData {
    @JsonProperty("name")
    private String name;
    @JsonProperty("no_levels")
    private int noLevels;//number of levels
    @JsonProperty("levels")
    private ArrayList<LevelData> levels;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNoLevels() {
        return noLevels;
    }

    public void setNoLevels(int noLevels) {
        this.noLevels = noLevels;
    }

    public ArrayList<LevelData> getLevels() {
        return levels;
    }

    public void setLevels(ArrayList<LevelData> levels) {
        this.levels = levels;
    }
}