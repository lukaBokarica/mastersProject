package hr.fer.masters.project.controllers;

import lombok.AllArgsConstructor;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.*;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/", method = RequestMethod.POST, consumes="application/x-www-form-urlencoded")
public class InteractionActionsController {

    @PostMapping("/actions")
    public void handleButtonClicked(@RequestBody String payload) throws ParseException, UnsupportedEncodingException {
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
        String username = (String) user.get("username");

        // HANDLING ACTIONS
        if (action.get("action_id").equals("actionId-0")) {
            System.out.println("vrijeme start promijenjeno");
        } else if (action.get("action_id").equals("actionId-1")) {
            System.out.println("vrijeme end promijenjeno");
        } else if (action.get("action_id").equals("actionId-2")) {
            System.out.println("gumb stisnut");
        }
    }
}
