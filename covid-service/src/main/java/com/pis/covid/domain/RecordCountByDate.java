package com.pis.covid.domain;

import java.sql.Date;

public interface RecordCountByDate {
    Date getDate();
    Integer getNumberOfCases();
}
