package co.edu.uco.ucochallenge.crosscutting.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDTO {

    @JsonProperty("code")
    private String code;

    // Soporta: technicalMessage (service) y technical (record anterior)
    @JsonProperty("technicalMessage")
    @JsonAlias({"technical"})
    private String technicalMessage;

    // Soporta: userMessage (service), spanish (record anterior), message/msg (fallback)
    @JsonProperty("userMessage")
    @JsonAlias({"spanish", "message", "msg"})
    private String userMessage;

    // Soporta: generalMessage (service) y english/general (record anterior)
    @JsonProperty("generalMessage")
    @JsonAlias({"english", "general"})
    private String generalMessage;

    // ===== Jackson-friendly =====
    public MessageDTO() { }

    // (Opcional) por si en algún punto lo construyes manualmente
    public MessageDTO(String code, String technicalMessage, String userMessage, String generalMessage) {
        this.code = code;
        this.technicalMessage = technicalMessage;
        this.userMessage = userMessage;
        this.generalMessage = generalMessage;
    }

    // ===== Getters / Setters =====
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTechnicalMessage() { return technicalMessage; }
    public void setTechnicalMessage(String technicalMessage) { this.technicalMessage = technicalMessage; }

    public String getUserMessage() { return userMessage; }
    public void setUserMessage(String userMessage) { this.userMessage = userMessage; }

    public String getGeneralMessage() { return generalMessage; }
    public void setGeneralMessage(String generalMessage) { this.generalMessage = generalMessage; }

    /** Preferencia para UI: español → general/inglés → técnico → code */
    public String getUserMessageResolved() {
        if (isNotBlank(userMessage)) return userMessage;
        if (isNotBlank(generalMessage)) return generalMessage;
        if (isNotBlank(technicalMessage)) return technicalMessage;
        return (code != null) ? code : "Error";
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }
}
