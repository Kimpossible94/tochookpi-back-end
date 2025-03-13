package com.tochookpi.tochookpi.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NaverApiAddressDTO {
    private String title;
    private String category;
    private String address;
    private String roadAddress;
    private String mapx;
    private String mapy;
}
