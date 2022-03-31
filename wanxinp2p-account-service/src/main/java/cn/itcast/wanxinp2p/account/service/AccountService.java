package cn.itcast.wanxinp2p.account.service;

import cn.itcast.wanxinp2p.account.entity.Account;
import cn.itcast.wanxinp2p.api.account.model.AccountDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountLoginDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountRegisterDTO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @className: 业务层接口
 * @description: TODO
 * @author: RuoNan.Wang
 * @create: 2022-03-17 15:39
 */
public interface AccountService extends IService<Account> {
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
     * @return 0表示手机号不存在,1表示存在
     */
    Integer checkMobile(String mobile,String key,String code);
    /***
     * 账户注册
     *@param registerDTO 注册信息
     *@return
     */
    AccountDTO register(AccountRegisterDTO registerDTO);

    /**
     * 用户登录
     * @param accountLoginDTO
     * @return
     */
    AccountDTO login(AccountLoginDTO accountLoginDTO);

}
