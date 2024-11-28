package com.uofc.acmeplex.logic.services;

import com.uofc.acmeplex.config.AppProperties;
import com.uofc.acmeplex.dto.request.IUserDetails;
import com.uofc.acmeplex.dto.request.mail.EmailMessage;
import com.uofc.acmeplex.dto.request.movie.MovieRequest;
import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.dto.response.ResponseCodeEnum;
import com.uofc.acmeplex.dto.response.ResponseData;
import com.uofc.acmeplex.entities.Movie;
import com.uofc.acmeplex.entities.Showtime;
import com.uofc.acmeplex.entities.Theatre;
import com.uofc.acmeplex.enums.MessageSubTypeEnum;
import com.uofc.acmeplex.exception.CustomException;
import com.uofc.acmeplex.logic.IMovieService;
import com.uofc.acmeplex.mail.EmailService;
import com.uofc.acmeplex.repository.MovieRepository;
import com.uofc.acmeplex.repository.ShowTimeRepository;
import com.uofc.acmeplex.repository.TheatreRepository;
import com.uofc.acmeplex.repository.UserRepository;
import com.uofc.acmeplex.security.RequestBean;
import com.uofc.acmeplex.util.CommonLogic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class MovieService implements IMovieService {
    private final UserRepository userRepository;

    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final ShowTimeRepository showTimeRepository;
    private final RequestBean requestBean;
    private final AppProperties appProperties;
    private final EmailService emailService;

    @Override
    public IResponse createMovie(MovieRequest movieRequest) {
        Optional<Movie> existingMovie = movieRepository.findByMovieName(movieRequest.getTitle());
        if (existingMovie.isPresent()) {
            throw new CustomException("Movie already exists", HttpStatus.CONFLICT);
        }
        Movie movie = MovieRequest.convertToEntity(movieRequest);

        //Retrieve all RUs
        List<IUserDetails> allRegisteredUsers = userRepository.findAllEmailsAndFirstName();
        for (IUserDetails userDetails : allRegisteredUsers) {
            log.debug("User Details: {}", userDetails);
            //Send email to all RUs
            EmailMessage emailMessage = new EmailMessage();
            emailMessage.setMessageBody(appProperties.getMovieAnnouncedMessage());
            emailMessage.setLinkUrl("http://localhost:8080/movies");
            emailMessage.setRecipients(allRegisteredUsers.stream().map(IUserDetails::getEmail).toArray(String[]::new));
            emailMessage.setSubject("Exciting News! A New Movie is Now Showing in Theatres!");
            emailMessage.setFirstName(userDetails.getFirstName());
            emailMessage.setRecipient(userDetails.getEmail());
            emailMessage.setMessageType("EMAIL");
            var subType = MessageSubTypeEnum.NEW_MOVIE_ALERT;

            emailMessage.setMessageSubType(subType);
            emailMessage.setMovie(movie);
            CompletableFuture.runAsync(()-> emailService.sendSimpleMail(emailMessage));
        }

        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, movieRepository.save(movie));
    }

    @Override
    public IResponse retrieveMovies(Pageable pageable, String name) {
        ResponseData<Map<String, Object>> response = new ResponseData<>();
        response.setResponse(ResponseCodeEnum.SUCCESS);

        /**Only show movies that were create 24hours ago on the landing.
         This is make sure only RUs have access before then **/
        log.debug("TIME HERE IS {}", LocalDateTime.now().minusHours(5));
        Page<Movie> raiseRequests = null;
        if (StringUtils.isNotBlank(requestBean.getPrincipal())) {
            //Only Registered User would have an email in header
            raiseRequests = movieRepository.findAllByActiveAndMovieName(pageable, true, name);
        } else {
            //Only show movies that were create 5hours ago on the landing for Ordinary Users
            raiseRequests = movieRepository.findAllByActiveAndCreateDateLessThanEqual(pageable, true, LocalDateTime.now().minusHours(5));
        }
        if (!raiseRequests.isEmpty()) {
            Map<String, Object> metaData = new HashMap<>();
            metaData.put("totalElements", raiseRequests.getTotalElements());
            metaData.put("totalPages", raiseRequests.getTotalPages());
            metaData.put("content", raiseRequests.getContent());
            response.setData(metaData);
        } else {
            response.setMessage("No movie has been created");
            response.setData(CommonLogic.getEmptyDataResponse());
        }
        return response;
    }

    public IResponse attachMovieToTheatres(Long movieId, Map<Long, List<LocalDateTime>> showTimes) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException("Movie not found", HttpStatus.NOT_FOUND));

        List<Theatre> theatres = theatreRepository.findAllById(showTimes.keySet());

        for (Theatre theatre : theatres) {
            List<LocalDateTime> theatreShowTimes = showTimes.get(theatre.getId());

            if (theatreShowTimes != null) {
                for (LocalDateTime showtime : theatreShowTimes) {
                    Showtime newShowtime = new Showtime();
                    newShowtime.setMovie(movie);
                    newShowtime.setTheatre(theatre);
                    newShowtime.setStartTime(showtime);

                    showTimeRepository.save(newShowtime);
                }
            }
        }
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, "Movie attached to theatres successfully");
    }

}
