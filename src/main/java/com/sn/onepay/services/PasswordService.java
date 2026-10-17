package com.sn.onepay.services;

import com.sn.onepay.dto.FirstPasswordChangeDTO;

public interface PasswordService {

    /*Remplace le mot de passe temporaire d'un compte (action requise UPDATE_PASSWORD) par un mot de passe définitif*/
    void changeTemporaryPassword(FirstPasswordChangeDTO firstPasswordChangeDTO);
}
