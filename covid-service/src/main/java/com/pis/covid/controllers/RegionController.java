package com.pis.covid.controllers;

import com.pis.covid.data.RegionRepository;
import com.pis.covid.domain.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.pis.covid.Constants.API_REGION;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = API_REGION, produces = MediaType.APPLICATION_JSON_VALUE)
public class RegionController {
    @Autowired
    private RegionRepository regionRepository;

    @GetMapping()
    public Iterable<Region> getRegions() {
        return regionRepository.findAllByOrderByNameAsc();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Region> insertRegion(@RequestBody Region region) {
        Region insertedRegion = regionRepository.save(region);
        return new ResponseEntity<>(insertedRegion, HttpStatus.CREATED);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Region> updatedRegion(@RequestBody Region region) {
        Region updatedRegion = regionRepository.save(region);
        return new ResponseEntity<>(updatedRegion, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteRegion(@RequestParam Long regionId)  {
        regionRepository.deleteById(regionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
