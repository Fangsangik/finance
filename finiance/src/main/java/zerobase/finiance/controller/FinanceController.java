package zerobase.finiance.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.finiance.model.ScrapedRst;
import zerobase.finiance.service.FinanceServiceImpl;

@RestController
@RequestMapping("/finance")
@AllArgsConstructor
public class FinanceController {

    private final FinanceServiceImpl financeService;

    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(
            // ResponseEntity<?> -> 어떤 타입으로 생성할지 정하지 않았을때
            @PathVariable String companyName
    ) {
        ScrapedRst rst = financeService.getDividendByCompanyName(companyName);
        return ResponseEntity.ok(rst);
    }

}
