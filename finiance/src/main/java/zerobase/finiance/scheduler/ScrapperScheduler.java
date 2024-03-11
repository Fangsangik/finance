package zerobase.finiance.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zerobase.finiance.model.Company;
import zerobase.finiance.model.ScrapedRst;
import zerobase.finiance.persist.entitiy.CompanyEntity;
import zerobase.finiance.persist.entitiy.DividendEntity;
import zerobase.finiance.repository.CompanyRepository;
import zerobase.finiance.repository.DividendRepository;
import zerobase.finiance.scrapper.Scrapper;
import zerobase.finiance.type.CacheKey;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@EnableCaching
@AllArgsConstructor
public class ScrapperScheduler {

    private final CompanyRepository companyRepository;
    private final Scrapper yahooFinanaceScrapper;
    private final DividendRepository dividendRepository;

    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true) //redis cache에 있는 값 모두 다 비운다.
    @Scheduled(cron = "${scheduler.scrap.yahoo}") //스프링 배치 사용 해보기

    //일정 주가 마다 수행
//    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    //스케줄러를 활용해서 회사마다 배당금 긁어오기 (추가적으로 배당금이 들어오면 자동으로 들어오게끔)
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is started");
        // 저장된 회사 목록을 조회
        List<CompanyEntity> companyEntities = companyRepository.findAll();
        // 회사마다 배당금 정보를 서로 스크래핑
        for (CompanyEntity companyEntity : companyEntities) {
            log.info("scrapping scheduler is started " + companyEntity.getName());
            ScrapedRst scrapedRst =
                    yahooFinanaceScrapper.scrap(new Company(companyEntity.getName(), companyEntity.getTicker()));

            // 스크래핑한 방금 정보 중 데이터 없는 값 저장
            scrapedRst.getDividendEntity().stream()
                    //디비든 모델을 디비든 엔티티로 메핑
                    .map(e -> new DividendEntity(companyEntity.getId(), e))
                    //엘리먼트를 하나씩 디비든 리포지토리에 삽입
                    .forEach(e -> {
                        boolean exist = dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exist) {
                            dividendRepository.save(e);
                            log.info("insert new dividend -> " + e.toString());
                        }
                    });

            //연속적으로 계속 request를 날리면 서버에 부하가 간다. -> 따라서 thread.sleep
            try {
                Thread.sleep(3000); // 3초를 의미
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }
}
