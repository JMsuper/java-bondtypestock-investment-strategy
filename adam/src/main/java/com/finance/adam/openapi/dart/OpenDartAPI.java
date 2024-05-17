package com.finance.adam.openapi.dart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.adam.openapi.dart.vo.*;
import com.finance.adam.repository.reportalarm.domain.ReportType;
import com.finance.adam.util.MultiValueMapConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.ZipInputStream;

@Component
@Slf4j
public class OpenDartAPI {

    @Value("${open-dart.financial-info-url}")
    private String financialInfoUrl;
    @Value("${open-dart.corp-code-url}")
    private String corpCodeUrl;

    @Value("${open-dart.report-list-url}")
    private String reportListUrl;

    @Value("${open-dart.service-key}")
    private String serviceKey;
    @Value("${open-dart.report-code}")
    private String reprtCode;

    private final String ERROR_MSG_FINANCIAL_INFO = "(Open Dart)재무정보를 가져오는데 실패하였습니다.";
    private final String ERROR_MSG_CORP_CODE = "(Open Dart)기업코드를 가져오는데 실패하였습니다.";
    private final String ERROR_MSG_XML = "XML 파일 처리에 실패하였습니다.";

    private ObjectMapper objectMapper;

    public OpenDartAPI(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    public List<OpenDartFinancialInfo> getCorpFinancialInfo(String corpCode, String bsnsYear){
        RestTemplate restTemplate = new RestTemplate();

        OpenDartFinancialInfoRequest params = OpenDartFinancialInfoRequest.builder()
                .crtfcKey(serviceKey)
                .corpCode(corpCode)
                .bsnsYear(bsnsYear)
                .reprtCode(reprtCode)
                .build();

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(financialInfoUrl)
                .queryParams(MultiValueMapConverter.convert(objectMapper, params))
                .encode()
                .toUriString();
        URI uri;
        try {
            uri = new URI(urlTemplate);
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI 생성 중 오류가 발생하였습니다.",e);
        }

        ResponseEntity<String> rawResponse = restTemplate.getForEntity(uri,String.class);
        if(rawResponse.getStatusCode() != HttpStatus.OK){
            log.warn(rawResponse.toString());
            throw new RuntimeException(ERROR_MSG_FINANCIAL_INFO);
        }

        OpenDartFinancialInfoResponse response;
        try {
            response =  objectMapper.readValue(rawResponse.getBody(), OpenDartFinancialInfoResponse.class);
        } catch (JsonProcessingException e) {
            log.warn(rawResponse.toString());
            throw new RuntimeException(ERROR_MSG_FINANCIAL_INFO,e);
        }

        if(response.getStatus().equals("013")){
            log.info(response.toString());
            log.info("corpCode : " + corpCode + ", bsnsYear : " + bsnsYear);
            return null;
        }

        if(!response.getStatus().equals("000")){
            log.warn(response.toString());
            throw new RuntimeException(ERROR_MSG_FINANCIAL_INFO);
        }

        List<OpenDartFinancialInfo> financialInfoList = response.getList();
        return financialInfoList;
    }

    public List<OpenDartReportDTO> getRecentReportList(String corpCode, int pageCount){
        return getRecentReportList(corpCode, pageCount, null);
    }

    public List<OpenDartReportDTO> getRecentReportList(String corpCode, int pageCount, ReportType reportType){
        RestTemplate restTemplate = new RestTemplate();

        OpenDartReportListRequest params;

        if(reportType == null){
            params = OpenDartReportListRequest.builder()
                    .crtfcKey(serviceKey)
                    .corpCode(corpCode)
                    .bgnDe("20000101")
                    .pageCount(String.valueOf(pageCount))
                    .build();
        }else {
            params = OpenDartReportListRequest.builder()
                    .crtfcKey(serviceKey)
                    .corpCode(corpCode)
                    .bgnDe("20000101")
                    .pageCount(String.valueOf(pageCount))
                    .pblntfTy(reportType)
                    .build();
        }

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(reportListUrl)
                .queryParams(MultiValueMapConverter.convertWithOutNull(objectMapper, params))
                .encode()
                .toUriString();
        URI uri;
        try {
            uri = new URI(urlTemplate);
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI 생성 중 오류가 발생하였습니다.",e);
        }

        ResponseEntity<String> rawResponse = restTemplate.getForEntity(uri,String.class);
        if(rawResponse.getStatusCode() != HttpStatus.OK){
            log.warn(rawResponse.toString());
            throw new RuntimeException(ERROR_MSG_FINANCIAL_INFO);
        }

        OpenDartReportListResponse response;
        try {
            response =  objectMapper.readValue(rawResponse.getBody(), OpenDartReportListResponse.class);
        } catch (JsonProcessingException e) {
            log.warn(rawResponse.toString());
            throw new RuntimeException(ERROR_MSG_FINANCIAL_INFO,e);
        }

        if(response.getStatus().equals("013") || response.getTotalCount() == 0 || response.getList() == null){
            log.warn("corpCode : " + corpCode + ", 조회된 데이터가 없습니다.");
            return null;
        }

        if(!response.getStatus().equals("000")){
            log.error(response.toString());
        }

        List<OpenDartReportDTO> reportDTOList = response.getList();
        for(OpenDartReportDTO reportDTO : reportDTOList){
            String parsedReportName = reportDTO.getReportNm().trim();
            reportDTO.setReportNm(parsedReportName);
        }
        return reportDTOList;
    }

    public Map<String, String> getCorpCodeMap(){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.ALL));
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(corpCodeUrl)
                .queryParam("crtfc_key",serviceKey)
                .encode()
                .toUriString();

