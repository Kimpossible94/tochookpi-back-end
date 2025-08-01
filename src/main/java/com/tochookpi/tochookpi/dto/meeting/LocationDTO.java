package com.tochookpi.tochookpi.dto.meeting;

import com.tochookpi.tochookpi.entity.Location;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDTO {
    private String title;
    private String address;
    private Double lng;
    private Double lat;

    public Location toLocation() {
        return Location.builder()
                .title(this.title)
                .address(this.address)
                .lng(this.lng)
                .lat(this.lat)
                .build();
    }
}
