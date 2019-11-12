package com.runfast.gateway.enums;

public enum ResultCodeEnum {
    SUCCESS("成功"),
    FAIL("失败"),

    VALUE_IS_NULL("值为null"),
    VALUE_IS_EMPTY("值为空"),
    VALUE_IS_BLANK("值为空白"),
    PARAMETER_ERROR("参数错误"),
    NO_AUTHORIZED("没有访问权限"),
    SYSTEM_BUSY("系统繁忙，请稍后再试"),
    ACCOUNT_RELOGIN("未登录，请先登录"),
    ACCOUNT_USERNAME_OR_PASSWORD_ERROR("用户名或者密码错误"),
    ACCOUNT_NOT_EXIST("账号不存在"),
    FREQUENT_OPRATION("频繁操作"),
    DELIVER_COST_NOT_EXIST("配送模板不存在"),
    AGENT_NEAR_BY_NOT_EXIST("附近不存在商家"),
    BUSINESS_NEAR_BY_NOT_EXIST("更多商家接入中,敬请期待!"),
    PAY_REFUND_FAIL("退款失败"),
    PAY_PREPAY_FAIL("预下单失败"),
    ORDER_NOT_EXIST("订单不存在"),
    PAY_CHANNEL_NOT_SUPPORT("支付渠道目前不支持"),
    PAY_ORDER_QUERY_FAIL("订单查询失败"),
    PAY_STATUS_UNPAID("订单未支付"),
    ILLEGAL_OPERATION("非法操作"),
    STANDARD_NOT_EXIST("规格不存在"),
    GOODS_NOT_EXIST("商品不存在"),
    SUB_OPTION_NOT_EXIST("子选项不存在"),
    BUSINESS_NOT_EXIST("商家不存在"),
    MINI_ACCOUNT_NOT_BIND("小程序账户没有绑定"),
    WECHAT_ACCOUNT_NOT_BIND("微信公众号账户没有绑定"),
    UNABLE_TO_OBTAIN("无法获取"),
    CONTENT_TYPE_ERROR("Content-Type错误"),
    REQUEST_METHOD_ERROR("请求method错误"),
    ACCECT_TOKEN_ERROR("token错误或过期"),
    CLIENTID_ERROR("clientId错误"),
    TIMESTAMP_ERROR("请求时间过期"),
    SIGN_ERROR("签名无效"),
    ;

    private String description;

    private ResultCodeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
