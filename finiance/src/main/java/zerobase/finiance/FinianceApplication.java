package zerobase.finiance;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import zerobase.finiance.model.Company;
import zerobase.finiance.scrapper.Scrapper;
import zerobase.finiance.scrapper.YahooFinanceScrapper;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling //크론 표현식 사용 하기 위해 필요
@EnableCaching //캐시를 스프링 부트에서 사용 할 수 있도록 함
public class FinianceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinianceApplication.class, args);
	}
}
