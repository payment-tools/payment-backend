package com.sn.onepay.utils;

public interface IMapper<E, D> {

    D asDTO(E entity);

    E asEntity(D dto);
}
