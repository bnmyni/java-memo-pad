package com.scoco.sample.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum CacheEnum {

    user(35, "user_"), dict(40, "dict_");

    private Integer ttl;

    private String prefixKey;

}
