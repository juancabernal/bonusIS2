package co.edu.uco.messageservice.catalog;

import java.util.Objects;

/**
 * Represents a message entry in the {@link MessageCatalog}. Each message keeps three
 * different representations so that the same code can be used for technical logging,
 * final user display and general contextualization purposes.
 */
public class Message {

    private String code;
    private String technicalMessage;
    private String userMessage;
    private String generalMessage;

    public Message(String code, String technicalMessage, String userMessage, String generalMessage) {
        this.code = Objects.requireNonNull(code, "code");
        this.technicalMessage = Objects.requireNonNull(technicalMessage, "technicalMessage");
        this.userMessage = Objects.requireNonNull(userMessage, "userMessage");
        this.generalMessage = Objects.requireNonNull(generalMessage, "generalMessage");
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = Objects.requireNonNull(code, "code");
    }

    public String getTechnicalMessage() {
        return technicalMessage;
    }

    public void setTechnicalMessage(String technicalMessage) {
        this.technicalMessage = Objects.requireNonNull(technicalMessage, "technicalMessage");
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = Objects.requireNonNull(userMessage, "userMessage");
    }

    public String getGeneralMessage() {
        return generalMessage;
    }

    public void setGeneralMessage(String generalMessage) {
        this.generalMessage = Objects.requireNonNull(generalMessage, "generalMessage");
    }
}
