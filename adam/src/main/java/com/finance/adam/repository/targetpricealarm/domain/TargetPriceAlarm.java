package com.finance.adam.repository.targetpricealarm.domain;

import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import com.finance.adam.util.AlarmAddedInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetPriceAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * true : 매수, false : 매도
     */
    private boolean isBuy;

    /**
     * 목표 주가
     */
    private int targetPrice;

    /**
     * 알람 추가 정보 리스트
     */
    private String infoIndexList;

    @ColumnDefault("true")
    @Builder.Default()
    private boolean active = true;

    @ColumnDefault("false")
    @Builder.Default()
    private boolean alarmed = false;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JoinColumn(name = "save_corp_info_id")
    private SaveCorpInfo saveCorpInfo;

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

    public static boolean getValueFrom(String buyOrSell){
        return buyOrSell.equals("매수");
    }
}
