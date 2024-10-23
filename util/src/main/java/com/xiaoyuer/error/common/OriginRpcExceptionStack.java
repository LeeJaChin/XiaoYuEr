package com.xiaoyuer.error.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class OriginRpcExceptionStack implements Serializable {

    private String msg;

    private String errorStack;
}
