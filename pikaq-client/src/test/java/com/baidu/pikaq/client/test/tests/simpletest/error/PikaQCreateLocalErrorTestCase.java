package com.baidu.pikaq.client.test.tests.simpletest.error;

import java.math.BigDecimal;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.BeforeTransaction;

import com.baidu.pikaq.client.test.common.BaseTestCase;
import com.baidu.pikaq.client.test.mock.PikaQGatewayMockImpl;
import com.baidu.pikaq.client.test.service.campaign.bo.Campaign;
import com.baidu.pikaq.client.test.service.campaign.service.CampaignMgr;

import junit.framework.Assert;

/**
 * Created by knightliao on 15/7/27.
 * <p/>
 * 使用 PikaQ，校验在 本地事务异常时，消息是否会回滚
 */
public class PikaQCreateLocalErrorTestCase extends BaseTestCase {

    protected static final Logger LOGGER = LoggerFactory.getLogger(PikaQCreateLocalErrorTestCase.class);

    @Autowired
    private CampaignMgr campaignMgr;

    private static long RANDOM_DATA = RandomUtils.nextInt(10000000);

    private static String campaignName = "campaign" + String.valueOf(RANDOM_DATA);

    /**
     * 错误处理业务：订单生成，发送消息，（强一致性）
     * <p/>
     * 测试：数据库无此条数据，消息也不存在
     *
     * @throws Exception
     */
    @Test
    public void test() {

    }

    @BeforeTransaction
    public void test_() throws Exception {

        try {
            campaignMgr.createWithLocalErrorPikaQStrong(campaignName, BigDecimal.valueOf(RANDOM_DATA));
        } catch (Throwable e) {
            LOGGER.info("");
        }
    }

    /**
     * 由于 前面的主逻辑已经 throw 异常了，因此，这里都检测不到任何数据了
     */
    @After
    public void check() {

        checkDbData(campaignName);
        checkQData(campaignName);
    }

    /**
     * check Q有数据
     *
     * @param campaignName
     */
    public void checkQData(String campaignName) {

        Object data = PikaQGatewayMockImpl.consumeOne();

        LOGGER.info("checking Q data.......");
        if (data != null) {
            Assert.assertTrue(false);

        } else {
            Assert.assertTrue(true);
        }
    }

    /**
     * check 消息数据库是否数据
     */
    public void checkDbData(String campaignName) {

        LOGGER.info("checking Db data.......");
        //
        // check 数据库有数据
        //
        Campaign campaign = campaignMgr.getByName(campaignName);
        Assert.assertNull(campaign);
    }
}
