package dto.response;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import dto.excel.SectionData;
import lombok.Data;

import java.util.List;

/**
 * Created by chetan on 23.12.2017.
 */
@Data
@JsonClassDescription("Dto containing list of results processed.")
public class GetResultsResponse {

    @JsonPropertyDescription("Job Id.")
    private Long jobId;

    @JsonPropertyDescription("Job Name.")
    private String jobName;

    @JsonPropertyDescription("List of Section Datas and their respective Geographical Classes.")
    private List<SectionData> sectionDatas;
}
