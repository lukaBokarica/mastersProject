package hr.fer.masters.project.models.parsers;

import org.json.simple.JSONObject;

import java.util.List;

public class ActionParser {

    public ActionParser() {}

    public JSONObject parseActionList(Object actions) {
        List<Object> actionsList =  (List<Object>) actions;
        return (JSONObject) actionsList.get(0);
    }
}
