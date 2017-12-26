package dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by chetan on 22.12.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RegisterJobRequest {

    @JsonPropertyDescription("File Name")
    @NotNull
    private String fileName;
}
