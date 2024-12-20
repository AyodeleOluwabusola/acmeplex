package com.uofc.acmeplex.logic.services;

import com.uofc.acmeplex.dto.request.mail.EmailMessage;
import com.uofc.acmeplex.dto.request.ticket.TicketRequest;
import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.dto.response.ResponseCodeEnum;
import com.uofc.acmeplex.dto.response.ResponseData;
import com.uofc.acmeplex.entities.RefundCode;
import com.uofc.acmeplex.entities.ShowtimeSeat;
import com.uofc.acmeplex.entities.Showtime;
import com.uofc.acmeplex.entities.TheatreSeat;
import com.uofc.acmeplex.entities.Ticket;
import com.uofc.acmeplex.enums.BookingStatusEnum;
import com.uofc.acmeplex.enums.MessageSubTypeEnum;
import com.uofc.acmeplex.exception.CustomException;
import com.uofc.acmeplex.logic.ITicketService;
import com.uofc.acmeplex.repository.InvoiceRepository;
import com.uofc.acmeplex.repository.RefundCodeRepository;
import com.uofc.acmeplex.repository.ShowTimeRepository;
import com.uofc.acmeplex.repository.ShowTimeSeatsRepository;
import com.uofc.acmeplex.repository.TheatreSeatRepository;
import com.uofc.acmeplex.repository.TicketRepository;
import com.uofc.acmeplex.security.RequestBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final ShowTimeRepository showtimeRepository;

    private final TheatreSeatRepository theatreSeatRepository;

    private final NotificationService notificationService;

    private final TicketRepository ticketRepository;

    private final RefundCodeService refundCodeService;

    private final RefundCodeRepository refundCodeRepository;

    private final InvoiceRepository invoiceRepository;

    private final ShowTimeSeatsRepository showTimeSeatsRepository;

    private final RequestBean requestBean;

    @Override
    public IResponse fetchTicket(Pageable pageable) {
        Page<Ticket> allTickets = ticketRepository.findAllByEmail(pageable, requestBean.getPrincipal());
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, allTickets.getContent());
    }

    @Override
    public IResponse retrieveTicketByCode(String code) {
        Ticket ticket = ticketRepository.findByCode(code)
                .orElseThrow(() -> new CustomException("Ticket not found", HttpStatus.NOT_FOUND));
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, ticket);
    }

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
        List<Long> alreadyReservedSeats = theatreSeatRepository.findExistingReservedTheatreSeats(request.getSeatIds(), request.getShowtimeId());
        if (!alreadyReservedSeats.isEmpty()) {
            throw new CustomException("One more selected seats(s) was already reserved", HttpStatus.BAD_REQUEST);
        }

        float refundBalance = 0F;
        RefundCode refundCode = null;
        if (StringUtils.isNotBlank(request.getRefundCode())) {
            refundCode = refundCodeRepository.findByCode(request.getRefundCode())
                    .orElseThrow(() -> new CustomException("Refund code not found", HttpStatus.NOT_FOUND));
            refundBalance = refundCode.getBalance();
        }

        Float amountPaid = 0F;
        if (StringUtils.isNotBlank(request.getPaymentReference())) {
             amountPaid = invoiceRepository.findByPaymentReference(request.getPaymentReference())
                    .orElseThrow(() -> new CustomException("Payment reference not found", HttpStatus.NOT_FOUND))
                     .getAmount();
        }

        if ((refundBalance + amountPaid / showtime.getShowtimeSeats().size()) < showtime.getMovie().getMoviePrice()) {
            throw new CustomException("Insufficient funds to purchase movie ticket", HttpStatus.BAD_REQUEST);
        }

        if (refundCode != null) {
            refundBalance = Math.max(0, refundBalance - showtime.getMovie().getMoviePrice());
            refundCode.setBalance(refundBalance);
            refundCodeRepository.save(refundCode);
        }

        // Create and save ticket
        Ticket ticket = new Ticket();
        ticket.setEmail(request.getEmail());
        ticket.setShowtime(showtime);

        // Assign the seats to the ticket
        for (TheatreSeat seat : seats) {
            ShowtimeSeat showtimeSeat = new ShowtimeSeat();
            showtimeSeat.setShowtime(showtime);
            showtimeSeat.setTheatreSeat(seat);
            showTimeSeatsRepository.save(showtimeSeat);
        }

        ticket.setTicketSeats(seats.stream().map(TheatreSeat::getId).toList());
        ticket.setBookingStatus(BookingStatusEnum.RESERVED);
        ticket.setCode("TKT-" +refundCodeService.generateUniqueCode());

        //Send ticket confirmation Email
        sendTicketConfirmationEmail(showtime, seats, ticket);
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, ticketRepository.save(ticket));
    }

    private void sendTicketConfirmationEmail(Showtime showtime, List<TheatreSeat> seats, Ticket ticket) {

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setFirstName(ticket.getEmail());
        emailMessage.setRecipient(ticket.getEmail());
        emailMessage.setMessageType("EMAIL");
        emailMessage.setTheatre(ticket.getShowtime().getTheatre().getName());
        var subType = MessageSubTypeEnum.TICKET_PURCHASE;
        emailMessage.setMessageSubType(subType);
        emailMessage.setMessageBody("Ticket purchase");
        emailMessage.setSubject("Ticket purchase");
        emailMessage.setShowTime(showtime.getStartTime());
        emailMessage.setMovie(showtime.getMovie());
        emailMessage.setSeats(convertSeatsToString(seats));
        emailMessage.setTicketCode(ticket.getCode());
        CompletableFuture.runAsync(()-> notificationService.sendSimpleMail(emailMessage));
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
        if (ChronoUnit.HOURS.between(currentTime, showtimeDateTime) < 72) {
            throw new CustomException("Cannot cancel ticket less than 72 hours before showtime", HttpStatus.BAD_REQUEST);
        }

        // Change the status to CANCELED
        ticket.setBookingStatus(BookingStatusEnum.CANCELLED);

        // Free up the seats
        log.debug("SEATS TO CLEAR {}", ticket.getTicketSeats());
        List<Long> seatsIds = new ArrayList<>(ticket.getTicketSeats());
        if (!ticket.getTicketSeats().isEmpty()) {
            theatreSeatRepository.deleteShowTimSeatsBySeatIds(ticket.getTicketSeats());
            ticket.getTicketSeats().clear();
        }

        //Credit 85%, remove 15% as cancellation fee for Ordinary Users, but credit 100% for Registered Users
        String email = requestBean.getPrincipal(); //Only registered users has token with email in header
        float amount = 0F;
        RefundCode refundCode = null;
        if (StringUtils.isNotBlank(email)){
            // Create a promoCode with 100% of the ticket price
             amount = ticket.getShowtime().getMovie().getMoviePrice() * seatsIds.size();
            refundCode= refundCodeService.createRefundCode(amount, email);
        } else {
            // Create a promoCode with 85% of the ticket price
            amount = ticket.getShowtime().getMovie().getMoviePrice() * seatsIds.size() * 0.85F;
            refundCode = refundCodeService.createRefundCode(amount, ticket.getEmail());
        }

        // Save the updated ticket
        ticketRepository.save(ticket);

        // Send ticket Cancellation Email
        sendTicketCancellationEmail(refundCode.getCode(), amount, ticket.getShowtime(), seatsIds, ticket);
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

    private void sendTicketCancellationEmail(String code, Float amount, Showtime showtime, List<Long> seatsIds, Ticket ticket) {

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setFirstName(ticket.getEmail());
        emailMessage.setRecipient(ticket.getEmail());
        emailMessage.setMessageType("EMAIL");
        emailMessage.setShowTime(ticket.getShowtime().getStartTime());
        emailMessage.setTheatre(ticket.getShowtime().getTheatre().getName());
        emailMessage.setRefundCode(code);
        emailMessage.setTotalAmount("$ "+ String.format("%.2f", amount));
        var subType = MessageSubTypeEnum.TICKER_CANCELLATION;
        emailMessage.setMessageSubType(subType);

        log.debug("ALL SEAT IDs: {}", seatsIds);
        List<TheatreSeat> seats = theatreSeatRepository.findAllById(seatsIds);

        emailMessage.setMessageBody("Ticket cancellation");
        emailMessage.setSubject("Ticket cancellation");
        emailMessage.setShowTime(showtime.getStartTime());
        emailMessage.setMovie(showtime.getMovie());
        emailMessage.setSeats(convertSeatsToString(seats));
        emailMessage.setTicketCode(ticket.getCode());
        CompletableFuture.runAsync(()-> notificationService.sendSimpleMail(emailMessage));
    }

}
