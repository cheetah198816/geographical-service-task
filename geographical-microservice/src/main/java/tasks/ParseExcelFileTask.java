package tasks;

import config.FileConfiguration;
import dto.excel.GeographicalClassData;
import dto.excel.SectionData;
import mappers.SectionEntityMapper;
import model.JobEntity;
import model.SectionEntity;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import services.GeographicalProcess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by chetan on 22.12.2017.
 */
public class ParseExcelFileTask implements Runnable {

    private GeographicalProcess geographicalProcess;

    private FileConfiguration fileConfiguration;

    private Long jobId;

    public ParseExcelFileTask(Long jobId, FileConfiguration fileConfiguration, GeographicalProcess geographicalProcess) {
        this.geographicalProcess = geographicalProcess;
        this.fileConfiguration = fileConfiguration;
        this.jobId = jobId;
    }

    public void run() {
        final JobEntity jobEntity = geographicalProcess.findById(jobId);
        final List<SectionData> sectionDatas = readAndparseExcelFile(jobEntity);
        final List<SectionEntity> sectionEntities = processData(sectionDatas);
        writeData(sectionEntities);
    }

    private List<SectionEntity> processData(List<SectionData> sectionDatas) {
        return sectionDatas.stream()
                .map(sectionData -> SectionEntityMapper.dto2Entity(sectionData))
                .collect(Collectors.toList());
    }

    private void writeData(List<SectionEntity> sectionEntities) {
        sectionEntities.stream()
                .forEach(sectionEntity -> geographicalProcess.saveSectionEntity(sectionEntity, jobId));
    }

    private List<SectionData> readAndparseExcelFile(JobEntity jobEntity) {
        final FileInputStream excelFile;
        final List<SectionData> sectionDataList = new ArrayList<>();
        final Workbook workbook;
        final int numberOfSheets;
        try {
            excelFile = new FileInputStream(new File(fileConfiguration.getBaseFilePath() + jobEntity.getFileName()));
            if (jobEntity.getFileName().contains(".xlsx")) {
                workbook = new XSSFWorkbook(excelFile);
            } else {
                workbook = new HSSFWorkbook(excelFile);
            }
            numberOfSheets = workbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheets; i++) {
                Sheet s = workbook.getSheetAt(i);
                Iterator<Row> iterator = s.iterator();
                while (iterator.hasNext()) {
                    setRowData(sectionDataList, iterator);
                }
            }
            return sectionDataList;
        } catch (IOException ex) {
            //log the error.
        }
        return sectionDataList;
    }

    private void setRowData(List<SectionData> sectionDataList, Iterator<Row> iterator) {
        final SectionData sectionData = new SectionData();
        final Row currentRow = iterator.next();
        final Iterator<Cell> cellIterator = currentRow.iterator();
        final List<GeographicalClassData> geographicalClassDatas = new ArrayList<>();
        while (cellIterator.hasNext()) {
            setCellData(sectionData, cellIterator, geographicalClassDatas);
        }
        sectionDataList.add(sectionData);
    }

    private void setCellData(SectionData sectionData, Iterator<Cell> cellIterator, List<GeographicalClassData> geographicalClassDatas) {
        final Cell currentCell = cellIterator.next();
        if (currentCell.getColumnIndex() == 0) {
            sectionData.setName(currentCell.getStringCellValue());
            sectionData.setGeographicalClassDataList(geographicalClassDatas);
        } else {
            GeographicalClassData geographicalClassData = new GeographicalClassData();
            geographicalClassData.setName(currentCell.getStringCellValue());
            geographicalClassData.setCode(cellIterator.next().getStringCellValue());
            geographicalClassDatas.add(geographicalClassData);
        }
    }
}
