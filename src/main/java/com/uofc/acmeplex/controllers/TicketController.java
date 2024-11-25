package com.uofc.acmeplex.controllers;

import com.uofc.acmeplex.dto.request.ticket.TicketRequest;
import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.logic.ITicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("issue")
    public IResponse issueTicket(@RequestBody @Valid TicketRequest request) {
        return ticketService.issueTicket(request);
    }

    @GetMapping("cancel")
    public IResponse cancelTicket(@RequestParam(name = "ticketId") Long ticketId) {
        return ticketService.cancelTicket(ticketId);
    }
}
