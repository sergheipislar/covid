package com.pis.covid;

import com.google.common.collect.ImmutableList;
import com.pis.covid.data.RecordRepository;
import com.pis.covid.domain.Record;
import com.pis.covid.domain.Region;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.util.List;

import static com.pis.covid.Constants.API_RECORD;
import static com.pis.covid.Constants.API_REGION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class RecordIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private RecordRepository recordRepository;

    private Region region;

    private List<Record> records;

    @Before
    public void setUp() {
        this.region = prepareRegion();
        this.records = insertRecords();
    }

    @After
    public void tearDown() {
        recordRepository.deleteAll();
        deleteRegion();
    }

    @Test
    public void get_records() {
        getRecords();
    }

    private Region prepareRegion() {
        // Given
        Region region = new Region("Cluj");

        // When
        ResponseEntity<Region> response = post(API_REGION, region, Region.class);

        // Then
        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(response.getBody().getId(), notNullValue());
        assertThat(response.getBody().getName(), equalTo(region.getName()));

        return response.getBody();
    }

    private List<Record> insertRecords() {
        Record record1 = new Record(Date.valueOf("2020-04-01"), 12, region);
        Record record2 = new Record(Date.valueOf("2020-04-02"), 16, region);
        Iterable<Record> records = recordRepository.saveAll(List.of(record1, record2));

        return ImmutableList.copyOf(records);
    }

    private void getRecords() {
        // When
        ResponseEntity<Record[]> response = get(Record[].class);

        // Then
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), equalTo(new Record[]{records.get(0), records.get(1)}));
    }

    private void deleteRegion() {
        // When
        ResponseEntity<?> response = delete(API_REGION,"regionId", region.getId().toString());

        // Then
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Override
    String getApiPath() {
        return API_RECORD;
    }
}
