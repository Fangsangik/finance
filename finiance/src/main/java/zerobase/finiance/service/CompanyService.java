package zerobase.finiance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import zerobase.finiance.model.Company;
import zerobase.finiance.persist.entitiy.CompanyEntity;

import java.util.List;

@Service
public interface CompanyService {
    Company save (String ticker);

    Company storeCompanyAndDividend(String ticker);

    Page<CompanyEntity> findAllCompany(Pageable pageable);

    void addAutoCompleteKeyword(String keyword);

    public List<String> autoComplete(String keyword);

    void deleteAutoCompleteKeyword(String keyword);

    List<String> getCompanyNameByKeyword(String keyword);
}
