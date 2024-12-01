package com.uofc.acmeplex.controllers;

import com.uofc.acmeplex.dto.request.ticket.TicketRequest;
import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.logic.ITicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("ticket")
public class TicketController {

    private final ITicketService ticketService;

    @GetMapping("all")
    public IResponse displayTickets(Pageable pageable) {
        return ticketService.fetchTicket(pageable);
    }

    @GetMapping("code")
    public IResponse retrieveTicketByCode(@RequestParam(name = "ticketCode") String ticketCode){
        return ticketService.retrieveTicketByCode(ticketCode);
    }

    @PostMapping("issue")
    public IResponse issueTicket(@RequestBody @Valid TicketRequest request) {
        return ticketService.issueTicket(request);
    }

    @GetMapping("cancel")
    public IResponse cancelTicket(@RequestParam(name = "ticketCode") String ticketCode, @RequestParam(name = "email") String email) {
        return ticketService.cancelTicket(ticketCode, email);
    }
}
