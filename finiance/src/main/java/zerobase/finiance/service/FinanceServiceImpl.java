package zerobase.finiance.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zerobase.finiance.exception.impl.NoCompanyException;
import zerobase.finiance.model.Company;
import zerobase.finiance.model.Dividend;
import zerobase.finiance.model.ScrapedRst;
import zerobase.finiance.persist.entitiy.CompanyEntity;
import zerobase.finiance.persist.entitiy.DividendEntity;
import zerobase.finiance.repository.CompanyRepository;
import zerobase.finiance.repository.DividendRepository;
import zerobase.finiance.type.CacheKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class FinanceServiceImpl implements FinanceService {
    @Autowired
    private final CompanyRepository companyRepository;

    @Autowired
    private final DividendRepository dividendRepository;

    //요청이 자주 들어오는가? -> 한번 cache 하면 빠르게 응답 가능
    //자주 변경이 되는가? -> NO로 설정

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)//redis Server에 key, value와 다르다.
    @Override
    public ScrapedRst getDividendByCompanyName(String companyName) {
        log.info("search company -> " + companyName);

        //1. 회사 명을 기준 으로 회사 정보를 조회
        CompanyEntity company = companyRepository.findByName(companyName)
                .orElseThrow(() -> new NoCompanyException());
        //2. 조회된 회사 id로 배상금을 조회
        List<DividendEntity> dividendEntities = dividendRepository.findAllByCompanyId(company.getId());

        //3. 결과 조합 후 반환
        //리스트 타입이기에 가공 작업 필요
//        List<Dividend> dividends = new ArrayList<>();
//        for (Dividend dividend : dividends) {
//            dividends.add(Dividend.builder()
//                    .date(dividend.getDate())
//                    .dividend(dividend.getDividend())
//                    .build());
//        }

        List<Dividend> dividends = dividendEntities.stream()
                .map(e -> new Dividend(e.getDate(), e.getDividend()))
                .collect(Collectors.toList());

        return new ScrapedRst(new Company(company.getTicker(), company.getName()),
                dividends);
    }
}
