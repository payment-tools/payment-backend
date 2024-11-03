package com.sn.onepay.services;

import com.sn.onepay.dto.ClientDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {

    ClientDTO createClient(ClientDTO clientDTO);

    ClientDTO updateClient(ClientDTO clientDTO, Long clientId);

    void deleteClient(Long clientId);

    Page<ClientDTO> getClientsByFilter(ClientDTO clientDTO, Pageable pageable);
}
