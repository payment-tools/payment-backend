package com.sn.onepay.services;

import com.sn.onepay.dto.ClientCreateDTO;
import com.sn.onepay.dto.ClientDTO;
import com.sn.onepay.dto.ClientUpdateDTO;
import com.sn.onepay.enumeration.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ClientService {

    ClientDTO createClient(ClientCreateDTO clientCreateDTO);

    ClientDTO updateClient(ClientUpdateDTO clientUpdateDTO, Long clientId);

    void deleteClient(Long clientId);

    Page<ClientDTO> getClientsByFilters(Long id, String ref, String firstname, String lastname, String username, String email, String phoneNumber, Roles role, Long enterpriseId, Boolean active, LocalDateTime creationDate, LocalDateTime modificationDate, Pageable pageable);

    ClientDTO getClientById(Long id);
}
