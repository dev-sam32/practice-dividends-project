package com.dayone.service;

import com.dayone.exception.impl.NoCompanyException;
import com.dayone.model.Company;
import com.dayone.model.Dividend;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.CacheKey;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import com.dayone.persist.repository.CompanyRepository;
import com.dayone.persist.repository.DividendRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE) // '@Cacheable' 은 캐시에 데이터가 없을 경우에 등록을, 있다면 버로 반환
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("Search company -> " + companyName);
        // 회사명으로 회사 정보 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                                                        .orElseThrow(NoCompanyException::new); // 오류 발생시 핸들링(옵셔널 벗겨낼 필요없어짐)
        // 조회 된 회사 ID로 배당금 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        List<Dividend> dividends = new ArrayList<>();
        for(var entity : dividendEntities) {
            dividends.add(new Dividend(entity.getDate(), entity.getDividend()));
        }

        // 결과 조합 후 반환
        return new ScrapedResult(new Company(company.getTicker(), company.getName()), dividends);
    }
}
