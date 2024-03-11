package zerobase.finiance.scrapper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import zerobase.finiance.model.Company;
import zerobase.finiance.model.Dividend;
import zerobase.finiance.model.ScrapedRst;
import zerobase.finiance.type.Month;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScrapper implements Scrapper{

    private static final String URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    //%s, %d -> 유동적으로 변동 하기 위함
    private static final String SUMMARY = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final Long START_TIME = 86400L;

    @Override
    public ScrapedRst scrap(Company company) {
        var scrapResult = new ScrapedRst();
        scrapResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000;
            //밀리세컨을 초 단위로 변경 하기 위함

            String url = String.format(URL, company.getTicker(), START_TIME, now);

            Connection connect = Jsoup.connect(url);
            Document document = connect.get();
            //Document클래스가 Element클래스를 상속
            //Elelment를 조회 할 수 있음 -> 가장 적합한 메소드를 찾아 사용

            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableElement = parsingDivs.get(0);

            Element tbody = tableElement.children().get(1);
            List<Dividend> dividends = new ArrayList<>();

            for (Element e : tbody.children()) {
                String txt = e.text(); // 웹사이트 들이 Dividend
                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.parseInt(splits[1].replace(",", ""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("UN_EXPECTED_MONTH_VALUE" + splits[0]);
                }

                dividends.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividend));


//                System.out.println(year + " / " + month + " / " + day + " / " + "-> " + dividend);
            }
            scrapResult.setDividendEntity(dividends);

        } catch (IOException e) {

            throw new RuntimeException(e);
        }

        return scrapResult;
    }

    //회사의 메타 정보를 scrapping으로 찾아서 결과로 반환
    //추후에 다른 정보를 더 제공 -> 메타 정보를 긁어오면 된다
    @Override
    public Company scrapCompanyByTicker(String ticker){
        String url = String.format(SUMMARY, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").get(0);
            String title = titleEle.text().split(" - ")[1].trim();
            //element에 text를 다 가져와서 " - " 기준으로 쪼갠다. idx 값을 가져오고

            return new Company(ticker, title);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
