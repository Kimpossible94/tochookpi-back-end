package com.tochookpi.tochookpi.controller;

import com.tochookpi.tochookpi.dto.NaverApiAddressDTO;
import com.tochookpi.tochookpi.service.NaverApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/naver")
public class NaverApiController {
    private final NaverApiService naverApiService;

    public NaverApiController(NaverApiService naverApiService) {
        this.naverApiService = naverApiService;
    }

    @GetMapping("search/local")
    public ResponseEntity<List<NaverApiAddressDTO>> searchLocal(@RequestParam String query) {
        List<NaverApiAddressDTO> apiAddressDTOList = naverApiService.searchLocal(query);
        return ResponseEntity.ok(apiAddressDTOList);
    }
}
