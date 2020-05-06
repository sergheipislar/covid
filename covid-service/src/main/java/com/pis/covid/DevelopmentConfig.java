package com.pis.covid;


import com.pis.covid.data.RegionRepository;
import com.pis.covid.domain.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Profile("prod")
public class DevelopmentConfig {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private GoogleSheetDataImporter googleSheetDataImporter;

    @Bean
    public CommandLineRunner dataLoader(RegionRepository regionRepository) {
        return args -> {
            addRegions(regionRepository);
            googleSheetDataImporter.importData();
        };
    }

    private void addRegions(RegionRepository regionRepository) {
        if (regionRepository.count() == 0) {
            List<String> regionNames = List.of("Suceava", "Mun. București", "Brașov", "Constanța", "Arad", "Neamț",
                    "Cluj", "Hunedoara", "Timiș", "Galați", "Vrancea", "Iași", "Ilfov", "Botoșani", "Bihor", "Maramureș",
                    "Mureș", "Ialomița", "Covasna", "Prahova", "Bistrița-Năsăud", "Dolj", "Sibiu", "Bacău", "Dâmbovița",
                    "Giurgiu", "Călărași", "Satu Mare", "Teleorman", "Caraș-Severin", "Buzău", "Brăila", "Argeș",
                    "Mehedinți", "Alba", "Vaslui", "Olt", "Gorj", "Vâlcea", "Sălaj", "Tulcea", "Harghita");
            regionRepository.saveAll(regionNames.stream().map(Region::new).collect(Collectors.toList()));
        }
    }
}
