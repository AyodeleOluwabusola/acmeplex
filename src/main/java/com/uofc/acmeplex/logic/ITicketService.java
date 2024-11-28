package com.uofc.acmeplex.logic;

import com.uofc.acmeplex.dto.request.ticket.TicketRequest;
import com.uofc.acmeplex.dto.response.IResponse;

public interface ITicketService {

    //SOLID principle: Dependency Inversion (Classes depending on abstractions)
    IResponse issueTicket(TicketRequest ticketRequest);
    IResponse cancelTicket(String ticketCode, String email) ;
}
