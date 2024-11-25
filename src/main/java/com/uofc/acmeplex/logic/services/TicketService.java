package com.uofc.acmeplex.logic.services;

import com.uofc.acmeplex.dto.request.ticket.TicketRequest;
import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.dto.response.ResponseCodeEnum;
import com.uofc.acmeplex.dto.response.ResponseData;
import com.uofc.acmeplex.entities.AcmePlexUser;
import com.uofc.acmeplex.entities.Showtime;
import com.uofc.acmeplex.entities.Theatre;
import com.uofc.acmeplex.entities.TheatreSeat;
import com.uofc.acmeplex.entities.Ticket;
import com.uofc.acmeplex.enums.BookingStatusEnum;
import com.uofc.acmeplex.enums.UserTypeEnum;
import com.uofc.acmeplex.exception.CustomException;
import com.uofc.acmeplex.logic.ITicketService;
import com.uofc.acmeplex.repository.ShowTimeRepository;
import com.uofc.acmeplex.repository.TheatreRepository;
import com.uofc.acmeplex.repository.TheatreSeatRepository;
import com.uofc.acmeplex.repository.TicketRepository;
import com.uofc.acmeplex.repository.UserRepository;
import com.uofc.acmeplex.security.RequestBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class TicketService implements ITicketService {

    private final UserRepository userRepository;

    private final ShowTimeRepository showtimeRepository;

    private final TheatreSeatRepository theatreSeatRepository;

    private final TheatreRepository theatreRepository;

    private final TicketRepository ticketRepository;

    private final PromotionCodeService promotionCodeService;

    private final RequestBean requestBean;

    public IResponse issueTicket(TicketRequest request) {

        log.debug("Issuing ticket for user: {}", requestBean.getPrincipal());
        AcmePlexUser user = userRepository.findByEmail(requestBean.getPrincipal())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new CustomException("Showtime not found", HttpStatus.NOT_FOUND));

        Theatre theatre = theatreRepository.findById(request.getTheatreId())
                .orElseThrow(() -> new CustomException("Theatre not found", HttpStatus.NOT_FOUND));

        int reservedSeats = request.getSeatIds().size();
        int size = theatre.getSeats().size();
        if (reservedSeats > size * 0.1 ) {
            throw new CustomException("You are not allowed to reserve more than 10% of available seats", HttpStatus.BAD_REQUEST);
        }

        // Fetch and validate seats
        List<TheatreSeat> seats = theatreSeatRepository.findAllById(request.getSeatIds());
        if (seats.size() != reservedSeats) {
            throw new CustomException("One or more seats not found", HttpStatus.BAD_REQUEST);
        }

        // Check seat availability
        for (TheatreSeat seat : seats) {
            if (seat.getTicket() != null && Objects.equals(seat.getTicket().getBookingStatus(), BookingStatusEnum.RESERVED)) {
                throw new CustomException("Seat " + seat.getSeatRow() + seat.getSeatNumber() + " is already reserved", HttpStatus.BAD_REQUEST);
            }
        }

        // Create and save ticket
        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setShowtime(showtime);
        ticket.setMovie(showtime.getMovie());


        // Assign the seats to the ticket
        for (TheatreSeat seat : seats) {
            seat.setTicket(ticket); // Link each seat to the ticket
            seat.setTaken(true); // Mark the seat as taken
        }
        ticket.setReservedSeats(seats);
        ticket.setBookingStatus(BookingStatusEnum.RESERVED);

        
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, ticketRepository.save(ticket));
    }

    public IResponse cancelTicket(Long ticketId) {
        // Fetch the ticket by its ID

        //Check if showTime is 72hours prior to current time


        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new CustomException("Ticket not found", HttpStatus.NOT_FOUND));

        // Check if the ticket is already canceled
        if (ticket.getBookingStatus() == BookingStatusEnum.CANCELLED) {
            throw new CustomException("Ticket has already been canceled", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime showtimeDateTime = ticket.getShowtime().getStartTime();
        if (showtimeDateTime.isAfter(currentTime.plusHours(72))) {
            throw new CustomException("Cannot cancel ticket less than 72 hours before showtime", HttpStatus.BAD_REQUEST);
        }


        // Change the status to CANCELED
        ticket.setBookingStatus(BookingStatusEnum.CANCELLED);

        // Free up the seats
        ticket.getReservedSeats().forEach((seat)-> seat.setTaken(false));

        //Credit 85%, remove 15% as cancellation fee for Ordinary Users, but credit 100% for Registered Users
        if (Objects.equals(ticket.getUser().getUserType().getName(), UserTypeEnum.REGISTERED_USER.getName())){
            // Create a promoCode with 100% of the ticket price
            promotionCodeService.createPromotionCode(ticket.getMovie().getMoviePrice(), requestBean.getPrincipal());
        } else {
            // Create a promoCode with 85% of the ticket price
            promotionCodeService.createPromotionCode((float) (ticket.getMovie().getMoviePrice() * 0.85), requestBean.getPrincipal());
        }

        // Save the updated ticket
        ticketRepository.save(ticket);
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, "Ticket canceled successfully");
    }
}
