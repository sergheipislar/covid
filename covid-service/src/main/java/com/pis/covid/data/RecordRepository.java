package com.pis.covid.data;

import com.pis.covid.domain.Record;
import com.pis.covid.domain.RecordCountByDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

public interface RecordRepository extends CrudRepository<Record, UUID> {
    Record findFirstByOrderByDateDesc();
    Iterable<Record> findAllByOrderByDateAsc();
    Iterable<Record> findAllByRegionIdOrderByDateAsc(Long regionId);
    Iterable<Record> findAllByDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(Date statDate, Date endDate);
    Iterable<Record> findAllByRegionIdInAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateAsc(List<Long> regionId, Date statDate, Date endDate);

    @Query(value = "SELECT r.date as date, SUM(r.number_of_cases) as numberOfCases FROM record r WHERE 1 GROUP BY r.date",
            nativeQuery = true)
    List<RecordCountByDate> sumNumberOfCasesByDate();

    @Query(value = "SELECT r.date as date, SUM(r.number_of_cases) as numberOfCases FROM record r WHERE r.date>=:start and r.date<=:end GROUP BY r.date",
            nativeQuery = true)
    List<RecordCountByDate> sumNumberOfCasesByDate(Date start, Date end);
}
