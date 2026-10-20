package com.sn.onepay.utils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.sn.onepay.entity.QCashier;
import com.sn.onepay.entity.QClient;
import com.sn.onepay.entity.QEnterpriseProfile;
import com.sn.onepay.entity.QSalesProfile;
import com.sn.onepay.enumeration.PlatformUserType;
import com.sn.onepay.exceptions.ResourceAlreadyExistException;
import com.sn.onepay.repository.CashierRepository;
import com.sn.onepay.repository.ClientRepository;
import com.sn.onepay.repository.EnterpriseProfileRepository;
import com.sn.onepay.repository.SalesProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import static com.sn.onepay.enumeration.PlatformUserType.CASHIER;
import static com.sn.onepay.enumeration.PlatformUserType.CLIENT;
import static com.sn.onepay.enumeration.PlatformUserType.ENTERPRISE_PROFILE;
import static com.sn.onepay.enumeration.PlatformUserType.SALES_PROFILE;

/*Username, e-mail et téléphone sont uniques sur tous les comptes (Client, Cashier, EnterpriseProfile, SalesProfile)*/
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountUniquenessChecker {

    static final QClient CLIENT_PATH = QClient.client;
    static final QCashier CASHIER_PATH = QCashier.cashier;
    static final QEnterpriseProfile ENTERPRISE_PROFILE_PATH = QEnterpriseProfile.enterpriseProfile;
    static final QSalesProfile SALES_PROFILE_PATH = QSalesProfile.salesProfile;

    final ClientRepository clientRepository;
    final CashierRepository cashierRepository;
    final EnterpriseProfileRepository enterpriseProfileRepository;
    final SalesProfileRepository salesProfileRepository;

    /*Champ null : non vérifié. selfId : fiche modifiée, exclue de la vérification (null à la création)*/
    public void checkAvailable(PlatformUserType type, Long selfId, String username, String email, String phoneNumber) {
        // Keycloak ne distingue pas la casse des usernames et des e-mails : la vérification non plus
        if (username != null && taken(type, selfId,
                CLIENT_PATH.username.equalsIgnoreCase(username),
                CASHIER_PATH.username.equalsIgnoreCase(username),
                ENTERPRISE_PROFILE_PATH.username.equalsIgnoreCase(username),
                SALES_PROFILE_PATH.username.equalsIgnoreCase(username))) {
            throw new ResourceAlreadyExistException("Ce username est déjà utilisé par un autre compte : " + username);
        }
        if (email != null && taken(type, selfId,
                CLIENT_PATH.email.equalsIgnoreCase(email),
                CASHIER_PATH.email.equalsIgnoreCase(email),
                ENTERPRISE_PROFILE_PATH.email.equalsIgnoreCase(email),
                SALES_PROFILE_PATH.email.equalsIgnoreCase(email))) {
            throw new ResourceAlreadyExistException("Cet e-mail est déjà utilisé par un autre compte : " + email);
        }
        String phone = PhoneNumbers.normalize(phoneNumber);
        if (phone != null && taken(type, selfId,
                CLIENT_PATH.phoneNumber.eq(phone),
                CASHIER_PATH.phoneNumber.eq(phone),
                ENTERPRISE_PROFILE_PATH.phoneNumber.eq(phone),
                SALES_PROFILE_PATH.phoneNumber.eq(phone))) {
            throw new ResourceAlreadyExistException("Ce numéro de téléphone est déjà utilisé par un autre compte : " + phone);
        }
    }

    private boolean taken(PlatformUserType type, Long selfId, BooleanExpression client, BooleanExpression cashier,
                          BooleanExpression enterpriseProfile, BooleanExpression salesProfile) {
        return clientRepository.exists(excludeSelf(client, type == CLIENT, CLIENT_PATH.id, selfId))
                || cashierRepository.exists(excludeSelf(cashier, type == CASHIER, CASHIER_PATH.id, selfId))
                || enterpriseProfileRepository.exists(excludeSelf(enterpriseProfile, type == ENTERPRISE_PROFILE, ENTERPRISE_PROFILE_PATH.id, selfId))
                || salesProfileRepository.exists(excludeSelf(salesProfile, type == SALES_PROFILE, SALES_PROFILE_PATH.id, selfId));
    }

    private static BooleanExpression excludeSelf(BooleanExpression expression, boolean sameType, NumberPath<Long> id, Long selfId) {
        return sameType && selfId != null ? expression.and(id.ne(selfId)) : expression;
    }
}
