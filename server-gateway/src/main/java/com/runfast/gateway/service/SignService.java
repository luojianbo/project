package com.runfast.gateway.service;

import com.runfast.gateway.vo.ResultVo;

/**
 *  签名验证接口，验证签名sign的合法性
 * @author luojianbo
 * @date 2019/6/5
 */
public interface SignService {
    public ResultVo check();
}
