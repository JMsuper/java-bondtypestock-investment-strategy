package com.finance.adam.repository.pricealarm.domain;

import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import com.finance.adam.util.AlarmAddedInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 알람 설정 요일
     * 0 : 월요일, 1 : 화요일, 2 : 수요일, 3 : 목요일, 4 : 금요일, 5 : 토요일, 6 : 일요일
     */
    private String weekDayList;

    /**
     * 알람 설정 시간
     * ex) 09:00
     */
    private LocalDateTime time;

    /**
     * 알람 추가 정보 리스트
     */
    private String infoIndexList;

    @ManyToOne
    @JoinColumn(name = "save_corp_info_id")
    private SaveCorpInfo saveCorpInfo;

    public List<AlarmAddedInfo> fromInfoIndexList(){
        String str = this.infoIndexList;
        str = str.replace("[", "").replace("]", ""); // 대괄호 제거
        String[] strArray = str.split(","); // 쉼표를 기준으로 문자열 분리

        int[] intArray = new int[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            intArray[i] = Integer.parseInt(strArray[i]); // 문자열을 정수로 변환
        }

        List<AlarmAddedInfo> alarmAddedInfoList = new ArrayList<>();
        for(int i = 0; i < intArray.length; i++){
            alarmAddedInfoList.add(AlarmAddedInfo.valueOfFrom(intArray[i]));
        }
        return alarmAddedInfoList;
    }

    public void setInfoIndexList(List<AlarmAddedInfo> alarmAddedInfoList){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i < alarmAddedInfoList.size(); i++){
            sb.append(alarmAddedInfoList.get(i).getIndex());
            if(i != alarmAddedInfoList.size() - 1){
                sb.append(",");
            }
        }
        sb.append("]");
        this.infoIndexList = sb.toString();
    }
}
