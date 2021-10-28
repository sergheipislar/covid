package com.pis.covid;

import com.pis.covid.domain.Region;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.pis.covid.Constants.API_REGION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class RegionIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void insert_update_get_delete_region() {
        Region insertedRegion = insertRegion();
        Region updatedRegion = updateRegion(insertedRegion);
        getRegion(updatedRegion);
        deleteRegion(updatedRegion);
    }

    private Region insertRegion() {
        // Given
        Region region = new Region("Cluj");

        // When
        ResponseEntity<Region> response = post(region, Region.class);

        // Then
        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(response.getBody().getId(), notNullValue());
        assertThat(response.getBody().getName(), equalTo(region.getName()));

        return response.getBody();
    }

    private Region updateRegion(Region region) {
        // Given
        Region updatedRegion = new Region(region.getId(), "Sibiu");

        // When
        ResponseEntity<Region> response = put(updatedRegion, Region.class);

        // Then
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), equalTo(updatedRegion));

        return response.getBody();
    }

    private void getRegion(Region category) {
        // When
        ResponseEntity<Region[]> response = get(Region[].class);

        // Then
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), equalTo(new Region[]{category}));
    }

    private void deleteRegion(Region category) {
        // When
        ResponseEntity<?> response = delete("regionId", category.getId().toString());

        // Then
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Override
    String getApiPath() {
        return API_REGION;
    }
}
