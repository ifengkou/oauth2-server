package cn.ifengkou.service.token;

import cn.ifengkou.model.AuthClient;

import java.util.Map;

/**
 * @author shenlongguang<https://github.com/ifengkou>
 * @date: 2020/8/24
 */
public interface TokenGranter {
    /**
     *  授权，生成Token
     * @param client
     * @param parameters
     * @return
     */
    Map<String, Object> grant(AuthClient client, Map<String, String> parameters);
}
