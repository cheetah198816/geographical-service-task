package services.impl;

import config.FileConfiguration;
import dto.excel.SectionData;
import dto.request.RegisterJobRequest;
import dto.response.GetResultsResponse;
import dto.response.RegisterJobResponse;
import dto.response.SearchResultsResponse;
import mappers.JobEntityMapper;
import mappers.SectionEntityMapper;
import model.JobEntity;
import model.SectionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import services.GeographicalProcess;
import services.GeographicalService;
import tasks.ParseExcelFileTask;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by chetan on 22.12.2017.
 */
@Service
public class GeographicalServiceImpl implements GeographicalService {


    private GeographicalProcess geographicalProcess;

    private TaskExecutor taskExecutor;

    private FileConfiguration fileConfiguration;

    @Autowired
    public GeographicalServiceImpl(TaskExecutor taskExecutor,
                                   GeographicalProcess geographicalProcess,
                                   FileConfiguration fileConfiguration) {
        this.taskExecutor = taskExecutor;
        this.geographicalProcess = geographicalProcess;
        this.fileConfiguration = fileConfiguration;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RegisterJobResponse registerJob(RegisterJobRequest registerJobRequest) {
        final RegisterJobResponse registerJobResponse = new RegisterJobResponse();
        final JobEntity jobEntity = JobEntityMapper.dto2Entity(registerJobRequest);
        final JobEntity savedJobEntity = geographicalProcess.saveJob(jobEntity);
        taskExecutor.execute(new ParseExcelFileTask(savedJobEntity.getId(), fileConfiguration, geographicalProcess));
        registerJobResponse.setJobId(savedJobEntity.getId());
        return registerJobResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public GetResultsResponse getResults(Long jobId) {
        final GetResultsResponse getResultsResponse = new GetResultsResponse();
        final JobEntity jobEntity = geographicalProcess.findById(jobId);
        final List<SectionData> sectionDatas = jobEntity.getSectionEntityList().stream()
                .map(sectionEntity -> SectionEntityMapper.entity2Dto(sectionEntity))
                .collect(Collectors.toList());
        if (!sectionDatas.isEmpty()) {
            getResultsResponse.setSectionDatas(sectionDatas);
        } else {
            throw new EntityNotFoundException("File does not exists or not processed yet.");
        }
        getResultsResponse.setJobId(jobEntity.getId());
        getResultsResponse.setJobName(jobEntity.getJobName());

        return getResultsResponse;
    }

    @Override
    public SearchResultsResponse searchResults(String code, String name, Long jobId) {
        final SearchResultsResponse searchResultsResponse = new SearchResultsResponse();
        final List<SectionEntity> sectionEntities = geographicalProcess.findByNameAndCode(name, code, jobId);
        final List<SectionData> sectionDatas = sectionEntities.stream().map(sectionEntity -> SectionEntityMapper.entity2Dto(sectionEntity)).collect(Collectors.toList());
        searchResultsResponse.setSectionDatas(sectionDatas);
        return searchResultsResponse;
    }
}
