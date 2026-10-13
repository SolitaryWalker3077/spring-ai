package com.mcp.stdio.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfo {
    private String name;
    private Integer age;
    private String sex;
    private String address;
}
