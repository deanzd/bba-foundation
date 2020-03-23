package com.eking.momp.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -5340253161094235680L;

    private Integer userId;

}
