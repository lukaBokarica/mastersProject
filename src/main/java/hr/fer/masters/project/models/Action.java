package hr.fer.masters.project.models;

import java.util.List;

public class Action {
    private String actionId;
    private String type;
    private String selectedTime;

    public Action(String actionId, String type, String selectedTime) {
        this.actionId = actionId;
        this.type = type;
        this.selectedTime = selectedTime;
    }

    public String getActionId() {
        return actionId;
    }

    public String getType() {
        return type;
    }

    public String getSelectedTime() {
        return selectedTime;
    }
}
