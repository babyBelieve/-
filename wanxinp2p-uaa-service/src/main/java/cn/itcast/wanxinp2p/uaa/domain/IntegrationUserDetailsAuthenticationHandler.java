package cn.itcast.wanxinp2p.uaa.domain;

import cn.itcast.wanxinp2p.api.account.model.AccountDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountLoginDTO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import cn.itcast.wanxinp2p.common.util.StringUtil;
import cn.itcast.wanxinp2p.uaa.agent.AccountApiAgent;
import cn.itcast.wanxinp2p.uaa.common.utils.ApplicationContextHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class IntegrationUserDetailsAuthenticationHandler {//这个类没有直接交给spring进行管理

	/**
	 * 认证处理
	 * @param domain 用户域 ，如b端用户、c端用户等
	 * @param authenticationType  认证类型，如密码认证，短信认证等
	 * @param token SpringSecurity的token对象，可提取用户名、密码等用于认证的信息
	 * @return UnifiedUserDetails 登录成功
	 *
	 * 参数说明：
	 * 			domain: 用户域 ，说明了此次登录的是b端用户还是c端用户，由接入方传入
	 * 			authenticationType: 说明了此次登录的方式，如密码认证，短信认证等，由接入方传入
	 * 			token: SpringSecurity的token对象，里面存放了接入方调用UAA接口进行认证时传入的用户名、
	 * 			密码等需要验证的信息
	 * 返回值说明：
	 * 		UnifiedUserDetails SpringSecurity对象，用于存放登录成功时返回的信息，比如账号基本信
	 * 		息、权限、资源等。此方法返回该对象给Spring Security OAuth2框架，Spring Security OAuth2
	 * 		框架会"根据里面的内容生成jwt令牌"，从而使令牌中保存了登录用户的相关数据。
	 *
	 * 	UnifiedUserDetails: 登录成功后才会返回这个,用来封装用户的基本信息,框架会自动生成jwt令牌,令牌中保存了用户的基本信息
	 */
	public UnifiedUserDetails authentication(String domain, String authenticationType,
			UsernamePasswordAuthenticationToken token) {//token在这里只是个参数名,里面有前端传来的账号密码
		//1.从客户端取数据
		String username = token.getName(); //取账号
		if(StringUtil.isBlank(username)){
			throw  new BadCredentialsException("账户为空");
		}
		if(token.getCredentials() == null){
			throw  new BadCredentialsException("密码为空");
		}
		//取密码:密码是string,所以要用toString把密码转换成string类型
		String presentedPassword = token.getCredentials().toString();//getCredentials 如果不为空就使用toString转成String类型
		//2.远程调用统一账户服务，进行账户密码校验
		AccountLoginDTO accountLoginDTO = new AccountLoginDTO();//把需要传过去的数据封装成dto对象
		accountLoginDTO.setDomain(domain);
		accountLoginDTO.setUsername(username);//账号
		accountLoginDTO.setMobile(username);
		accountLoginDTO.setPassword(presentedPassword);//密码
		AccountApiAgent accountApiAgent = (AccountApiAgent) ApplicationContextHelper.getBean(AccountApiAgent.class);//得到AccountApiAgent代理对象

		RestResponse<AccountDTO> restResponse = accountApiAgent.login(accountLoginDTO); //返回的数据都在这里
		//3.异常处理
		if(restResponse.getCode() != 0){
			throw new BadCredentialsException("登录失败");
		}
		//4.登录成功，把用户数据封装到UnifiedUserDetails对象中
		//把 账号密码,手机号..等权限信息 封装到UnifiedUserDetails
		UnifiedUserDetails unifiedUserDetails=new UnifiedUserDetails(restResponse.getResult().getUsername(),presentedPassword,AuthorityUtils.createAuthorityList());
		unifiedUserDetails.setMobile(restResponse.getResult().getMobile());
		return unifiedUserDetails;
	}

	private UnifiedUserDetails getUserDetails(String username) {
		Map<String, UnifiedUserDetails> userDetailsMap = new HashMap<>();
		userDetailsMap.put("admin",
				new UnifiedUserDetails("admin", "111111", AuthorityUtils.createAuthorityList("ROLE_PAGE_A", "PAGE_B")));
		userDetailsMap.put("xufan",
				new UnifiedUserDetails("xufan", "111111", AuthorityUtils.createAuthorityList("ROLE_PAGE_A", "PAGE_B")));

		userDetailsMap.get("admin").setDepartmentId("1");
		userDetailsMap.get("admin").setMobile("18611106983");
		userDetailsMap.get("admin").setTenantId("1");
		Map<String, List<String>> au1 = new HashMap<>();
		au1.put("ROLE1", new ArrayList<>());
		au1.get("ROLE1").add("p1");
		au1.get("ROLE1").add("p2");
		userDetailsMap.get("admin").setUserAuthorities(au1);
		Map<String, Object> payload1 = new HashMap<>();
		payload1.put("res", "res1111111");
		userDetailsMap.get("admin").setPayload(payload1);
		userDetailsMap.get("xufan").setDepartmentId("2");
		userDetailsMap.get("xufan").setMobile("18611106984");
		userDetailsMap.get("xufan").setTenantId("1");
		Map<String, List<String>> au2 = new HashMap<>();
		au2.put("ROLE2", new ArrayList<>());
		au2.get("ROLE2").add("p3");
		au2.get("ROLE2").add("p4");
		userDetailsMap.get("xufan").setUserAuthorities(au2);
		Map<String, Object> payload2 = new HashMap<>();
		payload2.put("res", "res222222");
		userDetailsMap.get("xufan").setPayload(payload2);
		return userDetailsMap.get(username);

	}

}
