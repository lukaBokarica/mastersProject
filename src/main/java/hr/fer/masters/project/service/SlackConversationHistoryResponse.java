package hr.fer.masters.project.service;

import java.util.List;

public class SlackConversationHistoryResponse {
    private List<Message> messages;
    // getters and setters
    public class Message {
        private String user;
        private long ts;

        public Object getUser() {
            return user;
        }

        public Long getTs() {
            return ts;
        }
        // getters and setters
    }

    public List<Message> getMessages() {
        return messages;
    }
}
