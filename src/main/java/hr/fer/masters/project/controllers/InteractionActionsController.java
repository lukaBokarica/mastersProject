package hr.fer.masters.project.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.slack.api.Slack;
import com.slack.api.bolt.App;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
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
                    .userId(slackUserId)
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

            Slack slack = Slack.getInstance();
            // Load an env variable
            // If the token is a bot token, it starts with `xoxb-` while if it's a user token, it starts with `xoxp-`
            String botToken = "xoxb-4211602315858-4462905584498-Hm3wXGcfArRNE19WLktJXoP8";

            // Initialize an API Methods client with the given token
            MethodsClient methods = slack.methods(botToken);

            // Build a request object
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel("#daily") // Use a channel ID `C1234567` is preferable
                    .text(":clock1: Hey, " + username + " just updated his/hers working hours!")
                    .build();

            // Get a response as a Java object
            ChatPostMessageResponse response = methods.chatPostMessage(request);
        }
    }
}
