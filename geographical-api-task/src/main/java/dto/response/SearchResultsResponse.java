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
@JsonClassDescription("Dto contaning the list of results searched according to name or code or both")
public class SearchResultsResponse {

    @JsonPropertyDescription("Section Data List according to the search query.")
    private List<SectionData> sectionDatas;
}
