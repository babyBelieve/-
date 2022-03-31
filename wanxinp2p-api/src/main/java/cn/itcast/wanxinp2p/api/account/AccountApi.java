package cn.itcast.wanxinp2p.api.account;

import cn.itcast.wanxinp2p.api.account.model.AccountDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountLoginDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountRegisterDTO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;

/**
 * @className: 所有接口都写在api中
 * @description: TODO
 * @author: RuoNan.Wang
 * @create: 2022-03-17 15:27
 */
public interface AccountApi {
    /**
     * 获取手机号验证码
     * @param mobile 手机号
     * @return
     */
    RestResponse getSMSCode(String mobile);

    /**
     * 效验手机号和验证码
     * @param mobile 手机号
     * @param key 效验标识 sms:...
     * @param code 验证码
     * @return
     */
    RestResponse<Integer> checkMobile(String mobile, String key,String code);

    /**
     * 注册 保存信息
     * @param accountRegisterDTO 封装前端传来的注册信息
     * @return AccountDTO 返回给前端的数据
     */
    RestResponse<AccountDTO> register(AccountRegisterDTO accountRegisterDTO);

    /**
     * 用户登录
     * @param accountLoginDTO 封装用户登录信息
     * @return
     */
    RestResponse<AccountDTO> login(AccountLoginDTO accountLoginDTO);
}
