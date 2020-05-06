package com.pis.covid.controllers;

import com.google.common.collect.ImmutableList;
import com.pis.covid.data.RecordRepository;
import com.pis.covid.domain.Record;
import com.pis.covid.domain.RecordCountByDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static com.pis.covid.Constants.API_RECORD;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = API_RECORD, produces = MediaType.APPLICATION_JSON_VALUE)
public class RecordController {
    @Autowired
    private RecordRepository recordRepository;

    @GetMapping()
    public Iterable<Record> getRecordsByRegionId(@RequestParam("region.id") Optional<Long> regionId) {
        if (regionId.isPresent()) {
            return recordRepository.findAllByRegionIdOrderByDateAsc(regionId.get());
        } else {
            return recordRepository.findAllByOrderByDateAsc();
        }
    }

    @GetMapping("/byregions")
    public Iterable<Record> getRecordsByRegionIdsAndDates(@RequestParam("region.id") List<Long> regionIds, @RequestParam("start") Date start, @RequestParam("end") Date end) {
        return recordRepository.findAllByRegionIdInAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(regionIds, start, end);
    }

    @GetMapping("/bydate")
    public Iterable<Record> getRecordsByDate(@RequestParam("start") Date start, @RequestParam("end") Date end) {
        return recordRepository.findAllByDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(start, end);
    }

    @GetMapping("/all")
    public List<RecordCountByDate> getAllRecordsGroupByDate(@RequestParam("start") Optional<Date> start, @RequestParam("end") Optional<Date> end) {
        if (start.isPresent() && end.isPresent()) {
            return recordRepository.sumNumberOfCasesByDate(start.get(), end.get());
        } else {
            return recordRepository.sumNumberOfCasesByDate();
        }
    }

    @GetMapping("/lastday")
    public Iterable<Record> getRecordsFromLastDay() {
        Record lastRecord = recordRepository.findFirstByOrderByDateDesc();
        if (lastRecord != null) {
            return recordRepository.findAllByDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(lastRecord.getDate(), lastRecord.getDate());
        }

        return ImmutableList.of();
    }
}
