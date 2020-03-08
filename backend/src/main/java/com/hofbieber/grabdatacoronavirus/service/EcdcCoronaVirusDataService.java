package com.hofbieber.grabdatacoronavirus.service;

import com.hofbieber.grabdatacoronavirus.business.domain.ReportTuple;
import com.hofbieber.grabdatacoronavirus.data.entity.RegionEntity;
import com.hofbieber.grabdatacoronavirus.data.entity.VirusCaseEntity;
import com.hofbieber.grabdatacoronavirus.data.repository.RegionRepository;
import com.hofbieber.grabdatacoronavirus.data.repository.VirusCaseRepository;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EcdcCoronaVirusDataService implements ICoronaVirusDataService {

    private static final String VIRUS_DATA = "https://www.ecdc.europa.eu/sites/default/files/documents/COVID-19-geographic-disbtribution-worldwide-%s.xls";

    @Autowired
    RegionRepository regionRepository;
    @Autowired
    VirusCaseRepository virusCaseRepository;

    @PostConstruct
    public void prepareData() {
        List<ReportTuple> data = fetchVirusData();

        Set<ReportTuple> countrys = data.stream()
                .map(value -> ReportTuple.builder()
                        .country(value.getCountry())
                        .countryId(value.getCountryId())
                        .isEu(value.isEu())
                        .build())
                .collect(Collectors.toSet());

        fillRegionData(countrys);
        fillCases(data);
    }

    private void fillCases(List<ReportTuple> data) {
        Map<String, RegionEntity> regionEntities = ((ArrayList<RegionEntity>) regionRepository.findAll())
                .stream()
                .collect(Collectors.toMap(RegionEntity::getId, x -> x));

        List<VirusCaseEntity> cases = data.stream()
                .sorted(Comparator.comparing(ReportTuple::getCountryId).thenComparing(ReportTuple::getDate))
                .map(reportTuple -> fillCasesByRegion(reportTuple, regionEntities.get(reportTuple.getCountryId())))
                .filter(virusCaseEntity -> virusCaseEntity != null)
                .collect(Collectors.toList());

        virusCaseRepository.saveAll(cases);
    }

    public VirusCaseEntity fillCasesByRegion(ReportTuple reportTuple, RegionEntity region) {
        if (region.getVirusCaseList().stream().filter(virusCaseEntity -> virusCaseEntity.getDate().equals(reportTuple.getDate())).findFirst().isPresent()) {
            return null;
        }
        VirusCaseEntity viruscaseEntity = new VirusCaseEntity();
        viruscaseEntity.setCountry(region);
        viruscaseEntity.setDate(new java.sql.Date(reportTuple.getDate().getTime()));
        viruscaseEntity.setCases(reportTuple.getConfCases());
        viruscaseEntity.setDeaths(reportTuple.getDeath());

        return viruscaseEntity;
    }

    private void fillRegionData(Set<ReportTuple> regions) {
        List<RegionEntity> _regions = (ArrayList<RegionEntity>) regionRepository.findAll();
        Set<String> regionIds = _regions.stream().map(region -> region.getId()).collect(Collectors.toSet());

        regionRepository.saveAll(regions.stream()
                .filter(reportTuple -> !regionIds.contains(reportTuple.getCountryId()))
                .map(reportTuple -> RegionEntity.fromReportTuple(reportTuple))
                .collect(Collectors.toList()));
    }

    public List<ReportTuple> fetchVirusData() {
        List<ReportTuple> list = new ArrayList<>();

        try {
            HttpResponse<InputStream> httpResponse = loadData();
            parseExcelFile(list, httpResponse);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    private void parseExcelFile(List<ReportTuple> list, HttpResponse<InputStream> httpResponse) throws IOException {
        Workbook workbook = new HSSFWorkbook(httpResponse.body());

        Sheet firstSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = firstSheet.iterator();

        while (iterator.hasNext()) {
            Row nextRow = iterator.next();

            // first row is the header
            if (nextRow.getRowNum() > 0) {
                list.add(ReportTuple.builder()
                        .date(nextRow.getCell(0).getDateCellValue())
                        .country(nextRow.getCell(1).getStringCellValue())
                        .confCases(nextRow.getCell(2).getNumericCellValue())
                        .death(nextRow.getCell(3).getNumericCellValue())
                        .countryId(nextRow.getCell(4).getStringCellValue())
                        .isEu(nextRow.getCell(5).getCellTypeEnum().equals(CellType.STRING) &&
                                nextRow.getCell(5).getStringCellValue().equals("EU"))
                        .build());
            }
        }
    }

    private HttpResponse<InputStream> loadData() throws IOException, InterruptedException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String dateStr = df.format(new Date()).toLowerCase();
        String fileStr = String.format(VIRUS_DATA, dateStr);

        System.out.println(fileStr);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fileStr))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofInputStream());
    }
}
