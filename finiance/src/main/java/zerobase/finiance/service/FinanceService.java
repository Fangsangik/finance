package zerobase.finiance.service;

import org.springframework.stereotype.Service;
import zerobase.finiance.model.ScrapedRst;
import zerobase.finiance.persist.entitiy.CompanyEntity;

import java.util.Optional;

@Service
public interface FinanceService {
    ScrapedRst getDividendByCompanyName(String companyName);

}
