package hr.fer.masters.project.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.slack.api.bolt.App;
import com.slack.api.methods.SlackApiException;
import hr.fer.masters.project.models.Action;
import hr.fer.masters.project.models.AppHomeViewCustom;
import hr.fer.masters.project.models.parsers.ActionParser;
import hr.fer.masters.project.service.UserService;
import lombok.AllArgsConstructor;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public void handleButtonClicked(@RequestBody String payload, App app) throws ParseException, IOException, SlackApiException, java.text.ParseException {
        // payload parsing
        String s = URLDecoder.decode(payload, StandardCharsets.UTF_8);
        s = s.substring(8);
        Object obj = new JSONParser().parse(s);
        JSONObject jo = (JSONObject) obj;

        // ActionParser
        ActionParser actionParser =  new ActionParser();
        JSONObject action = actionParser.parseActionList(jo.get("actions"));

        // user parsing
        Object userObject = jo.get("user");
        JSONObject user = (JSONObject) userObject;
        String slackUserId = (String) user.get("id");
        String username = (String) user.get("username");
        String name = (String) user.get("name");
        userService.updateUsername(slackUserId, username);

        System.out.println(action.get("action_id"));
        // action handling
        if (action.get("action_id").equals("actionId-userScheduleSelect")) {
            String selectedUserId = (String) action.get("selected_user");
            AppHomeViewCustom appHomeViewCustom = new AppHomeViewCustom(selectedUserId, userService);

            app.client().viewsPublish(r -> r
                    .userId(selectedUserId)
                    .view(appHomeViewCustom.reloadAppHomeView(true))
            );
        } else if (action.get("action_id").equals("actionId-startTimeChange")) {
            System.out.println("vrijeme start promijenjeno");
        } else if (action.get("action_id").equals("actionId-endTimeChange")) {
            System.out.println("vrijeme end promijenjeno");
        } else if (action.get("action_id").equals("actionId-saveWorkingHours")) {
            // working hours parsing
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

            // action handling
            String workingHoursStartSelected = actionModels.get(0).getSelectedTime();
            String workingHoursEndSelected = actionModels.get(1).getSelectedTime();
            userService.updateWorkingHours(slackUserId, workingHoursStartSelected, workingHoursEndSelected);
            AppHomeViewCustom appHomeViewCustom = new AppHomeViewCustom(slackUserId, userService);

            app.client().viewsPublish(r -> r
                    .userId(slackUserId)
                    .view(appHomeViewCustom.reloadAppHomeView(false))
            );
        }
    }
}
