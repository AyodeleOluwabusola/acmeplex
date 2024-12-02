package com.uofc.acmeplex.dto.request;

import java.time.LocalDateTime;

public interface IUserDetails {

    String getEmail();
    void setEmail(String email);
    String getFirstName();
    void setFirstName(String firstName);
    LocalDateTime getLastPaymentDate();
    void setLastPaymentDate(LocalDateTime lastPaymentDate);
}
