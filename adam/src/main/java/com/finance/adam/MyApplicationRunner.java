package com.finance.adam;

import com.finance.adam.dao.CorpCode;
import com.finance.adam.service.KoreaFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
public class MyApplicationRunner implements ApplicationRunner {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private KoreaFinanceService koreaFinanceService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Connection connection = dataSource.getConnection();
//        Map<String,String> codeList = koreaFinanceService.getStockCodeList();
//        for(String key : codeList.keySet()){
//            String code = key;
//            String name = codeList.get(code);
//            String sql = "insert into corp_code(code,name) values("+code+",\""+name+"\");";
//            jdbcTemplate.execute(sql);
//        }
        List<CorpCode> stockCodeList = jdbcTemplate.query("select * from corp_code",
                new RowMapper<CorpCode>() {
                    @Override
                    public CorpCode mapRow(ResultSet rs, int rowNum) throws SQLException {
                        CorpCode cc = new CorpCode(
                                rs.getString("code"),
                                rs.getString("name")
                        );
                        return cc;
                    }
                });
        for(int i = 0; i < stockCodeList.size(); i++){
            CorpCode cc = stockCodeList.get(i);
            Map<String, String> map = koreaFinanceService.getFinancialData(cc.getCode());
//            returnMap.put("EPS","0");
//            returnMap.put("BPS","0");
//            returnMap.put("ROE","0");
//            returnMap.put("배당수익률","0");
            String EPS = map.get("EPS");
            if (EPS.length() == 0){
                EPS = "0";
            }
            String BPS = map.get("BPS");
            if (BPS == null || BPS.length() == 0){
                BPS = "0";
            }
            String ROE = map.get("ROE");
            if (ROE.length() == 0){
                ROE = "0";
            }
            String extRet = "0";
            String Dividend = map.get("배당수익률");
            if (Dividend.length() == 0){
                Dividend = "0";
            }
            String sql = "insert into stock_info values(" + cc.getCode() + "," + EPS + "," + BPS + "," + ROE + "," + extRet + "," + Dividend + ")";
            jdbcTemplate.execute(sql);
        }

    }
}
