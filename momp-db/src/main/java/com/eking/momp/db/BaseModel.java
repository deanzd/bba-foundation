package com.eking.momp.db;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseModel<T extends BaseModel<?>> extends Model<T> {

	private static final long serialVersionUID = 1L;
	
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
	
    private boolean sysInit;

    private LocalDateTime createTime;

    private String createUser;

    private LocalDateTime updateTime;

    private String updateUser;

    private LocalDateTime deleted;
    
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
