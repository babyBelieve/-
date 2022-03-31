package cn.itcast.wanxinp2p.account.service;

import cn.itcast.wanxinp2p.account.common.AccountErrorCode;
import cn.itcast.wanxinp2p.common.domain.BusinessException;
import cn.itcast.wanxinp2p.common.domain.CommonErrorCode;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import cn.itcast.wanxinp2p.common.util.OkHttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @className: 跟验证码有关的
 * @description: TODO
 * @author: RuoNan.Wang
 * @create: 2022-03-17 15:42
 */
@Service
public class SmsService {

    //取Apollo里面配置的url
    @Value("${sms.url}")
    private String smsURL;

    @Value("${sms.enable}")
    private Boolean smsEnable;

    /**
     * 发送并获取手机号验证码
     * @param mobile 手机号
     * @return
     */
    public RestResponse getSmsCode(String mobile) {
        if (smsEnable){ //是true,表示开启验证码功能
            return OkHttpUtil.post(smsURL + "/generate?effectiveTime=300&name=sms", "{\"mobile\":" + mobile + "}");        }
        //关闭的话走密码登录模式
        return RestResponse.success();
    }

    /**
     * 效验验证码
     * @param key 效验标识   redis中的键
     * @param code 短信验证码
     */
    //验证不通过直接抛异常
    public void verifySmsCode(String key,String code){
        if (smsEnable) {
            StringBuilder params = new StringBuilder("/verify?name=sms");
            //拼接字符串
            params.append("&verificationKey=").append(key).append("&verificationCode=").append(code);
            //参数一: 请求地址
            RestResponse smsResponse = OkHttpUtil.post(smsURL + params, "");
            //不等于0 或者 false 就抛出验证码错误异常
            if (smsResponse.getCode() != CommonErrorCode.SUCCESS.getCode() ||
                    smsResponse.getResult().toString().equalsIgnoreCase("false")) {
                throw new BusinessException(AccountErrorCode.E_140152);//验证码错误
            }
        }
    }
}
