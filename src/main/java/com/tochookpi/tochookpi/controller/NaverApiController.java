package com.tochookpi.tochookpi.controller;

import com.tochookpi.tochookpi.service.NaverApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/naver")
public class NaverApiController {
    private final NaverApiService naverApiService;

    public NaverApiController(NaverApiService naverApiService) {
        this.naverApiService = naverApiService;
    }

    @GetMapping("search/local")
    public ResponseEntity<Void> searchLocal(@RequestParam String query) {
        naverApiService.searchLocal(query);
        return ResponseEntity.ok().build();
    }
}
