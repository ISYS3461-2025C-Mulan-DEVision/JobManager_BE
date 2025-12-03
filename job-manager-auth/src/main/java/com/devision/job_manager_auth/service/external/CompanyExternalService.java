package com.devision.job_manager_auth.service.external;

import com.devision.job_manager_auth.dto.external.CompanyBasicInfoDto;

import java.util.Optional;

public interface CompanyExternalService {

    Optional<CompanyBasicInfoDto> getCompanyBasicInfo(Long companyId);

    Optional<CompanyBasicInfoDto> getCompanyBasicInfoByEmail(String email);

    Optional<CompanyBasicInfoDto> getCompanyAuthStatus(Long companyId);

    boolean isCompanyActivated(String email);

    boolean isCompanyLocked(String email);

}
