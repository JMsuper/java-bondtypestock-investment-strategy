package com.finance.adam.service;

import com.finance.adam.dto.StockPriceInfoDTO;
import com.finance.adam.repository.CorpRepository;
import com.finance.adam.repository.StockPriceRepository;
import com.finance.adam.repository.domain.CorpInfo;
import com.finance.adam.repository.domain.StockPrice;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CsvReaderService {

    private final CorpRepository corpRepository;
    private final StockPriceRepository stockPriceRepository;

    public CsvReaderService(CorpRepository corpRepository, StockPriceRepository stockPriceRepository) {
        this.corpRepository = corpRepository;
        this.stockPriceRepository = stockPriceRepository;
    }

    public String getKrxStockPriceOTPCode() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("locale", "ko_KR");
        map.add("mktId", "ALL");
        map.add("trdDd", "20240328");
        map.add("share", "1");
        map.add("money", "1");
        map.add("csvxls_isNo", "false");
        map.add("name", "fileDown");
        map.add("url", "dbms/MDC/STAT/standard/MDCSTAT01501");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://data.krx.co.kr/comm/fileDn/GenerateOTP/generate.cmd", request, String.class);
        System.out.println("response = " + response);
        return response.getBody();
    }


    public File getKrxStockPriceCsvFile() {
        // krx에서 code 값 가져오기
        String otpCode = getKrxStockPriceOTPCode();

        // 파일 다운로드 url 호출하기
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("code", otpCode);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<byte[]> response = restTemplate.postForEntity("http://data.krx.co.kr/comm/fileDn/download_csv/download.cmd", request, byte[].class);
        String contentDisposition = response.getHeaders().get("Content-Disposition").toString();
        String fileName = contentDisposition.substring(contentDisposition.indexOf("filename=") + 9, contentDisposition.length() - 1);

        File outputFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(response.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputFile;
    }

    public List<StockPriceInfoDTO> readKrxPriceCsvFile(String filePath) {
        List<StockPriceInfoDTO> stockInfoList = new ArrayList<>();

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath)).withSkipLines(1).build()) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                StockPriceInfoDTO stockInfo = new StockPriceInfoDTO();
                stockInfo.setStockCode("A"+line[0]);
                stockInfo.setStockName(line[1]);
                stockInfo.setMarketType(line[2]);
                stockInfo.setDepartment(line[3]);
                stockInfo.setClosingPrice(Long.parseLong(line[4]));
                stockInfo.setDifference(Long.parseLong(line[5]));
                stockInfo.setFluctuationRate(Double.parseDouble(line[6]));
                stockInfo.setOpeningPrice(Long.parseLong(line[7]));
                stockInfo.setHighPrice(Long.parseLong(line[8]));
                stockInfo.setLowPrice(Long.parseLong(line[9]));
                stockInfo.setVolume(Long.parseLong(line[10]));
                stockInfo.setTradingValue(Long.parseLong(line[11]));
                stockInfo.setMarketCap(Long.parseLong(line[12]));
                stockInfo.setListedShares(Long.parseLong(line[13]));
                stockInfoList.add(stockInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }

        return stockInfoList;
    }

    public void saveOrUpdateStockPrices(List<StockPriceInfoDTO> stockPriceInfoDTOList) {
        for (StockPriceInfoDTO stockPriceInfoDTO : stockPriceInfoDTOList) {
            Optional<CorpInfo> corpInfo = corpRepository.findById(stockPriceInfoDTO.getStockCode());
            if (!corpInfo.isPresent()) {
                log.error("corpInfo is null");
                log.error("stockCode = {}", stockPriceInfoDTO.getStockCode());
                continue;
            }

            StockPrice stockPrice = stockPriceRepository.findByCorpInfo(corpInfo.get());
            if (stockPrice == null) {
                stockPrice = new StockPrice();
                stockPrice.setCorpInfo(corpInfo.get());
            }

            stockPrice.setClosingPrice(stockPriceInfoDTO.getClosingPrice());
            stockPrice.setDifference(stockPriceInfoDTO.getDifference());
            stockPrice.setFluctuationRate(stockPriceInfoDTO.getFluctuationRate());
            stockPrice.setOpeningPrice(stockPriceInfoDTO.getOpeningPrice());
            stockPrice.setHighPrice(stockPriceInfoDTO.getHighPrice());
            stockPrice.setLowPrice(stockPriceInfoDTO.getLowPrice());
            stockPrice.setVolume(stockPriceInfoDTO.getVolume());
            stockPrice.setTradingValue(stockPriceInfoDTO.getTradingValue());
            stockPrice.setMarketCap(stockPriceInfoDTO.getMarketCap());
            stockPrice.setListedShares(stockPriceInfoDTO.getListedShares());

            stockPriceRepository.save(stockPrice);

        }
    }
}