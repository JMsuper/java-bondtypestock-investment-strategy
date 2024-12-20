package com.finance.adam.openapi.krx;

import com.finance.adam.repository.stockprice.dto.StockPriceInfoDTO;
import com.finance.adam.repository.corpinfo.CorpRepository;
import com.finance.adam.repository.stockprice.StockPriceRepository;
import com.finance.adam.repository.corpinfo.domain.CorpInfo;
import com.finance.adam.repository.stockprice.domain.StockPrice;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * 한국거래소 KRX 정보데이터 시스템 크롤링 API
 */
@Service
@Slf4j
public class CsvReaderService {

    private final CorpRepository corpRepository;
    private final StockPriceRepository stockPriceRepository;
    private final RestTemplate restTemplate;

    public CsvReaderService(CorpRepository corpRepository,
                            StockPriceRepository stockPriceRepository,
                            RestTemplate restTemplate) {
        this.corpRepository = corpRepository;
        this.stockPriceRepository = stockPriceRepository;
        this.restTemplate = restTemplate;
        log.debug("CsvReaderService initialized with repositories and RestTemplate");
    }

    // 주식 시세 정보 CSV 파일을 다운로드하기 위한 OTP 코드를 가져오는 메소드
    public String getKrxStockPriceOTPCode() {
        log.debug("Requesting OTP code from KRX");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = currentDate.format(formatter);
        log.debug("Using date {} for OTP request", formattedDate);

        HttpEntity<MultiValueMap<String, String>> request = getMultiValueMapHttpEntity(formattedDate, headers);

        // postForEntity 메서드는 POST 요청을 보내고, 응답을 ResponseEntity 객체로 받음
        // 아래 POST 요청은 Http Body 에 OTP 코드를 담아 반환하는 요청
        // EX) HTTP BODY : lksjdofevxlkjg
        String url = "http://data.krx.co.kr/comm/fileDn/GenerateOTP/generate.cmd";
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        log.info("Successfully received OTP code from KRX");
        return response.getBody();
    }

    private static HttpEntity<MultiValueMap<String, String>> getMultiValueMapHttpEntity(String formattedDate, HttpHeaders headers) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("locale", "ko_KR");
        map.add("mktId", "ALL");
        map.add("trdDd", formattedDate);
        map.add("share", "1");
        map.add("money", "1");
        map.add("csvxls_isNo", "false");
        map.add("name", "fileDown");
        map.add("url", "dbms/MDC/STAT/standard/MDCSTAT01501");

