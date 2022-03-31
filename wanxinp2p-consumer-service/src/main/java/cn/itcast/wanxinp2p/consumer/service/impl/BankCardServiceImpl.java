package cn.itcast.wanxinp2p.consumer.service.impl;


import cn.itcast.wanxinp2p.api.consumer.model.BankCardDTO;
import cn.itcast.wanxinp2p.consumer.entity.BankCard;
import cn.itcast.wanxinp2p.consumer.mapper.BankCardMapper;
import cn.itcast.wanxinp2p.consumer.service.BankCardService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @className: 业务层
 * @description: TODO
 * @author: RuoNan.Wang
 * @create: 2022-03-21 22:16
 */
@Service
public class BankCardServiceImpl extends ServiceImpl<BankCardMapper,BankCard> implements BankCardService {
    /**
     * 获取银行卡信息
     * @param consumerId 用户id
     * @return
     */
    @Override
    public BankCardDTO getByConsumerId(Long consumerId) {
        BankCard one = getOne(new QueryWrapper<BankCard>()
                .lambda().eq(BankCard::getConsumerId, consumerId)
        );
        return convertBankCardEntityToDTO(one);
    }
    /**
     * 获取银行卡信息
     * @param cardNumber 卡号
     * @return
     */
    @Override
    public BankCardDTO getByCardNumber(String cardNumber) {
        BankCard one = getOne(new QueryWrapper<BankCard>()
                .lambda().eq(BankCard::getCardNumber, cardNumber)
        );
        return convertBankCardEntityToDTO(one);
    }

    /**
     * entity转为dto
     * @param entity
     * @return
     */
    private BankCardDTO convertBankCardEntityToDTO(BankCard entity){
        if (entity == null) {
            return null;
        }
        BankCardDTO dto = new BankCardDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
