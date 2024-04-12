package com.dayone.web;

import com.dayone.model.ScrapedResult;
import com.dayone.service.FinanceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/finance")
public class FinanceController {

    private final FinanceService financeService;

    /**
     * 배당금 검색
     *
     * @param companyName 회사명
     * @return
     */
    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(@PathVariable String companyName) {
        var result = this.financeService.getDividendByCompanyName(companyName);
        return ResponseEntity.ok(result);
    }
}
