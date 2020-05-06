package com.pis.covid.updater;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CovidUpdater {
    private static final Logger LOG = LoggerFactory.getLogger( CovidUpdater.class );

    private static final String SPREADSHEET_ID = "1d33m3Alj8_CMt1O27sfQYC0a6xJgjLWR7Y8j5fpFIg4";
    private static final int NUMBER_OF_ROWS = 42;

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        String sheetName;
        if (args.length < 1) {
            throw new IllegalArgumentException("The sheet name is missing");
        } else {
            sheetName = args[0];
            LOG.info("Start update of sheet {} ", sheetName);
        }
        String previousSheet = calculatePreviousSheet(sheetName);

        Sheets service = CovidUtil.getService();

        Map<String, List<RetrievedData>> valueMap = retrieveValues(SPREADSHEET_ID, service, List.of(previousSheet, sheetName));

        Map<String, RetrievedData> previousSheetValues = valueMap.get(previousSheet)
                .stream()
                .collect(Collectors.toMap(RetrievedData::getRegionName, v -> v));

        List<List<Object>> values = valueMap.get(sheetName)
                .stream()
                .map(retrievedData -> List.of((Object) (retrievedData.getNumberOfCases() - previousSheetValues.get(retrievedData.getRegionName()).getNumberOfCases())))
                .collect(Collectors.toList());

        List<ValueRange> data = new ArrayList<>();
        data.add(new ValueRange()
                .setRange(String.format("'%s'!C1:C%d", sheetName, NUMBER_OF_ROWS))
                .setValues(values));
        data.add(new ValueRange()
                .setRange(String.format("'%s'!B%d:B%d", sheetName, NUMBER_OF_ROWS+1, NUMBER_OF_ROWS+2))
                .setValues(List.of(List.of(String.format("=SUM(B1:B%d)", NUMBER_OF_ROWS)),
                        List.of(String.format("=B%d-'%s'!B%d", NUMBER_OF_ROWS+1, previousSheet, NUMBER_OF_ROWS+1)))));
        // Additional ranges to update ...

        BatchUpdateValuesRequest body = new BatchUpdateValuesRequest()
                .setValueInputOption("USER_ENTERED")
                .setData(data);
        BatchUpdateValuesResponse result =
                service.spreadsheets().values().batchUpdate(SPREADSHEET_ID, body).execute();
        System.out.printf("%d cells updated.", result.getTotalUpdatedCells());
    }

    private static Map<String, List<RetrievedData>> retrieveValues(String spreadsheetId, Sheets service, List<String> sheetTitlesToImport) throws IOException {
        List<String> ranges = sheetTitlesToImport.stream()
                .map(title -> String.format("%s!A1:B%d", title, NUMBER_OF_ROWS))
                .collect(Collectors.toList());

        BatchGetValuesResponse readResult = service.spreadsheets().values()
                .batchGet(spreadsheetId)
                .setRanges(ranges)
                .execute();

        Map<String, List<RetrievedData>> valueMap = new HashMap<>();
        List<ValueRange> valueRanges = readResult.getValueRanges();
        for (ValueRange valueRange : valueRanges) {
            Optional<String> title = extractTitleFromValueRange(valueRange);
            if (title.isPresent()) {
                List<RetrievedData> sheetValues = new ArrayList<>();
                for (List<Object> values : valueRange.getValues()) {
                    String regionName = (String) values.get(0);
                    int numberOfCases = Integer.parseInt((String) values.get(1));
                    sheetValues.add(new RetrievedData(regionName, numberOfCases));
                }
                valueMap.put(title.get(), sheetValues);
            }
        }

        return valueMap;
    }

    private static Optional<String> extractTitleFromValueRange(ValueRange valueRange) {
        Pattern p = Pattern.compile("^'(.*)'!.*$");
        Matcher m = p.matcher(valueRange.getRange());

        return m.find() ? Optional.of(m.group(1)) : Optional.empty();
    }

    private static String calculatePreviousSheet(String sheetName) {
        String[] parts = sheetName.split("-");
        int day = Integer.parseInt(parts[1])-1;
        return parts[0]+"-"+String.format("%02d", day);
    }

    @Data
    private static class RetrievedData {
        private final String regionName;
        private final int numberOfCases;
    }
}
