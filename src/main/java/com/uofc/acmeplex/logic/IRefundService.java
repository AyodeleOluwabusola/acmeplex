package com.uofc.acmeplex.logic;

import com.uofc.acmeplex.dto.response.IResponse;

public interface IRefundService {

    //SOLID principle: Dependency Inversion (Classes depending on abstractions)
    IResponse validateRefundCode(String code);
}
