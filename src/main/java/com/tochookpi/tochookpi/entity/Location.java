package com.tochookpi.tochookpi.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@AttributeOverrides({
        @AttributeOverride(name = "title", column = @Column(name = "location_title")),
        @AttributeOverride(name = "address", column = @Column(name = "location_address")),
        @AttributeOverride(name = "lng", column = @Column(name = "location_lng")),
        @AttributeOverride(name = "lat", column = @Column(name = "location_lat"))
})
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Location {
    private String title;
    private String address;
    private Double lng;
    private Double lat;
}
