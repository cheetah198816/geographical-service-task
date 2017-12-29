package dto.response;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by chetan on 22.12.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@JsonClassDescription("Dto containing the job Id of the registered job.")
public class RegisterJobResponse {

    @JsonPropertyDescription("Job Id")
    private Long jobId;
}
