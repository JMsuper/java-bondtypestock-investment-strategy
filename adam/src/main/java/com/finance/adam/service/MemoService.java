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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoService {

    private final UserRepository userRepository;
    private final SaveCorpInfoRepository saveCorpInfoRepository;
    private final MemoRepository memoRepository;

    public MemoDTO createMemo(String userId, MemoCreateDTO memoCreateDTO) {
        log.info("Creating memo for user: {}, saveCorpInfoId: {}", userId, memoCreateDTO.getSaveCorpInfoId());
        Long saveCorpInfoId = memoCreateDTO.getSaveCorpInfoId();
        String content = memoCreateDTO.getContent();

        SaveCorpInfo saveCorpInfo = saveCorpInfoRepository.findByIdAndAccountId(saveCorpInfoId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SAVE_CORP_INFO_NOT_FOUND));
        log.debug("Found SaveCorpInfo for id: {}", saveCorpInfoId);

        Account account = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        log.debug("Found Account for userId: {}", userId);

        Memo newMemo = Memo.builder()
                .account(account)
                .saveCorpInfo(saveCorpInfo)
                .content(content)
                .build();

        Memo savedMemo = memoRepository.save(newMemo);
        log.info("Successfully created memo with id: {}", savedMemo.getId());
        return MemoDTO.from(savedMemo);
    }

    public MemoDTO updateMemo(String userId, Long memoId, MemoUpdateDTO memoUpdateDTO) {
        log.info("Updating memo for user: {}, memoId: {}", userId, memoId);
        String content = memoUpdateDTO.getContent();

        Memo memo = memoRepository.findByIdAndAccountId(memoId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        log.debug("Found Memo for id: {}", memoId);

        memo.setContent(content);
        Memo updatedMemo = memoRepository.save(memo);
        log.info("Successfully updated memo with id: {}", memoId);
        return MemoDTO.from(updatedMemo);
    }

    public void deleteMemo(String userId, Long memoId) {
        log.info("Deleting memo for user: {}, memoId: {}", userId, memoId);
        Memo memo = memoRepository.findByIdAndAccountId(memoId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_ALLOWED));
        log.debug("Found Memo for id: {}", memoId);

        memoRepository.delete(memo);
        log.info("Successfully deleted memo with id: {}", memoId);
    }
}
