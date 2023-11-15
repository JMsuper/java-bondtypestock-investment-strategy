package com.finance.adam.datashuttle;

import com.finance.adam.calculator.FinancialCalculator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class Scrapper {
    /*
    return :
      1. EPS
      2. BPS
      3. ROE
     */
    public Map<String,String> getFinancialData(String stockCode) {
        Document doc;
        String url = "https://comp.fnguide.com/SVO2/ASP/SVD_main.asp?gicode=A" + stockCode;
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("EPS","0");
        returnMap.put("ROE","0");
        try{
            doc = Jsoup.connect(url).get();
            Elements elements = doc.select(".um_table#svdMainGrid10D table tbody tr");

            Iterator<Element> ie1 = elements.iterator();
            while(ie1.hasNext()) {
                Element e = ie1.next();
                Elements e2 = e.select("th div");
                String thName;

                // tr th div 까지는 동일
                // 이후 dl 태그가 존재하는지 여부에 따라 코드가 나눠줘야함.
                if(e2.select("dl").size() > 0){
                    thName = e2.select("dl dt").text();
                }else {
                    thName = e2.textNodes().get(0).text();
                }

                Iterator<String> keys = returnMap.keySet().iterator();
                while(keys.hasNext()) {
                    if (thName.equals(keys.next())) {
                        Elements e3 = e.select("td");
                        returnMap.put(thName, e3.get(0).text().replaceAll(",",""));
                    }
                }
            }
            double EPS = Double.valueOf(returnMap.get("EPS"));
            double ROE = Double.valueOf(returnMap.get("ROE"));
            if(EPS != 0){
                int BPS = (int) FinancialCalculator.calculateBPS(EPS,ROE);
                returnMap.put("BPS",String.valueOf(BPS));
            }
            return returnMap;
        } catch (Exception e) {
            return returnMap;
        }
    }
}