        // HttpEntity 는 HTTP 요청을 위한 클래스로, 요청 헤더와 요청 바디를 설정할 수 있음
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        return request;
    }


    public File getKrxStockPriceCsvFile() {
        log.debug("Starting to download KRX stock price CSV file");
        // krx에서 code 값 가져오기
        String otpCode = getKrxStockPriceOTPCode();
        log.debug("Retrieved OTP code: {}", otpCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("code", otpCode);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        String url = "http://data.krx.co.kr/comm/fileDn/download_csv/download.cmd";
        ResponseEntity<byte[]> response = restTemplate.postForEntity(url, request, byte[].class);
        log.debug("Received CSV file response from KRX");

        /**
         * Content-Disposition은 HTTP 헤더 중 하나로, HTTP 응답 본문의 처리 방식을 정의하는 데 사용됩니다.
         * 이 헤더는 주로 다운로드할 파일의 이름을 지정하거나, 브라우저가 응답을 어떻게 처리해야 하는지를 지시하는 데 사용됩니다.
         * Content-Disposition 헤더에는 주로 두 가지 타입의 값이 사용됩니다:
         *
         * inline: 이 값은 브라우저가 응답 본문을 즉시 표시해야 함을 나타냅니다.
         * 예를 들어, 이미지나 텍스트 파일과 같은 리소스를 브라우저에서 직접 열 수 있도록 합니다.
         * attachment: 이 값은 브라우저가 응답 본문을 다운로드해야 함을 나타냅니다.
         * 이때, filename 매개변수를 사용하여 다운로드될 파일의 이름을 지정할 수 있습니다.
         *
         * 예를 들어, Content-Disposition: attachment; filename="example.txt"라는 헤더는,
         * 브라우저에게 응답 본문을 example.txt 라는 이름의 파일로 다운로드하도록 지시합니다.
         */
        String contentDisposition = Objects.requireNonNull(response.getHeaders().get("Content-Disposition")).toString();
        String fileName = contentDisposition.substring(contentDisposition.indexOf("filename=") + 9, contentDisposition.length() - 1);
        log.debug("Extracted filename from response: {}", fileName);

        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH_mm_ss");
        String formattedDate = currentDate.format(formatter);

        Path directoryPath = Paths.get(".", "price-data");
        File directory = directoryPath.toFile();
        if(!directory.exists()){
            log.debug("Creating directory: {}", directoryPath);
            directory.mkdir();
        }

        Path filePath = Paths.get(directoryPath.toString(),formattedDate+ "-" + fileName);
        log.debug("Creating file at path: {}", filePath);

        File outputFile = filePath.toFile();
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            log.error("Failed to create new file", e);
        }

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(Objects.requireNonNull(response.getBody()));
            log.info("Successfully wrote CSV file to: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to write CSV file", e);
        }

        return outputFile;
    }

    private InputStreamReader createInputStreamReader(String filePath) {
        log.debug("Creating InputStreamReader for file: {}", filePath);
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            return new InputStreamReader(fileInputStream, "x-windows-949");
        } catch (UnsupportedEncodingException e) {
            log.error("Unsupported encoding for file: {}", filePath, e);
            throw new RuntimeException("Unsupported encoding", e);
        } catch (FileNotFoundException e) {
            log.error("File not found: {}", filePath, e);
            throw new RuntimeException(e);
        }
    }

    public Map<String, StockPriceInfoDTO> readKrxPriceCsvFile(String filePath) throws NumberFormatException {
        log.debug("Starting to read KRX price CSV file: {}", filePath);
        Map<String ,StockPriceInfoDTO> stockPriceInfoMap = new HashMap<>();

        try (CSVReader reader = new CSVReaderBuilder(createInputStreamReader(filePath)).build()) {
            String[] line;
            boolean isFirstLine = true;
            while ((line = reader.readNext()) != null) {
                /**
                 * CSV 파일의 컬럼순서에 의존함
                 * 따라서 아래의 순서와 다운로드 받은 CSV 파일의 컬럼 순서가 일치하지 않는다면,
                 * 함수 실행을 멈추고, 에러를 반환해야함
                 * 0 : 종목코드
                 * 1 : 종목명
                 * 2 : 시장구분
                 * 3 : 소속부
                 * 4 : 종가
                 * 5 : 대비
                 * 6 : 등락률
                 * 7 : 시가
                 * 8 : 고가
                 * 9 : 저가
                 * 10 : 거래량
                 * 11 : 거래대금
                 * 12 : 시가총액
                 * 13 : 상장주식수
                 */
                if(isFirstLine){
                    isFirstLine = false;
                    log.debug("Validating CSV column headers");

                    String[] columnNameList = {
                            "종목코드","종목명","시장구분","소속부","종가","대비","등락률","시가","고가","저가","거래량","거래대금","시가총액","상장주식수"
                    };

                    for(int i = 0; i < line.length; i++){
                        String columnName = line[i];
                        if(!columnName.equals(columnNameList[i])){
                            log.error("CSV 파일의 컬럼 순서가 일치하지 않습니다. {} : {}", columnName, columnNameList[i]);
                            return null;
                        }
                    }
                    continue;
                }

                StockPriceInfoDTO stockInfo = getStockPriceInfoDTO(line);
                stockPriceInfoMap.put(line[0], stockInfo);
            }
            log.info("Successfully read {} stock price records from CSV", stockPriceInfoMap.size());
        } catch (IOException e) {
            log.error("Error reading CSV file", e);
        } catch (CsvValidationException e) {
            log.error("CSV validation error", e);
        }

        return stockPriceInfoMap;
    }

    private StockPriceInfoDTO getStockPriceInfoDTO(String[] line) {
        log.trace("Creating StockPriceInfoDTO for stock code: {}", line[0]);
        StockPriceInfoDTO stockInfo = new StockPriceInfoDTO();
        stockInfo.setStockCode(line[0]);
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
        return stockInfo;
    }

    public void saveOrUpdateStockPrices(Map<String, StockPriceInfoDTO> stockPriceInfoMap) {
            log.debug("Starting to save or update stock prices");
            List<CorpInfo> corpInfo = corpRepository.findAllWithStockPrice();
            log.debug("Found {} corporations to process", corpInfo.size());

            for(CorpInfo corp : corpInfo){
                StockPrice stockPrice = corp.getStockPrice();
                if (stockPrice == null) {
                    log.debug("Creating new StockPrice for corporation: {}", corp.getParsedStockCode());
                    stockPrice = new StockPrice();
                    stockPrice.setCorpInfo(corp);
                }

                StockPriceInfoDTO stockPriceInfoDTO = stockPriceInfoMap.get(corp.getParsedStockCode());
                if(stockPriceInfoDTO == null){
                    log.warn("StockPriceInfoDTO is null. corpCode : {}", corp.getParsedStockCode());
                    continue;
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

                stockPriceRepository.saveAndFlush(stockPrice);
                log.debug("Saved/Updated stock price for corporation: {}", corp.getParsedStockCode());
            }
            log.info("Successfully completed stock price update for {} corporations", corpInfo.size());
    }
}