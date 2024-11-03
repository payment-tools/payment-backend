package com.sn.onepay.services.impl;

import com.sn.onepay.dto.ClientDTO;
import com.sn.onepay.services.ClientService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {
    @Override
    public ClientDTO createClient(ClientDTO clientDTO) {
        return null;
    }

    @Override
    public ClientDTO updateClient(ClientDTO clientDTO, Long clientId) {
        return null;
    }

    @Override
    public void deleteClient(Long id) {

    }

    @Override
    public Page<ClientDTO> getClientsByFilter(ClientDTO clientDTO, Pageable pageable) {
        return null;
    }
}
