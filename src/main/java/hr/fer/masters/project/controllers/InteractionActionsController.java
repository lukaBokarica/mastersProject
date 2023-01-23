package hr.fer.masters.project.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import hr.fer.masters.project.models.Action;
import hr.fer.masters.project.service.UserService;
import lombok.AllArgsConstructor;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.*;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/", method = RequestMethod.POST, consumes="application/x-www-form-urlencoded")
public class InteractionActionsController {

    private UserService userService;

    @PostMapping("/actions")
    public void handleButtonClicked(@RequestBody String payload) throws ParseException, UnsupportedEncodingException, JsonProcessingException {
        // JSON PARSE
        String s = URLDecoder.decode(payload, StandardCharsets.UTF_8);
        s = s.substring(8);
        Object obj = new JSONParser().parse(s);
        JSONObject jo = (JSONObject) obj;

        // ACTION
        Object actions = jo.get("actions");
        List<Object> actionsList = (List<Object>) actions;
        JSONObject action = (JSONObject) actionsList.get(0);

        // USER
        Object userObject = jo.get("user");
        JSONObject user = (JSONObject) userObject;
        String username = (String) user.get("id");

        // WORKING HOURS
        JSONObject currentView = (JSONObject) jo.get("view");
        JSONObject currentState = (JSONObject) currentView.get("state");
        JSONObject values = (JSONObject) currentState.get("values");
        String blockId = (String) action.get("block_id");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(String.valueOf(values));
        JsonNode valuesNode = jsonNode.get(blockId);
        List<Action> actionModels = new ArrayList<>();
        for (Iterator<Map.Entry<String, JsonNode>> it = valuesNode.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            JsonNode actionNode = entry.getValue();
            String actionId = entry.getKey();
            String type = actionNode.get("type").asText();
            String selectedTime = actionNode.get("selected_time").asText();
            actionModels.add(new Action(actionId, type, selectedTime));
        }

        // HANDLING ACTIONS
        if (action.get("action_id").equals("actionId-0")) {
            System.out.println("vrijeme start promijenjeno");
        } else if (action.get("action_id").equals("actionId-1")) {
            System.out.println("vrijeme end promijenjeno");
        } else if (action.get("action_id").equals("actionId-2")) {
            String workingHoursStartSelected = actionModels.get(0).getSelectedTime();
            userService.updateWorkingHoursStart(username, workingHoursStartSelected);
            String workingHoursEndSelected = actionModels.get(1).getSelectedTime();
            userService.updateWorkingHoursEnd(username,  workingHoursEndSelected);
        }
    }
}
