package zerobase.finiance.scrapper;

import zerobase.finiance.model.Company;
import zerobase.finiance.model.ScrapedRst;

public interface Scrapper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedRst scrap(Company company);
}
