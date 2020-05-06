package com.pis.covid.domain;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.sql.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private Date date;

    private int numberOfCases;

    @Formula("number_of_cases - (SELECT r.number_of_cases FROM record r where r.region_id=region_id and unix_timestamp(r.date)=unix_timestamp(`date`)-86400)")
    private Integer difference;

    @ManyToOne
    @JoinColumn(name="region_id")
    private Region region;

    public Record(Date date, int numberOfCases, Region region) {
        this.date = date;
        this.numberOfCases = numberOfCases;
        this.region = region;
    }

    public Record(Date date, int numberOfCases) {
        this.date = date;
        this.numberOfCases = numberOfCases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return numberOfCases == record.numberOfCases &&
                Objects.equal(id, record.id) &&
                Objects.equal(date.toLocalDate(), record.date.toLocalDate()) &&
                Objects.equal(region, record.region);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, date.toLocalDate(), numberOfCases, region);
    }
}
