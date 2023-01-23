package hr.fer.masters.project;

import com.slack.api.bolt.App;
import com.slack.api.model.block.element.TimePickerElement;
import hr.fer.masters.project.domain.repositories.UserRepository;
import hr.fer.masters.project.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.view.Views.*;
import com.slack.api.model.event.AppHomeOpenedEvent;
import static com.slack.api.model.block.element.BlockElements.*;

@Configuration
public class SlackApp {
    @Bean
    public App initSlackApp() {
        App app = new App();
        app.command("/hello", (req, ctx) -> ctx.ack("Hi there!"));

        app.event(AppHomeOpenedEvent.class, (payload, ctx) -> {
            var appHomeView = view(view -> view
                    .type("home")
                    .blocks(asBlocks(
                            divider(),
                            actions(actions -> actions
                                    .elements(asElements(
                                            timePicker(tp -> tp.initialTime("09:0").placeholder(plainText(pt -> pt.text("Select time").emoji(true))).actionId("actionId-0")),
                                            timePicker(tp -> tp.initialTime("17:00").placeholder(plainText(pt -> pt.text("Select time").emoji(true))).actionId("actionId-1")),
                                            button(b -> b.text(plainText(pt -> pt.text("Set working hours").emoji(true))).value("click_me_123").actionId("actionId-2"))
                                    ))
                            )
                    )));

            var res = ctx.client().viewsPublish(r -> r
                    .userId(payload.getEvent().getUser())
                    .view(appHomeView)
            );

            return ctx.ack();
        });

        return app;
    }
}