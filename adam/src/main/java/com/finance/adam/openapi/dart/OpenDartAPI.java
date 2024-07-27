package com.finance.adam.openapi.dart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.adam.openapi.dart.dto.DartFinancialInfo;
import com.finance.adam.openapi.dart.dto.DartReportDTO;
import com.finance.adam.openapi.dart.dto.request.OpenDartBaseRequestDTO;
import com.finance.adam.repository.reportalarm.domain.ReportType;
import com.finance.adam.util.CustomModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
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
import java.nio.file.Files;
import java.util.*;
import java.util.zip.ZipInputStream;
import java.util.Map;

@Component
@Slf4j
public class OpenDartAPI {

    @Value("${open-dart.corp-code-url}")
    private String corpCodeUrl;
    @Value("${open-dart.service-key}")
    private String serviceKey;
    @Value("${open-dart.report-code}")
    private String reprtCode;

    private final String ERROR_MSG_CORP_CODE = "(Open Dart)기업코드를 가져오는데 실패하였습니다.";
    private final String ERROR_MSG_XML = "XML 파일 처리에 실패하였습니다.";

    private final OpenDartUtil openDartUtil;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenDartAPI(OpenDartUtil openDartUtil, RestTemplate restTemplate, ObjectMapper objectMapper){
        this.openDartUtil = openDartUtil;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 정기보고서 재무정보 / 단일회사 주요계정
     * @param corpCode 전자공시 기업코드
     * @param bsnsYear 조회연도
     * @return
     */
    public List<DartFinancialInfo> getCorpFinancialInfo(String corpCode, String bsnsYear) {
        final String REQ_URL = "api/fnlttSinglAcnt.json";

        OpenDartBaseRequestDTO requestDTO = OpenDartBaseRequestDTO.builder()
                .corpCode(corpCode)
                .bsnsYear(bsnsYear)
                .reprtCode(reprtCode)
                .build();
        List<Object> response = openDartUtil.apiRequest(REQ_URL, requestDTO);

        List<DartFinancialInfo> list = new LinkedList<>();
        for(Object snakeCaseMap : response){
            DartFinancialInfo dto = new DartFinancialInfo();
            CustomModelMapper.convert((Map<String, String>) snakeCaseMap, dto, DartFinancialInfo.class);
            list.add(dto);
        }

        return list;
    }


    public List<DartReportDTO> getRecentReportList(String corpCode, int pageCount){
        return getRecentReportList(corpCode, pageCount, null);
    }

    /**
     * 공시정보 / 공시검색
     * @param corpCode 전자공시 기업코드
     * @param pageCount 페이지 당 조회건수
     * @param reportType 공시보고서 유형
     * @return
     */
    public List<DartReportDTO> getRecentReportList(String corpCode, int pageCount, ReportType reportType){
        final String REQ_URL = "api/list.json";

        OpenDartBaseRequestDTO requestDTO = OpenDartBaseRequestDTO.builder()
                .corpCode(corpCode)
                .bgnDe("20000101")
                .pageCount(String.valueOf(pageCount))
                .pblntfTy(reportType)
                .build();
        List<Object> response = openDartUtil.apiRequest(REQ_URL, requestDTO);

        List<DartReportDTO> list = new LinkedList<>();
        for(Object snakeCaseMap : response){
            DartReportDTO dto = new DartReportDTO();
            CustomModelMapper.convert((Map<String, String>) snakeCaseMap, dto, DartReportDTO.class);
            list.add(dto);
        }

        return list;
    }

    /**
     * 공시정보 / 고유번호
     * @return key : 주식 종목코드 , value : 전자공시 기업코드
     */
    public Map<String, String> getCorpCodeMap(){
        final String REQ_URL = "api/corpCode.xml";

        OpenDartBaseRequestDTO requestDTO = OpenDartBaseRequestDTO.builder().build();
        byte[] response = openDartUtil.download(REQ_URL, requestDTO);

        File xmlFile = zipFileToXmlFile(response);
        Map<String, String> corpCodeMap = xmlToCorpCodeMap(xmlFile);

        return corpCodeMap;
    }

    private File zipFileToXmlFile(byte[] bytes){
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
            StreamUtils.copy(Objects.requireNonNull(bytes),outputStream1);
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

        return corpCodeFile;
    }

    private Map<String, String> xmlToCorpCodeMap(File xmlFile){
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


        } catch (ParserConfigurationException | IOException | SAXException e) {
            log.warn(ERROR_MSG_XML,e);
            throw new RuntimeException(e);
        }
        return corpCodeMap;
    }
}
