package com.eking.momp.db.model;

import com.eking.momp.db.BaseModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author Dean
 * @since 2019-09-17
 */
@Getter
@Setter
public class User extends BaseModel<User> {

    private static final long serialVersionUID = 1L;

    private String username;

    private String password;

    private Integer roleId;

}
