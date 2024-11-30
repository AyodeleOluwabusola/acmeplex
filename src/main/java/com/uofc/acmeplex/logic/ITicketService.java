package com.uofc.acmeplex.logic;

import com.uofc.acmeplex.dto.request.ticket.TicketRequest;
import com.uofc.acmeplex.dto.response.IResponse;
import org.springframework.data.domain.Pageable;

public interface ITicketService {

    //SOLID principle: Dependency Inversion (Classes depending on abstractions)
    IResponse fetchTicket(Pageable pageable);
    IResponse retrieveTicketByCode(String code);
    IResponse issueTicket(TicketRequest ticketRequest);
    IResponse cancelTicket(String ticketCode, String email) ;
}
