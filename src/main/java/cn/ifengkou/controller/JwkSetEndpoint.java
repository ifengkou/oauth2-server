package cn.ifengkou.controller;

import cn.ifengkou.config.SysProperties;
import cn.ifengkou.utils.BigIntegerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shenlongguang<https://github.com/ifengkou>
 * @date: 2020/8/24
 */
@Controller
public class JwkSetEndpoint {

    @Autowired
    KeyPair keyPair;

    @Autowired
    SysProperties sysProperties;

    @GetMapping("/.well-known/jwks.json")
    @ResponseBody
    public Map<String, List<Map<String, Object>>> getKey() {
        Map<String, List<Map<String, Object>>> jwksData = new HashMap<>(16);
        RSAPublicKey publicKey = (RSAPublicKey) this.keyPair.getPublic();

        String n = new String(Base64Utils.encode(BigIntegerUtils.toBytesUnsigned(publicKey.getModulus())));
        String e = new String(Base64Utils.encode(BigIntegerUtils.toBytesUnsigned(publicKey.getPublicExponent())));
        Map<String, Object> jwk = new HashMap<>(16);
        jwk.put("kty", publicKey.getAlgorithm());
        jwk.put("n", n);
        jwk.put("e", e);
        jwksData.put("keys", Arrays.asList(jwk));
        return jwksData;
    }

    @GetMapping("/.well-known/oauth-authorization-server")
    @ResponseBody
    public Map<String, String> metadataRequest() {
        Map<String, String> metaData = new HashMap<>(16);
        metaData.put("issuer", sysProperties.getIssuerUri());
        metaData.put("authorization_endpoint", sysProperties.getIssuerUri() + "/oauth/authorize");
        metaData.put("token_endpoint", sysProperties.getIssuerUri() + "/oauth/token");
        metaData.put("check_token", sysProperties.getIssuerUri() + "/oauth/check_token");
        metaData.put("jwks_uri", sysProperties.getIssuerUri() + "/.well-known/jwks.json");
        metaData.put("userinfo_endpoint", sysProperties.getIssuerUri() + "/user/me");
        return metaData;
    }
}
