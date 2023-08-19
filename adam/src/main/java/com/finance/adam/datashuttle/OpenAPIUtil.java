package com.finance.adam.datashuttle;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class OpenAPIUtil {

    public String fetchZipData(String url){
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            System.err.println("Request failed with status code: " + response.getStatusCode());
        }
        byte[] fileData = response.getBody();
        // 응답 본문을 zip 파일로 저장
        String fileName = "response.zip";
        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            outputStream.write(fileData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileName;
    }


    public String unzip(String zipFilePath) throws IOException {
        byte[] buffer = new byte[1024];
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipInputStream.getNextEntry();

        ArrayList<String> unzipFilePath = new ArrayList<String>();

        while (entry != null) {
            String entryName = entry.getName();
            File outputFile = new File(entryName);
            OutputStream outputStream = new FileOutputStream(outputFile);

            int bytesRead;
            while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            unzipFilePath.add(entryName);
            entry = zipInputStream.getNextEntry();
        }

        zipInputStream.closeEntry();
        zipInputStream.close();
        return unzipFilePath.get(0);
    }

    public Map<String, String> parseXML(String xmlPath){
        Map<String,String> retMap = new HashMap<>();
        try {
            File xmlFile = new File(xmlPath);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            // 루트 엘리먼트 가져오기
            Element root = document.getDocumentElement();

            // 엘리먼트 노드 리스트 가져오기
            NodeList nodeList = root.getElementsByTagName("list");

            // 엘리먼트 노드 리스트 순회
            for (int i = 0; i < nodeList.getLength(); i++) {
//            for (int i = 0; i < 10; i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    // 엘리먼트 텍스트 내용 가져오기
                    String corpName = getValue("corp_name",element);
                    String stockCode = getValue("stock_code",element);
                    if(stockCode.length() > 1){
                        retMap.put(stockCode,corpName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retMap;
    }

    private String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        return node.getNodeValue();
    }
}
