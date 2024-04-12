package com.dayone.model.constants;

public enum Authority {
    // Spring Security 에서 'ROLE_' Prefix 를 제외하여 권한으로 받아들인다.
    ROLE_READ,
    ROLE_WRITE;
}
