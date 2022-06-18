package client.docker.commands;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateContainerResponse {
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Warnings")
    private String[] warnings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getWarnings() {
        return warnings;
    }

    public void setWarnings(String[] warnings) {
        this.warnings = warnings;
    }
}
