package cn.itcast.wanxinp2p.api.consumer;

import cn.itcast.wanxinp2p.api.consumer.model.ConsumerRegisterDTO;
import cn.itcast.wanxinp2p.api.consumer.model.ConsumerRequest;
import cn.itcast.wanxinp2p.api.depository.GatewayRequest;
import cn.itcast.wanxinp2p.common.domain.RestResponse;

/**
 * 用户中心接口API
 */
public interface ConsumerAPI {

    /**
     * 用户注册  保存用户信息
     * @param consumerRegisterDTO 封装前端传来的注册信息
     * @return
     */
   RestResponse register(ConsumerRegisterDTO consumerRegisterDTO);
    /***
     * 生成开户请求数据
     * * @param consumerRequest 前端发来的开户信息
     * * @return GatewayRequest 封装返回的数据
     * */
    RestResponse<GatewayRequest> createConsumer(ConsumerRequest consumerRequest);

}
