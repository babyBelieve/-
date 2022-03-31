package cn.itcast.wanxinp2p.consumer.agent;

import cn.itcast.wanxinp2p.api.account.model.AccountDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountRegisterDTO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import org.dromara.hmily.annotation.Hmily;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @FeignClient 设置要远程调用哪个服务
 */
@FeignClient(value="account-service")//远程调用account服务
public interface AccountApiAgent {

    @Hmily
    @PostMapping(value = "/account/l/accounts")//远程调用account服务的"register"用户注册方法
    RestResponse<AccountDTO> register(@RequestBody AccountRegisterDTO accountRegisterDTO);


}
