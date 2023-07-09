package com.finance.adam.util;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class OpenAPIUtil {

    public String fetchData(String url){
        String result = "";
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            result = response.getBody();
        } else {
            System.err.println("Request failed with status code: " + response.getStatusCode());
        }
        return result;
    }

    public String ConvertStringToZipFile(String response){
        byte[] byteData = response.getBytes();
        // 응답 본문을 zip 파일로 저장
        String filePath = "response.zip";
        try{
            OutputStream outputStream = new FileOutputStream(filePath);
            outputStream.write(byteData);
            outputStream.close();
            return filePath;
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    public String[] unzip(String zipFilePath) throws IOException {
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
        return unzipFilePath.toArray(new String[0]);
    }

    public void parseXML(String xmlPath){
        try {
            File xmlFile = new File(xmlPath);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            // 루트 엘리먼트 가져오기
            Element root = document.getDocumentElement();

            // 엘리먼트 노드 리스트 가져오기
            NodeList nodeList = root.getChildNodes();

            // 엘리먼트 노드 리스트 순회
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // 엘리먼트 이름 가져오기
                    String elementName = element.getNodeName();

                    // 엘리먼트 텍스트 내용 가져오기
                    String elementText = element.getTextContent();

                    // 필요한 작업 수행
                    System.out.println("Element Name: " + elementName);
                    System.out.println("Element Text: " + elementText);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
