package cn.itcast.wanxinp2p.account.controller;

import cn.itcast.wanxinp2p.account.service.AccountService;
import cn.itcast.wanxinp2p.api.account.AccountApi;
import cn.itcast.wanxinp2p.api.account.model.AccountDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountLoginDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountRegisterDTO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 表现层/控制层
 */
@Slf4j
@RestController
@Api(value = "统一账号服务", tags = "Account", description = "统一账号服务API")
public class AccountController implements AccountApi {

    @Autowired
    private AccountService accountService;

    @ApiOperation("获取手机验证码")
    @ApiImplicitParam(name = "mobile",value = "手机号",dataType = "String")
    @GetMapping("/sms/{mobile}")
    @Override
    public RestResponse getSMSCode(@PathVariable String mobile) {
        return accountService.getSMSCode(mobile);
    }
    /**
     * 效验手机号和验证码
     * @param mobile 手机号
     * @param key 效验标识 sms:...
     * @param code 验证码
     * @return
     */
    @ApiOperation("校验手机号和验证码")
    @ApiImplicitParams({@ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "key", value = "校验标识", required = true, dataType = "String"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, dataType = "String")})
    @GetMapping(value = "/mobiles/{mobile}/key/{key}/code/{code}")
    @Override
    public RestResponse<Integer> checkMobile(@PathVariable String mobile,@PathVariable String key,@PathVariable String code) {
        return RestResponse.success(accountService.checkMobile(mobile,key,code));
    }
    /**
     * 注册 保存信息
     * @param accountRegisterDTO 封装前端传来的注册信息
     * @return AccountDTO 返回给前端的数据
     * 由于在注册功能中，Account服务会被Consumer服务调用
     */

    @ApiOperation("用户注册")
    @ApiImplicitParam(name = "accountRegisterDTO", value = "账户注册信息", required = true, dataType = "AccountRegisterDTO", paramType = "body")
    @PostMapping(value = "/l/accounts")//不暴露给前端的,l开头
    public RestResponse<AccountDTO> register(@RequestBody AccountRegisterDTO accountRegisterDTO) {
        return RestResponse.success(accountService.register(accountRegisterDTO));
    }
    /**
     * 用户登录
     * @param accountLoginDTO 封装用户登录信息
     * @return
     */
    @ApiOperation("用户登录")
    @ApiImplicitParam(name = "accountLoginDTO",value = "登录信息",required = true,dataType = "AccountLoginDTO",paramType = "body")
    // "l"表示这个请求不是直接暴露给前端访问的
    @PostMapping(value = "/l/accounts/session")
    @Override
    public RestResponse<AccountDTO> login(@RequestBody AccountLoginDTO accountLoginDTO) {
        return RestResponse.success(accountService.login(accountLoginDTO));
    }

}
