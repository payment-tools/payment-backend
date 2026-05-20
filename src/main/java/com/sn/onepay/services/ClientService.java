package com.sn.onepay.services;

import com.sn.onepay.dto.ClientDTO;
import com.sn.onepay.enumeration.Roles;
import com.sn.onepay.enumeration.StateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ClientService {

    ClientDTO createClient(ClientDTO clientDTO);

    ClientDTO updateClient(ClientDTO clientDTO, Long clientId);

    void deleteClient(Long clientId);

    Page<ClientDTO> getClientsByFilters(Long id, String ref, String firstname, String lastname, String username, String email, String phoneNumber, Roles role, Long enterpriseId, StateStatus status, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);
}
