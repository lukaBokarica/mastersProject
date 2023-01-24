package hr.fer.masters.project;

import com.slack.api.bolt.App;
import com.slack.api.methods.request.users.profile.UsersProfileGetRequest;
import com.slack.api.methods.response.views.ViewsPublishResponse;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.view.View;
import hr.fer.masters.project.domain.entities.UserEntity;
import hr.fer.masters.project.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.Blocks.actions;
import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.view.Views.*;
import com.slack.api.model.event.AppHomeOpenedEvent;
import org.springframework.web.reactive.function.client.WebClient;

import static com.slack.api.model.block.element.BlockElements.*;

@AllArgsConstructor
@Configuration
public class SlackApp {

    private UserService userService;

    @Bean
    public App initSlackApp() {
        App app = new App();
        // SLASH COMMANDS
        app.command("/hello", (req, ctx) -> ctx.ack("Hi there!"));

        // AppHomeOpenedEvent
        app.event(AppHomeOpenedEvent.class, (payload, ctx) -> {

            // String email = ctx.client().usersProfileGet(UsersProfileGetRequest.builder().build()).getProfile().getEmail();
            // System.out.println(email);

            String userId = payload.getEvent().getUser();
            userService.create(userId);
            UserEntity user = userService.getUserBySlackUserId(userId);
            View appHomeView = view(view -> view
                    .type("home")
                    .blocks(asBlocks(
                            header(h -> {
                                        PlainTextObject textObject = new PlainTextObject();
                                        textObject.setText("Welcome back!");
                                        h.text(textObject);
                                        return h;
                            }),
                            divider(),
                            actions(actions -> actions
                                    .elements(asElements(
                                            timePicker(tp -> tp.initialTime(user.getWorkingHoursStart()).placeholder(plainText(pt -> pt.text("Select time").emoji(true))).actionId("actionId-startTimeChange")),
                                            timePicker(tp -> tp.initialTime(user.getWorkingHoursEnd()).placeholder(plainText(pt -> pt.text("Select time").emoji(true))).actionId("actionId-endTimeChange")),
                                            button(b -> b.text(plainText(pt -> pt.text("Set working hours").emoji(true))).value("click_me_123").actionId("actionId-saveWorkingHours"))
                                    ))
                            ),
                            divider(),
                            header(h -> {
                                PlainTextObject textObject = new PlainTextObject();
                                textObject.setText("Choose whose schedule You want to see (use \"Select user\" menu below)");
                                h.text(textObject);
                                return h;
                            }),
                            actions(actions -> actions
                                    .elements(asElements(
                                            usersSelect(us -> us.placeholder(plainText(pt -> pt.text("Select user").emoji(true))).actionId("actionId-userScheduleSelect"))
                                    ))
                            )
                    )));

            ViewsPublishResponse res = ctx.client().viewsPublish(r -> r
                    .userId(payload.getEvent().getUser())
                    .view(appHomeView)
            );

            return ctx.ack();
        });

        return app;
    }
}