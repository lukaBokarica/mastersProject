package hr.fer.masters.project.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class UserEntity {

    @Id
    private String id;
    private String username;

    private String slackUsername;
    private String workingHoursStart;
    private String workingHoursEnd;

    private Map<String, String> weeklySchedule;

    public UserEntity(String username) {
        this.username = username;
        this.workingHoursStart = "09:00";
        this.workingHoursEnd = "17:00";
    }
}
