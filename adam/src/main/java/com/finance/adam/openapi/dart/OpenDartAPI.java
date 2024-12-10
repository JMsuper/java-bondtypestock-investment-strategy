package com.finance.adam.openapi.dart;

import com.finance.adam.openapi.dart.dto.DartFinancialInfo;
import com.finance.adam.openapi.dart.dto.DartReportDTO;
import com.finance.adam.openapi.dart.dto.request.OpenDartBaseRequestDTO;
import com.finance.adam.repository.corpinfo.CorpRepository;
import com.finance.adam.repository.corpinfo.domain.CorpInfo;
import com.finance.adam.repository.reportalarm.domain.ReportType;
import com.finance.adam.service.RedisService;
import com.finance.adam.util.CustomModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;
import java.util.Map;

@Component
@Slf4j
public class OpenDartAPI {

    private final CorpRepository corpRepository;
    @Value("${open-dart.corp-code-url}")
    private String corpCodeUrl;
    @Value("${open-dart.service-key}")
    private String serviceKey;
    @Value("${open-dart.report-code}")
    private String reprtCode;

    private final String ERROR_MSG_CORP_CODE = "(Open Dart)기업코드를 가져오는데 실패하였습니다.";
    private final String ERROR_MSG_XML = "XML 파일 처리에 실패하였습니다.";

    private final OpenDartUtil openDartUtil;
    private final RedisService redisService;

    private final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");

    public OpenDartAPI(OpenDartUtil openDartUtil, RedisService redisService, CorpRepository corpRepository){
        this.openDartUtil = openDartUtil;
        this.redisService = redisService;
        this.corpRepository = corpRepository;
        log.debug("OpenDartAPI initialized with OpenDartUtil, RedisService and CorpRepository");
    }

    /**
     * 정기보고서 재무정보 / 단일회사 주요계정
     * @param corpCode 전자공시 기업코드
     * @param bsnsYear 조회연도
     */
    public List<DartFinancialInfo> getCorpFinancialInfo(String corpCode, String bsnsYear) {
        log.info("Getting financial info for corp: {}, year: {}", corpCode, bsnsYear);
        final String REQ_URL = "api/fnlttSinglAcnt.json";

        OpenDartBaseRequestDTO requestDTO = OpenDartBaseRequestDTO.builder()
                .corpCode(corpCode)
                .bsnsYear(bsnsYear)
                .reprtCode(reprtCode)
                .build();
        List<Object> response = openDartUtil.apiRequest(REQ_URL, requestDTO);
        log.debug("Received {} financial info records", response.size());

        List<DartFinancialInfo> list = new LinkedList<>();
        for(Object snakeCaseMap : response){
            DartFinancialInfo dto = new DartFinancialInfo();
            if(!(snakeCaseMap instanceof Map)){
                log.error("Invalid data type for conversion: {}", snakeCaseMap.getClass());
                continue;
            }
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) snakeCaseMap;
            CustomModelMapper.convert(map, dto, DartFinancialInfo.class);
            list.add(dto);
        }

