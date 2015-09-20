package com.vermeersch.mycave.model;

import org.json.JSONObject;

/**
 * Created by Gert on 20/09/2015.
 */
public class LightingStates {
    private boolean twilights;
    private boolean standing_lamp;
    private boolean uplighter;
    private boolean desk_light;

    public boolean isTwilights() {
        return twilights;
    }

    public void setTwilights(boolean twilights) {
        this.twilights = twilights;
    }

    public boolean isStanding_lamp() {
        return standing_lamp;
    }

    public void setStanding_lamp(boolean standing_lamp) {
        this.standing_lamp = standing_lamp;
    }

    public boolean isUplighter() {
        return uplighter;
    }

    public void setUplighter(boolean uplighter) {
        this.uplighter = uplighter;
    }

    public boolean isDesk_light() {
        return desk_light;
    }

    public void setDesk_light(boolean desk_light) {
        this.desk_light = desk_light;
    }

    public void setStatesByJson(JSONObject states) {

    }
}
