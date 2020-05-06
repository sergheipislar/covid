package com.pis.covid.data;

import com.pis.covid.domain.Region;
import org.springframework.data.repository.CrudRepository;

public interface RegionRepository extends CrudRepository<Region, Long> {
    Iterable<Region> findAllByOrderByNameAsc();
}