        log.debug("Converted {} financial info records", list.size());
        return list;
    }

    /**
     * Redis에서 캐싱된 최근 공시를 조회.
     * 직접 OpenDart API를 활용하는 것은 공시 갱신 스케줄러에서만 수행하도록 제한
     * -> 관심사를 분리하기 위함
     */
    public List<DartReportDTO> getRecentReportListFive(String corpCode){
        log.info("Getting recent 5 reports from Redis for corp: {}", corpCode);
        // 1. 레디스에서 종목코드에 해당하는 공시 5건 조회
        List<Object> rawRepostList = redisService.getListRange(corpCode, 0, -1);
        log.debug("Retrieved {} raw reports from Redis", rawRepostList.size());

        // 역직렬화에 의해 List 내부 객체는 DartReportDTO인 상태임 -> 타입 캐스팅만 수행
        // 2. 조회된 Object 를 DartReportDTO 객체로 변환
        List<DartReportDTO> reportList = rawRepostList.stream()
                .map(obj -> (DartReportDTO) obj) // Object를 DartReportDTO로 캐스팅
                .collect(Collectors.toList());

        log.debug("Converted {} reports to DartReportDTO", reportList.size());
        // 3. List<DartReportDTO를 반환
        return reportList;
    }

    public List<DartReportDTO> getRecentReportList(String corpCode, int pageCount){
        log.debug("Getting recent reports with default parameters - corpCode: {}, pageCount: {}", corpCode, pageCount);
        return getRecentReportList(corpCode, pageCount, null);
    }

    /**
     * 공시정보 / 공시검색
     * @param corpCode 전자공시 기업코드
     * @param pageCount 페이지 당 조회건수
     * @param reportType 공시보고서 유형
     */
    public List<DartReportDTO> getRecentReportList(String corpCode, int pageCount, ReportType reportType){
        log.info("Getting recent reports - corpCode: {}, pageCount: {}, reportType: {}", corpCode, pageCount, reportType);
        final String REQ_URL = "api/list.json";
        String bgnDe = "20000101";

        if(corpCode == null){
            // 오늘 날짜 기준으로 2일 전 계산
            LocalDate oneMonthAgo = LocalDate.now().minusDays(2);
            // "yyyyMMdd" 형태로 포맷팅
            bgnDe = oneMonthAgo.format(yyyyMMdd);
            log.debug("No corpCode provided, using date range from: {}", bgnDe);
        }

        OpenDartBaseRequestDTO requestDTO = OpenDartBaseRequestDTO.builder()
                .corpCode(corpCode)
                .bgnDe(bgnDe)
                .pageCount(String.valueOf(pageCount))
                .pblntfTy(reportType)
                .build();
        List<Object> response = openDartUtil.apiRequest(REQ_URL, requestDTO);
        log.debug("Received {} reports from API", response.size());

        List<DartReportDTO> list = covertToDartReportDTO(response);
        log.debug("Converted {} reports to DartReportDTO", list.size());

        return list;
    }

    public int initRecentReportInRedis(List<String> corpCodeList){
        log.info("Initializing recent reports in Redis for {} corporations", corpCodeList.size());
        int cnt = 0;

        // 1. 종목코드명 리스트 순회
        for(String corpCode : corpCodeList){
            log.debug("Processing corporation: {}", corpCode);
            // 2. 종목코드에 해당하는 최근공시 5건 조회
            List<DartReportDTO> recentRepotList = getRecentReportList(corpCode,5);

            if(!recentRepotList.isEmpty()){
                cnt++;
            }else{
                log.debug("No recent reports found for corporation: {}", corpCode);
                continue;
            }

            // 3. 레디스에 추가
            for(DartReportDTO dto : recentRepotList){
                redisService.pushToList(corpCode,dto);
            }
            // 4. 5건을 넘길 경우를 고려하여, 자르기
            redisService.trimList(corpCode, 0, 4);
            log.debug("Successfully added and trimmed reports for corporation: {}", corpCode);
        }

        log.info("Initialized reports for {} corporations", cnt);
        return cnt;
    }

    /**
     * 공시유형별(ReportType)로 조회 -> why? dart API에서 공시유형을 알려주지 않음
     */
    public HashMap<ReportType, List<DartReportDTO>> updateRecentReportInRedis(){
        log.info("Updating recent reports in Redis by report type");
        // 0. 반환 Map 세팅
        HashMap<ReportType, List<DartReportDTO>> reportTypeMap = new HashMap<>();
        ReportType[] reportTypes = ReportType.values();
        for(ReportType reportType : reportTypes){
            reportTypeMap.put(reportType, new ArrayList<>());
        }

        Set<String> corpCodeSet = corpRepository.findAllWithStockPrice().stream().map(CorpInfo::getCorpCode).collect(Collectors.toSet());
        log.debug("Found {} corporations with stock prices", corpCodeSet.size());

        // 1. 공시유형별(ReportType) 조회
        for(ReportType reportType : reportTypes){
            log.debug("Processing report type: {}", reportType);
            List<DartReportDTO> dartReportDTOList = getRecentReportList(null, 100, reportType);

            for(DartReportDTO dto : dartReportDTOList){
                String corpCd = dto.getCorpCode();
                String rceptNo = dto.getRceptNo();

                // 2-0. 상장종목이 아닐경우 pass
                if(!corpCodeSet.contains(corpCd)){
                    log.trace("Skipping non-listed corporation: {}", corpCd);
                    continue;
                }

                List<DartReportDTO> redisList = redisService.getListRange(corpCd,0,-1)
                        .stream().map((item)->(DartReportDTO)item).toList();

                // 2-1. 레디스에 기업코드에 해당하는 key가 존재하지 않는 경우 -> key, list 추가
                if(redisList.isEmpty()){
                    log.debug("Adding first report for corporation: {}", corpCd);
                    redisService.pushToList(corpCd,dto);
                    reportTypeMap.get(reportType).add(dto);
                }

                // 2-2. 레디스에 기업코드에 해당하는 key가 존재하지만, List에는 없는 경우 -> list에 추가
                // + 공시번호가 작은 공시가 하나라도 있는 경우
                else if(redisList.stream().noneMatch((targetDto) -> targetDto.getRceptNo().equals(rceptNo))
                    && redisList.stream().anyMatch((targetDto) -> Long.parseLong(targetDto.getRceptNo()) < Long.parseLong(rceptNo))
                ){
                    log.debug("Adding new report for corporation: {}, report number: {}", corpCd, rceptNo);
                    redisService.pushToList(corpCd,dto);
                    redisService.trimList(corpCd,0,4);
                    reportTypeMap.get(reportType).add(dto);
                }
            }
        }

        log.info("Completed updating recent reports in Redis");
        // key : ReportType, value : 신규공시 담은 리스트 반환
        return reportTypeMap;
    }

    private List<DartReportDTO> covertToDartReportDTO(List<Object> response) {
        log.debug("Converting {} response objects to DartReportDTO", response.size());
        List<DartReportDTO> list = new LinkedList<>();
        for(Object snakeCaseMap : response){
            DartReportDTO dto = new DartReportDTO();
            if(!(snakeCaseMap instanceof Map)){
                log.error("Invalid data type for conversion: {}", snakeCaseMap.getClass());
                continue;
            }
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) snakeCaseMap;
            CustomModelMapper.convert(map, dto, DartReportDTO.class);
            list.add(dto);
        }
        return list;
    }

    /**
     * 공시정보 / 고유번호
     * @return key : 주식 종목코드 , value : 전자공시 기업코드
     */
    public Map<String, String> getCorpCodeMap(){
        log.info("Getting corporation code map");
        final String REQ_URL = "api/corpCode.xml";

        OpenDartBaseRequestDTO requestDTO = OpenDartBaseRequestDTO.builder().build();
        byte[] response = openDartUtil.download(REQ_URL, requestDTO);
        log.debug("Downloaded corporation code data, size: {} bytes", response.length);

        File xmlFile = zipFileToXmlFile(response);
        Map<String, String> corpCodeMap = xmlToCorpCodeMap(xmlFile);
        log.debug("Processed {} corporation codes", corpCodeMap.size());

        return corpCodeMap;
    }

    private File zipFileToXmlFile(byte[] bytes){
        log.debug("Converting zip file to XML file");
        File tempFile = null;
        File corpCodeFile = null;

        try {
            tempFile = File.createTempFile("corpCode",".zip");
            corpCodeFile = File.createTempFile("corpCode",".xml");
            tempFile.deleteOnExit();
            corpCodeFile.deleteOnExit();
            log.debug("Created temporary files for processing");

        } catch (IOException e) {
            log.error("Failed to create temporary files", e);
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
            log.error(ERROR_MSG_CORP_CODE, e);
            throw new RuntimeException(e);
        }

        log.debug("Successfully converted zip file to XML");
        return corpCodeFile;
    }

    private Map<String, String> xmlToCorpCodeMap(File xmlFile){
        log.debug("Converting XML file to corporation code map");
        Map<String, String> corpCodeMap = new HashMap<>();

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("list");
            log.debug("Found {} list elements in XML", nodeList.getLength());

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
                    if(corpCode != null && !corpCode.equals(" ") && !stockCode.equals(" ")){
                        corpCodeMap.put(stockCode,corpCode);
                        break;
                    }
                }
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            log.error(ERROR_MSG_XML, e);
            throw new RuntimeException(e);
        }
        log.debug("Successfully created corporation code map with {} entries", corpCodeMap.size());
        return corpCodeMap;
    }
}
