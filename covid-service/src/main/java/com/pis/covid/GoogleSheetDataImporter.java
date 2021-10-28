package com.pis.covid;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.ImmutableList;
import com.pis.covid.data.RecordRepository;
import com.pis.covid.data.RegionRepository;
import com.pis.covid.domain.Record;
import com.pis.covid.domain.Region;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class GoogleSheetDataImporter {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleSheetDataImporter.class);

    private static final String APPLICATION_NAME = "Covid";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final int NUMBER_OF_ROWS = 42;
    private static final List<String> SPREADSHEET_IDS = List.of(
            //"1lRXBGBA_FA3iezeL9S9BQa-WtHre9SOSRrihSiCozsY" // april
            //"1d33m3Alj8_CMt1O27sfQYC0a6xJgjLWR7Y8j5fpFIg4" // may
            //"1VsZ2_5KOpYKHhnP_wSfh5b3WaZixmvNaKWTN5_on2aQ" // june
            //"1LgXkdkI8VucuBRmt46ykkOtC43J9ny4kRls5jCiQ_-I" // july
            //"1LSk_9c2Yw6S6XTyDr8jXFyz1iE3ABkr_Q8tqTWs7t8Y" // august
            "18SfuyZk30v6NYpRxYkMR77FpMf-uA6Pg0a_OdOyd1lY" // september
    );

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private RecordRepository recordRepository;

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleSheetDataImporter.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public void importData() {
        Optional<Date> maxDate = retrieveMaxDateFromRepository(recordRepository);
        Date today = Date.valueOf(LocalDate.now());
        if (!maxDate.isPresent() || !maxDate.get().equals(today)){
            Iterable<Region> regions = regionRepository.findAll();
            Map<String, Region> regionsMap = ImmutableList.copyOf(regions)
                    .stream().collect(Collectors.toMap(region -> region.getName(), region -> region));
            SPREADSHEET_IDS.forEach(spreadsheetId -> {
                try {
                    Map<Date, List<RetrievedData>> retrieveData = retrieveData(maxDate, spreadsheetId);

                    if (retrieveData.size() > 0) {
                        importData(recordRepository, regionsMap, retrieveData);
                    } else {
                        LOG.debug("No data to import for spreadsheet {}", spreadsheetId);
                    }
                } catch (GeneralSecurityException | IOException e) {
                    LOG.error("Failed to retrieve data for spreadsheet {}", spreadsheetId, e);
                }
            });
        } else {
            LOG.debug("No data to import for today");
        }
    }

    private Optional<Date> retrieveMaxDateFromRepository(RecordRepository recordRepository) {
        Record record = recordRepository.findFirstByOrderByDateDesc();
        return record != null ? Optional.ofNullable(record.getDate()) : Optional.empty();
    }

    private void importData(RecordRepository recordRepository, Map<String, Region> regionsMap, Map<Date, List<RetrievedData>> retrieveData) {
        for (Date date : retrieveData.keySet()) {
            LOG.debug("Import data for {}", date);

            List<Record> records = retrieveData.get(date).stream()
                                        .map(data -> createRecord(date, data, regionsMap))
                                        .filter(record -> record.isPresent())
                                        .map(record -> record.get())
                                        .collect(Collectors.toList());

            recordRepository.saveAll(records);
            LOG.debug("Imported {} records for date {}", records.size(), date);
        }
    }

    private Optional<Record> createRecord(Date date, RetrievedData data, Map<String, Region> regionsMap) {
        Optional<Region> region = extractRegionByName(data.getRegionName(), regionsMap);
        if (!region.isPresent()) {
            LOG.warn("Region not found: {}", data.getRegionName());
        }
        return region.map(reg -> new Record(date, data.getNumberOfCases(), reg));
    }

    private Optional<Region> extractRegionByName(String regionName, Map<String, Region> regionsMap) {
        Optional<String> first = regionsMap.keySet().stream().filter(name -> name.startsWith(regionName)).findFirst();
        return first.map(name -> regionsMap.get(name));
    }

    private Map<Date, List<RetrievedData>> retrieveData(Optional<Date> maxDate, String spreadsheetId) throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        List<String> sheetTitlesToImport = retrieveSheetTitlesToImport(spreadsheetId, service, maxDate);

        return retrieveValues(spreadsheetId, service, sheetTitlesToImport);
    }

    private List<String> retrieveSheetTitlesToImport(String spreadsheetId, Sheets service, Optional<Date> maxDate) throws IOException {
        Spreadsheet execute = service.spreadsheets().get(spreadsheetId).execute();

        return execute.getSheets().stream()
                .filter(sheet -> !maxDate.isPresent() || createDateFromSheetTitle(sheet).after(maxDate.get()))
                .map(sheet -> sheet.getProperties().getTitle())
                .collect(Collectors.toList());
    }

    private Date createDateFromSheetTitle(Sheet sheet) {
        return Date.valueOf("2020-" + sheet.getProperties().getTitle());
    }

    private Map<Date, List<RetrievedData>> retrieveValues(String spreadsheetId, Sheets service, List<String> sheetTitlesToImport) throws IOException {
        List<String> ranges = sheetTitlesToImport.stream()
                .map(title -> String.format("%s!A1:B%d", title, NUMBER_OF_ROWS))
                .collect(Collectors.toList());

        BatchGetValuesResponse readResult = service.spreadsheets().values()
                .batchGet(spreadsheetId)
                .setRanges(ranges)
                .execute();

        Map<Date, List<RetrievedData>> valueMap = new HashMap<>();
        List<ValueRange> valueRanges = readResult.getValueRanges();
        if (valueRanges != null) {
            for (ValueRange valueRange : valueRanges) {
                Optional<String> title = extractTitleFromValueRange(valueRange);
                if (title.isPresent()) {
                    Date date = Date.valueOf("2020-" + title.get());
                    List<RetrievedData> retrievedData = new ArrayList<>();
                    for (List<Object> values : valueRange.getValues()) {
                        retrievedData.add(new RetrievedData(Integer.parseInt(((String) values.get(1)).replace(".", "")), (String) values.get(0)));
                    }
                    valueMap.put(date, retrievedData);
                }
            }
        }

        return valueMap;
    }

    private Optional<String> extractTitleFromValueRange(ValueRange valueRange) {
        Pattern p = Pattern.compile("^'(.*)'!.*$");
        Matcher m = p.matcher(valueRange.getRange());

        return m.find() ? Optional.of(m.group(1)) : Optional.empty();
    }

    @Data
    private class RetrievedData {
        private final int numberOfCases;
        private final String regionName;
    }
}
