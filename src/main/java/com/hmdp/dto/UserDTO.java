package com.hmdp.dto;

import lombok.Data;


/**
 * 对session保存的用户User信息进行细粒度控制
 */
@Data
public class UserDTO {
    private Long id;
    private String nickName;
    private String icon;
}
