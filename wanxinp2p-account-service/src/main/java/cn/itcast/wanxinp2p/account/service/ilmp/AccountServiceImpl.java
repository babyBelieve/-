package cn.itcast.wanxinp2p.account.service.ilmp;

import cn.itcast.wanxinp2p.account.common.AccountErrorCode;
import cn.itcast.wanxinp2p.account.entity.Account;
import cn.itcast.wanxinp2p.account.mapper.AccountMapper;
import cn.itcast.wanxinp2p.account.service.AccountService;
import cn.itcast.wanxinp2p.account.service.SmsService;
import cn.itcast.wanxinp2p.api.account.model.AccountDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountLoginDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountRegisterDTO;
import cn.itcast.wanxinp2p.common.domain.BusinessException;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import cn.itcast.wanxinp2p.common.domain.StatusCode;
import cn.itcast.wanxinp2p.common.util.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.Hmily;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @className: 业务层实现类
 * @description: TODO
 * @author: RuoNan.Wang
 * @create: 2022-03-17 15:41
 */
@Slf4j
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
    @Autowired
    private SmsService smsService;

    @Value("${sms.enable}")
    private Boolean smsEnable;

    /**
     * 获取手机号验证码
     * @param mobile 手机号
     * @return
     */
    @Override
    public RestResponse getSMSCode(String mobile) {
        return smsService.getSmsCode(mobile);
    }

    /**
     * 效验手机号和验证码
     * @param mobile 手机号
     * @param key    效验标识 sms:2304de4d9e4f469a9593108d83bcfff3
     * @param code   验证码
     * @return 0表示手机号不存在, 1表示存在
     */
    @Override
    public Integer checkMobile(String mobile, String key, String code) {
        smsService.verifySmsCode(key, code);//效验验证码
        // 查记录数: 查不出来就是0. 查出来是1 :因为手机号不允许重复
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        //方法一 : wrapper.eq("mobile",mobile);
        //方法二: lambda表达式
        wrapper.lambda().eq(Account::getMobile, mobile);
        int count = count(wrapper);
        return count > 0 ? 1 : 0; //三元运算
    }

    /**
     * 账户注册
     * @param registerDTO 注册信息
     * @return
     */
    @Override
    @Hmily(confirmMethod = "confirmRegister", cancelMethod = "cancelRegister")//解决分布式事务
    //这里需要注意DTO和DO的区别，请参考“万信金融p2p项目开发规范.pdf”
    public AccountDTO register(AccountRegisterDTO registerDTO) {
        Account account = new Account();
        account.setUsername(registerDTO.getUsername());
        account.setMobile(registerDTO.getMobile());
        account.setPassword(PasswordUtil.generate(registerDTO.getPassword()));
        if (smsEnable) {
            account.setPassword(PasswordUtil.generate(account.getMobile()));
        }
        account.setDomain("c");
        account.setStatus(StatusCode.STATUS_OUT.getCode());
        save(account);//调用MP的save方法
        return convertAccountEntityToDTO(account);
    }

    public void confirmRegister(AccountRegisterDTO registerDTO) {
        log.info("execute confirmRegister");
    }

    public void cancelRegister(AccountRegisterDTO registerDTO) {//注册失败,执行回滚操作
        log.info("execute cancelRegister"); //删除账号
        remove(Wrappers.<Account>lambdaQuery().eq(Account::getUsername, registerDTO.getUsername()));
    }

    /**
     * 用户登录
     * @param accountLoginDTO
     * @return
     */
    @Override
    public AccountDTO login(AccountLoginDTO accountLoginDTO) {
        //1.根据用户名和密码进行一次查询
        //2.先根据用户名进行查询，然后再比对密码
        Account account = null;
        if (accountLoginDTO.getDomain().equalsIgnoreCase("c")) {
            //如果是c端用户，用户名就是手机号
            account = getAccountByMobile(accountLoginDTO.getMobile());
        } else {
            //如果是b端用户，用户名就是账号
            account = getAccountByUsername(accountLoginDTO.getUsername());
        }
        if (account == null) {
            throw new BusinessException(AccountErrorCode.E_130104);
        }

        AccountDTO accountDTO = convertAccountEntityToDTO(account);
        if (smsEnable) { //如果为true,表示采用短信验证码登录，无需比较密码
            return accountDTO;
        }

        if (PasswordUtil.verify(accountLoginDTO.getPassword(), account.getPassword())) {
            return accountDTO;
        }
        throw new BusinessException(AccountErrorCode.E_130105);
    }

    /**
     * 根据手机获取账户信息
     * @param mobile 手机号
     * @return 账户实体
     */
    private Account getAccountByMobile(String mobile) {
        return getOne(new QueryWrapper<Account>().lambda()
                .eq(Account::getMobile, mobile));
    }

    /**
     * 根据用户名获取账户信息
     * @param username 用户名
     * @return 账户实体
     */
    private Account getAccountByUsername(String username) {
        return getOne(new QueryWrapper<Account>().lambda()
                .eq(Account::getUsername, username));
    }

    /**
     * entity转为dto
     * @param entity
     * @return
     */
    private AccountDTO convertAccountEntityToDTO(Account entity) {
        if (entity == null) {
            return null;
        }
        AccountDTO dto = new AccountDTO();
        BeanUtils.copyProperties(entity, dto);//把参数一对象属性的值,赋给参数2对象
        return dto;
    }
}