        URI uri;
        try {
            uri = new URI(urlTemplate);
        } catch (URISyntaxException e) {
            log.warn("URI 생성 전 URL = {}",urlTemplate);
            log.warn(ERROR_MSG_CORP_CODE,e);
            throw new RuntimeException("URI 생성 중 오류가 발생하였습니다.",e);
        }

        HttpEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET ,httpEntity, byte[].class);

        File tempFile = null;
        File corpCodeFile = null;

        try {
            tempFile = File.createTempFile("corpCode",".zip");
            corpCodeFile = File.createTempFile("corpCode",".xml");
            tempFile.deleteOnExit();
            corpCodeFile.deleteOnExit();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileOutputStream outputStream1 = new FileOutputStream(tempFile);
            ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(tempFile.toPath()));
            FileOutputStream outputStream2 = new FileOutputStream(corpCodeFile)
        ){
            StreamUtils.copy(Objects.requireNonNull(response.getBody()),outputStream1);
            zipInputStream.getNextEntry();

            int length;
            byte[] buffer = new byte[1024];

            while ( (length = zipInputStream.read(buffer)) >= 0){
                outputStream2.write(buffer, 0, length);
            }

        } catch (IOException e) {
            log.warn(ERROR_MSG_CORP_CODE,e);
            throw new RuntimeException(e);
        }


        Map<String, String> corpCodeMap = xmlToCorpCodeMap(corpCodeFile);

        return corpCodeMap;
    }

    public Map<String, String> xmlToCorpCodeMap(File xmlFile){
        Map<String, String> corpCodeMap = new HashMap<>();

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("list");

            for(int i = 0; i < nodeList.getLength(); i++){
                Node child = nodeList.item(i);
                NodeList items = child.getChildNodes();

                // 다트 Open API 에서 null 값을 " " 으로 표현함
                String corpCode = " ";
                String stockCode = " ";

                Node current;
                for(int j = 0; j < items.getLength(); j++){
                    current = items.item(j);
                    if(current.getNodeType() == Node.ELEMENT_NODE){

                        String nodeName = current.getNodeName();
                        String nodeValue = current.getTextContent();

                        if(nodeName.equals("corp_code")){
                            corpCode = nodeValue;
                        }else if(nodeName.equals("stock_code")){
                            if(nodeValue.equals(" ")){
                                break;
                            }
                            stockCode = nodeValue;
                        }
                    }
                    if(corpCode != null && !corpCode.equals(" ") && stockCode != null && !stockCode.equals(" ")){
                        corpCodeMap.put(stockCode,corpCode);
                        break;
                    }
                }
            }


        } catch (ParserConfigurationException e) {
            log.warn(ERROR_MSG_XML,e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.warn(ERROR_MSG_XML,e);
            throw new RuntimeException(e);
        } catch (SAXException e) {
            log.warn(ERROR_MSG_XML,e);
            throw new RuntimeException(e);
        }
        return corpCodeMap;
    }
}
