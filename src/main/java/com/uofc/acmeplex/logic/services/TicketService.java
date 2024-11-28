package com.uofc.acmeplex.logic.services;

import com.uofc.acmeplex.dto.request.ticket.TicketRequest;
import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.dto.response.ResponseCodeEnum;
import com.uofc.acmeplex.dto.response.ResponseData;
import com.uofc.acmeplex.entities.Showtime;
import com.uofc.acmeplex.entities.TheatreSeat;
import com.uofc.acmeplex.entities.Ticket;
import com.uofc.acmeplex.enums.BookingStatusEnum;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TicketService implements ITicketService {

    private final UserRepository userRepository;

    private final ShowTimeRepository showtimeRepository;

    private final TheatreSeatRepository theatreSeatRepository;

    private final TheatreRepository theatreRepository;

    private final TicketRepository ticketRepository;

    private final RefundCodeService refundCodeService;

    private final RequestBean requestBean;

    public IResponse issueTicket(TicketRequest request) {

        log.debug("Issuing ticket for user: {}", requestBean.getPrincipal());
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new CustomException("Showtime not found", HttpStatus.NOT_FOUND));

        int reservedSeats = request.getSeatIds().size();
        int size = showtime.getTheatre().getSeats().size();
        if (reservedSeats > size * 0.1 ) {
            throw new CustomException("You are not allowed to reserve more than 10% of available seats", HttpStatus.BAD_REQUEST);
        }

        // Fetch and validate seats
        List<TheatreSeat> seats = theatreSeatRepository.findAllById(request.getSeatIds());
        if (seats.size() != reservedSeats) {
            throw new CustomException("One or more seats not found", HttpStatus.BAD_REQUEST);
        }

        // Check seat availability :
        List<Long> alreadyReservedSeats = theatreSeatRepository.findExistingTheatreSeatIds(request.getShowtimeId(), request.getSeatIds());
        if (!alreadyReservedSeats.isEmpty()) {
            throw new CustomException("One more selected seats(s) was already reserved", HttpStatus.BAD_REQUEST);
        }

        // Create and save ticket
        Ticket ticket = new Ticket();
        ticket.setEmail(request.getEmail());
        ticket.setShowtime(showtime);
        ticket.setMovie(showtime.getMovie());


        // Assign the seats to the ticket
        showtime.getTheatreSeats().addAll(seats);
        ticket.setTicketSeats(seats.stream().map(TheatreSeat::getId).toList());
        ticket.setBookingStatus(BookingStatusEnum.RESERVED);

        //Send Email with ticket code
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, ticketRepository.save(ticket));
    }

    public IResponse cancelTicket(Long ticketId) {

        //Use ticket code
        // compare email in token with email in request

        // Fetch the ticket by its ID
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new CustomException("Ticket not found", HttpStatus.NOT_FOUND));

        // Check if the ticket is already canceled
        if (ticket.getBookingStatus() == BookingStatusEnum.CANCELLED) {
            throw new CustomException("Ticket has already been canceled", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime showtimeDateTime = ticket.getShowtime().getStartTime();
        //Check if showTime is 72hours prior to current time
        log.debug("Calculated Time: {}", ChronoUnit.HOURS.between(currentTime, showtimeDateTime));
        if (ChronoUnit.HOURS.between(currentTime, showtimeDateTime) > 72) {
            throw new CustomException("Cannot cancel ticket less than 72 hours before showtime", HttpStatus.BAD_REQUEST);
        }


        // Change the status to CANCELED
        ticket.setBookingStatus(BookingStatusEnum.CANCELLED);

        // Free up the seats
        log.debug("SEATS TO CLEAR {}", ticket.getTicketSeats());
        if (!ticket.getTicketSeats().isEmpty()) {
            theatreSeatRepository.deleteShowTimSeatsBySeatIds(ticket.getTicketSeats());
            ticket.getTicketSeats().clear();
        }

        //Credit 85%, remove 15% as cancellation fee for Ordinary Users, but credit 100% for Registered Users
        String email = requestBean.getPrincipal(); //Only registered users have a email has token with email in header vaue
        if (StringUtils.isNotBlank(email)){
            // Create a promoCode with 100% of the ticket price
            refundCodeService.createRefundCode(ticket.getMovie().getMoviePrice(), email);
        } else {
            // Create a promoCode with 85% of the ticket price
            refundCodeService.createRefundCode((float) (ticket.getMovie().getMoviePrice() * 0.85), ticket.getEmail());
        }

        // Save the updated ticket
        ticketRepository.save(ticket);
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, "Ticket canceled successfully");
    }
}
