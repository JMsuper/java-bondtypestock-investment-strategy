package com.finance.adam.service;

import com.finance.adam.exception.CustomException;
import com.finance.adam.exception.ErrorCode;
import com.finance.adam.repository.account.UserRepository;
import com.finance.adam.repository.account.domain.Account;
import com.finance.adam.repository.memo.MemoRepository;
import com.finance.adam.repository.memo.domain.Memo;
import com.finance.adam.repository.memo.dto.MemoCreateDTO;
import com.finance.adam.repository.memo.dto.MemoDTO;
import com.finance.adam.repository.memo.dto.MemoUpdateDTO;
import com.finance.adam.repository.savecorpinfo.SaveCorpInfoRepository;
import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final UserRepository userRepository;
    private final SaveCorpInfoRepository saveCorpInfoRepository;
    private final MemoRepository memoRepository;

    public MemoDTO createMemo(String userId, MemoCreateDTO memoCreateDTO) {
        Long saveCorpInfoId = memoCreateDTO.getSaveCorpInfoId();
        String content = memoCreateDTO.getContent();

        SaveCorpInfo saveCorpInfo = saveCorpInfoRepository.findByIdAndAccountId(saveCorpInfoId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SAVE_CORP_INFO_NOT_FOUND));

        Account account = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        Memo newMemo = Memo.builder()
                .account(account)
                .saveCorpInfo(saveCorpInfo)
                .content(content)
                .build();

        Memo savedMemo = memoRepository.save(newMemo);
        return MemoDTO.from(savedMemo);
    }

    public MemoDTO updateMemo(String userId, MemoUpdateDTO memoUpdateDTO) {
        Long memoId = memoUpdateDTO.getMemoId();
        String content = memoUpdateDTO.getContent();

        Memo memo = memoRepository.findByIdAndAccountId(memoId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        memo.setContent(content);
        Memo updatedMemo = memoRepository.save(memo);
        return MemoDTO.from(updatedMemo);
    }

    public void deleteMemo(String userId, Long memoId) {
        Memo memo = memoRepository.findByIdAndAccountId(memoId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_ALLOWED));

        memoRepository.delete(memo);
    }
}
