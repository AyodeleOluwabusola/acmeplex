package com.uofc.acmeplex.logic;

import com.uofc.acmeplex.dto.request.auth.UserSignInReq;
import com.uofc.acmeplex.dto.request.auth.UserSignUpDto;
import com.uofc.acmeplex.dto.response.IResponse;

public interface IUserService {

     //SOLID principle: Dependency Inversion (Classes depending on abstractions)
     IResponse signIn(UserSignInReq userSignInReq);
     IResponse signOut();
     IResponse createUser(UserSignUpDto req);

}
