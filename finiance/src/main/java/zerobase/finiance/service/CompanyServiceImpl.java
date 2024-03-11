package zerobase.finiance.service;

import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import zerobase.finiance.exception.impl.NoCompanyException;
import zerobase.finiance.model.Company;
import zerobase.finiance.model.ScrapedRst;
import zerobase.finiance.persist.entitiy.CompanyEntity;
import zerobase.finiance.persist.entitiy.DividendEntity;
import zerobase.finiance.repository.CompanyRepository;
import zerobase.finiance.repository.DividendRepository;
import zerobase.finiance.scrapper.Scrapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private final Trie trie;

    @Autowired
    private final Scrapper yahooFinanceScrapper;

    @Autowired
    private final CompanyRepository companyRepository;
    @Autowired
    private final DividendRepository dividendRepository;

    @Override
    public Company save(String ticker) {
        boolean exist = companyRepository.existsByTicker(ticker);

        if (exist){
            throw new RuntimeException("already exist " + ticker);
        }
        return storeCompanyAndDividend(ticker);
    }

    //DB에 저장 하지 않으 경우에만
    @Override
    public Company storeCompanyAndDividend(String ticker) {
        //ticker를 기준으로 회사를 scrapping
        Company company = yahooFinanceScrapper.scrapCompanyByTicker(ticker);

        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker" + ticker);
        }

        //해당 회사가 존재 할 경우 회사 배당금 정보를 스크래핑
        ScrapedRst scrapedRst = yahooFinanceScrapper.scrap(company);


        //스크래핑 결과
        //배당금 엔티티는 companyId와 같이 저장, 회사 정보를 저장, 결과를 받은 엔티티에서 id 가져와서
        //dividend 모델을 엔티티로 메핑, id 값 넣어서 저장
        CompanyEntity saveCompany = companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntity = scrapedRst.getDividendEntity()
                .stream()
                .map(e -> new DividendEntity(saveCompany.getId(), e))
                .collect(Collectors.toList());

        //map은 사용하는 원소 element를 다른 값으로 mapping
        //map 말고도 filter or stream
        dividendRepository.saveAll(dividendEntity);
        return company;
    }

    @Override
    public Page<CompanyEntity> findAllCompany(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    @Override
    public void addAutoCompleteKeyword(String keyword) {
        //apache에서 구현된 trie는 응용된 형태로 구현이 가능
        trie.put(keyword, null);
    }

    @Override
    public List<String> autoComplete(String keyword) {
       return (List<String>) trie.prefixMap(keyword).keySet()
                .stream()
               .limit(10)
               .collect(Collectors.toList());
    }

    @Override
    public void deleteAutoCompleteKeyword(String keyword) {
        trie.remove(keyword);
    }

    @Override
    public List<String> getCompanyNameByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);

        Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()
                .map(e -> e.getName())
                .collect(Collectors.toList());
    }

    public String deleteCompany(String ticker) {
        CompanyEntity company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new NoCompanyException());

        dividendRepository.deleteAllByCompanyId(company.getId()); //이 값을 가진 db는 모두 삭제
        companyRepository.delete(company);

        //트라이에 저장 되어 있는 companyName
        deleteAutoCompleteKeyword(company.getName());
        return company.getName();
    }
}
