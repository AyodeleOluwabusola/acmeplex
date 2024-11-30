package com.uofc.acmeplex.logic.services;

import com.uofc.acmeplex.dto.request.mail.EmailMessage;
import com.uofc.acmeplex.dto.request.ticket.TicketRequest;
import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.dto.response.ResponseCodeEnum;
import com.uofc.acmeplex.dto.response.ResponseData;
import com.uofc.acmeplex.entities.Showtime;
import com.uofc.acmeplex.entities.TheatreSeat;
import com.uofc.acmeplex.entities.Ticket;
import com.uofc.acmeplex.enums.BookingStatusEnum;
import com.uofc.acmeplex.enums.MessageSubTypeEnum;
import com.uofc.acmeplex.exception.CustomException;
import com.uofc.acmeplex.logic.ITicketService;
import com.uofc.acmeplex.mail.EmailService;
import com.uofc.acmeplex.repository.InvoiceRepository;
import com.uofc.acmeplex.repository.RefundCodeRepository;
import com.uofc.acmeplex.repository.ShowTimeRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class TicketService implements ITicketService {

    private final UserRepository userRepository;

    private final ShowTimeRepository showtimeRepository;

    private final TheatreSeatRepository theatreSeatRepository;

    private final EmailService emailService;

    private final TicketRepository ticketRepository;

    private final RefundCodeService refundCodeService;

    private final RefundCodeRepository refundCodeRepository;

    private final InvoiceRepository invoiceRepository;

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

        float refundBalance = 0F;
        if (StringUtils.isNotBlank(request.getRefundCode())) {
            refundBalance = refundCodeRepository.findByCode(request.getRefundCode())
                    .orElseThrow(() -> new CustomException("Invalid Code", HttpStatus.NOT_FOUND))
                    .getBalance();
        }

        Float amountPaid = 0F;
        if (StringUtils.isNotBlank(request.getPaymentReference())) {
             amountPaid = invoiceRepository.findByPaymentReference(request.getPaymentReference())
                    .orElseThrow(() -> new CustomException("Payment reference not found", HttpStatus.NOT_FOUND))
                     .getAmount();
        }

        if ((refundBalance + amountPaid) < showtime.getMovie().getMoviePrice()) {
            throw new CustomException("Insufficient funds to purchase movie ticket", HttpStatus.BAD_REQUEST);
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
        ticket.setCode("TKT-" +refundCodeService.generateUniqueCode());

        //Send Email with ticket code
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setFirstName(ticket.getEmail());
        emailMessage.setRecipient(ticket.getEmail());
        emailMessage.setMessageType("EMAIL");
        emailMessage.setMessageBody("Ticket purchase");
        emailMessage.setSubject("Ticket purchase");
        emailMessage.setTheatre(ticket.getShowtime().getTheatre().getName());

        var subType = MessageSubTypeEnum.TICKER_PURCHASE;
        emailMessage.setMessageSubType(subType);

        sendTicketConfirmationEmail(emailMessage, showtime, seats, ticket);
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, ticketRepository.save(ticket));
    }

    private void sendTicketConfirmationEmail(EmailMessage emailMessage, Showtime showtime, List<TheatreSeat> seats, Ticket ticket) {
        emailMessage.setShowTime(showtime.getStartTime());
        emailMessage.setMovie(showtime.getMovie());
        emailMessage.setSeats(convertSeatsToString(seats));
        emailMessage.setTicketCode(ticket.getCode());
        CompletableFuture.runAsync(()-> emailService.sendSimpleMail(emailMessage));
    }


    public IResponse cancelTicket(String ticketCode, String emailAddress) {

        // Fetch the ticket by its ID
        Ticket ticket = ticketRepository.findByCode(ticketCode)
                .orElseThrow(() -> new CustomException("Ticket not found", HttpStatus.NOT_FOUND));

        if (!ticket.getEmail().equals(emailAddress)) {
            throw new CustomException("Ticket was not purchased by you!", HttpStatus.NOT_ACCEPTABLE);
        }

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
        List<Long> seatsIds = new ArrayList<>(ticket.getTicketSeats());
        log.debug("SEATS TO CLEAR SECOND {}", seatsIds);
        if (!ticket.getTicketSeats().isEmpty()) {
            theatreSeatRepository.deleteShowTimSeatsBySeatIds(ticket.getTicketSeats());
            ticket.getTicketSeats().clear();
        }
        log.debug("SEATS TO CLEAR THIRD {}", seatsIds);

        //Credit 85%, remove 15% as cancellation fee for Ordinary Users, but credit 100% for Registered Users
        String email = requestBean.getPrincipal(); //Only registered users have a email has token with email in header vaue
        float amount = 0F;
        if (StringUtils.isNotBlank(email)){
            // Create a promoCode with 100% of the ticket price
             amount = ticket.getMovie().getMoviePrice();
            refundCodeService.createRefundCode(amount, email);
        } else {
            // Create a promoCode with 85% of the ticket price
            amount = ticket.getMovie().getMoviePrice() * 0.85F;
            refundCodeService.createRefundCode(amount, ticket.getEmail());
        }

        // Save the updated ticket
        ticketRepository.save(ticket);
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setFirstName(ticket.getEmail());
        emailMessage.setRecipient(ticket.getEmail());
        emailMessage.setMessageType("EMAIL");
        emailMessage.setMessageBody("Ticket cancellation");
        emailMessage.setSubject("Ticket cancellation");
        emailMessage.setShowTime(ticket.getShowtime().getStartTime());
        emailMessage.setTheatre(ticket.getShowtime().getTheatre().getName());

        emailMessage.setTotalAmount("$ "+ String.format("%.2f", amount));
        var subType = MessageSubTypeEnum.TICKER_CANCELLATION;
        emailMessage.setMessageSubType(subType);

        log.debug("ALL SEAT IDs: {}", seatsIds);
        List<TheatreSeat> seats = theatreSeatRepository.findAllById(seatsIds);
        sendTicketConfirmationEmail(emailMessage, ticket.getShowtime(), seats, ticket);
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, "Ticket canceled successfully");
    }

    public String convertSeatsToString(List<TheatreSeat> seats) {
        if (seats == null || seats.isEmpty()) {
            return "";
        }

        return seats.stream()
                .map(seat -> seat.getSeatRow() + seat.getSeatNumber())
                .collect(Collectors.joining(", "));
    }

}
