package com.uofc.acmeplex.logic.services;


import com.uofc.acmeplex.dto.request.auth.UserSignInReq;
import com.uofc.acmeplex.dto.request.auth.UserSignUpDto;
import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.dto.response.ResponseCodeEnum;
import com.uofc.acmeplex.dto.response.ResponseData;
import com.uofc.acmeplex.dto.response.auth.AuthResponse;
import com.uofc.acmeplex.entities.AcmePlexUser;
import com.uofc.acmeplex.exception.CustomException;
import com.uofc.acmeplex.logic.ITokenService;
import com.uofc.acmeplex.logic.IUserService;
import com.uofc.acmeplex.repository.UserRepository;
import com.uofc.acmeplex.util.CommonLogic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * Service class for User
 * Facilitates User functionalities
 * Fulfils SOLID principle: Single Responsibility- Only User operations such as sign in, sign out, create user
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class UserService implements IUserService {

    private final CommonLogic commonLogic;
    private final UserRepository userRepository;
    private final ITokenService iTokenService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Method to sign in created user
     *
     * @param userSignInReq
     * @return
     */
    @Override
    public IResponse signIn(UserSignInReq userSignInReq) {

        AcmePlexUser user = userRepository.findByEmail(userSignInReq.getEmail())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));


        if (!passwordEncoder.matches(userSignInReq.getPassword(), user.getPassword())) {
            throw new CustomException("Incorrect Username or Password", HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());


        var auth = AuthResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .authenticationToken(iTokenService.getAuthToken(user.getEmail(), claims))
                .build();

        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, auth);

    }

    @Override
    public IResponse signOut() {
        return null;
    }


    /**
     * Method to create user
     *
     * @param req
     * @return
     */
    @Override
    public IResponse createUser(UserSignUpDto req) {

        //Check if user already exists
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new CustomException("User already exists", HttpStatus.CONFLICT);
        }

        AcmePlexUser user = new AcmePlexUser();
        user.setEmail(req.getEmail());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setPassword(commonLogic.encodePassword(req.getPassword()));
        user.setUserType(req.getUserType());

        //Saves user to database
        userRepository.save(user);

        //Prepare response
        var resp = ResponseData.getInstance(ResponseCodeEnum.SUCCESS, "User created successfully");

        resp.setData(user);

        return resp;
    }
}
