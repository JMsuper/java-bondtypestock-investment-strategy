package com.finance.adam.repository.pricealarm.domain;

import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import com.finance.adam.util.AlarmAddedInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private LocalTime time;

    /**
     * 알람 추가 정보 리스트
     */
    private String infoIndexList;

    @ColumnDefault("true")
    @Builder.Default()
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "save_corp_info_id")
    private SaveCorpInfo saveCorpInfo;

    public List<Integer> fromWeekDayList(){
        String str = this.weekDayList;
        str = str.replace("[", "").replace("]", ""); // 대괄호 제거
        String[] strArray = str.split(","); // 쉼표를 기준으로 문자열 분리

        int[] intArray = new int[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            intArray[i] = Integer.parseInt(strArray[i]); // 문자열을 정수로 변환
        }

        List<Integer> list = new ArrayList<>();
        for (int i : intArray) {
            list.add(i);
        }
        return list;
    }

    public void setWeekDayList(List<Integer> weekDayList){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i < weekDayList.size(); i++){
            sb.append(weekDayList.get(i));
            if(i != weekDayList.size() - 1){
                sb.append(",");
            }
        }
        sb.append("]");
        this.weekDayList = sb.toString();
    }

    public List<Integer> fromInfoIndexList(){
        String str = this.infoIndexList;
        str = str.replace("[", "").replace("]", ""); // 대괄호 제거
        String[] strArray = str.split(","); // 쉼표를 기준으로 문자열 분리

        if(strArray[0].equals("")){
            return new ArrayList<>();
        }

        int[] intArray = new int[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            intArray[i] = Integer.parseInt(strArray[i]); // 문자열을 정수로 변환
        }

        List<Integer> list = new ArrayList<>();
        for (int i : intArray) {
            list.add(i);
        }
        return list;
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
