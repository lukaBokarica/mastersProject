package hr.fer.masters.project.models;

import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.view.View;
import java.util.ArrayList;
import java.util.List;
import com.slack.api.model.block.ContextBlockElement;
import com.slack.api.model.block.composition.PlainTextObject;
import hr.fer.masters.project.domain.entities.UserEntity;
import hr.fer.masters.project.service.UserService;
import lombok.AllArgsConstructor;
import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.Blocks.actions;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.*;
import static com.slack.api.model.view.Views.*;

@AllArgsConstructor
public class AppHomeViewCustom {

    private UserEntity user;

    private String username;

    private UserService userService;

    public AppHomeViewCustom(String slackUserId, UserService userService) {
        this.userService = userService;
        this.user = userService.getUserBySlackUserId(slackUserId);
        this.username = user.getSlackUsername();
    }

    public List<LayoutBlock> createBlockElements(Boolean scheduleShown) {
        List<LayoutBlock> blockElementsList = new ArrayList<>();
        blockElementsList.clear();
        blockElementsList.add(header(h -> {
            PlainTextObject textObject = new PlainTextObject();
            textObject.setText("Welcome back!");
            h.text(textObject);
            return h;
        }));
        blockElementsList.add(divider());
        blockElementsList.add(actions(actions -> actions
                .elements(asElements(
                        timePicker(tp -> tp.initialTime(user.getWorkingHoursStart()).placeholder(plainText(pt -> pt.text("Select time").emoji(true))).actionId("actionId-startTimeChange")),
                        timePicker(tp -> tp.initialTime(user.getWorkingHoursEnd()).placeholder(plainText(pt -> pt.text("Select time").emoji(true))).actionId("actionId-endTimeChange")),
                        button(b -> b.text(plainText(pt -> pt.text("Set working hours").emoji(true))).value("click_me_123").actionId("actionId-saveWorkingHours"))
                ))
        ));
        blockElementsList.add(divider());
        blockElementsList.add(header(h -> {
            PlainTextObject textObject = new PlainTextObject();
            textObject.setText("Choose whose schedule You want to see (use \"Select user\" menu below)");
            h.text(textObject);
            return h;
        }));
        blockElementsList.add(actions(actions -> actions
                .elements(asElements(
                        usersSelect(us -> {
                            us.placeholder(plainText(pt -> pt.text("Select user").emoji(true))).actionId("actionId-userScheduleSelect");
                            return us;
                        })
                ))
        ));
        if (scheduleShown) {
            blockElementsList.add(header(h -> {
                PlainTextObject textObject = new PlainTextObject();
                textObject.setEmoji(true);
                textObject.setText(username + "'s schedule for the day");
                h.text(textObject);
                return h;
            }));

            blockElementsList.add(header(h -> {
                PlainTextObject textObject = new PlainTextObject();
                textObject.setText("Their working hours start at: " + user.getWorkingHoursStart() + "h.");
                h.text(textObject);
                return h;
            }));

            blockElementsList.add(header(h -> {
                PlainTextObject textObject = new PlainTextObject();
                textObject.setText("Their working hours end at: " + user.getWorkingHoursEnd() + "h.");
                h.text(textObject);
                return h;
            }));
        }
        return blockElementsList;
    }

    public View reloadAppHomeView(Boolean scheduleShown) {
        View appHomeView = view(view -> view
                .type("home")
                .blocks(createBlockElements(scheduleShown)));
        return appHomeView;
    }
}
