package zerobase.finiance.controller;

import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import zerobase.finiance.exception.impl.NoCompanyException;
import zerobase.finiance.model.Company;
import zerobase.finiance.persist.entitiy.CompanyEntity;
import zerobase.finiance.service.CompanyServiceImpl;
import zerobase.finiance.type.CacheKey;

import java.util.List;

@RestController
@RequestMapping("/company") //경로에 공통되는 부분을 빼 줄 수 있다.
@AllArgsConstructor
public class CompanyController {

    private final CompanyServiceImpl companyService;
    private final CacheManager cacheManager;

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autoComplete(
            @PathVariable String keyWord
    ) {
        List<String> rst = companyService.getCompanyNameByKeyword(keyWord);
        return ResponseEntity.ok(rst);
    }

    //회사의 수가 많을 경우 -> API 호출 결과로 network 환경에 영향을 미칠 가능성 높다
    //pagerable = page기능
    //page기능 -> 12345 몇번재 page인지 확인
    @GetMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(final Pageable pageable) { //임의로 변경 되는 것을 막기 위해 final
        Page<CompanyEntity> companies = companyService.findAllCompany(pageable);
        return ResponseEntity.ok(companies);
    }

    @PostMapping
    @PreAuthorize("hasRole('WRITE')")
    //"hasRole('WRITE')" -> 쓰기 권한만 있는 것
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim();
        if (ObjectUtils.isEmpty(ticker)) {
            throw new NoCompanyException();
        }

        Company company = companyService.save(ticker);
        companyService.addAutoCompleteKeyword(company.getName());
        return ResponseEntity.ok(company);
    }

    @DeleteMapping ("/{ticker}")
    @PreAuthorize("hasRole('WRITE')") //아무나 접근 하면 안됨
    public ResponseEntity<?> deleteCompany(
            @PathVariable String ticker
    ) {
        String companyName = companyService.deleteCompany(ticker);
        clearFinanceCache(companyName);
        return ResponseEntity.ok(companyName);
    }

    public void clearFinanceCache(String companyName){ //케쉬 데이터 지워줘야 한다.
        cacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName);
    }
}
