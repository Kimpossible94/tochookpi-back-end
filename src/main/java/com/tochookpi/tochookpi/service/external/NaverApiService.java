package com.tochookpi.tochookpi.service.external;

import com.tochookpi.tochookpi.dto.external.NaverApiAddressDTO;

import java.util.List;

public interface NaverApiService {
    List<NaverApiAddressDTO> searchLocal(String query);
}
