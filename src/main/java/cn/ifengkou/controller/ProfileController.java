package cn.ifengkou.controller;

import cn.ifengkou.model.UserAccount;
import cn.ifengkou.service.UserAccountService;
import cn.ifengkou.utils.HttpUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.security.KeyPair;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ProfileController {

    private Logger log = LoggerFactory.getLogger(this.getClass());


    private static final Pattern AUTHORIZATION_PATTERN = Pattern.compile("^[B|b]]earer (?<token>[a-zA-Z0-9-._~+/]+)=*$");

    @Autowired
    UserAccountService userAccountService;

    @Autowired
    KeyPair keyPair;

    @ResponseBody
    @RequestMapping("/user/me")
    public ResponseEntity<Object> info(@RequestParam(value = "access_token", required = false) String paramToken,
                                       @RequestHeader(value = "Authorization", required = false) String headerToken,
                                       @CookieValue(value = "access_token", required = false) String cookieToken) {
        Map<String, Object> result = new HashMap<>(16);
        String errMsg = "未检测到token";
        try {
            String token = null;
            if (StringUtils.isNoneBlank(headerToken)) {
                Matcher matcher = AUTHORIZATION_PATTERN.matcher(headerToken);
                if (matcher.matches()) {
                    token = matcher.group("token");
                }
            }

            if (token == null && StringUtils.isNoneBlank(paramToken)) {
                token = paramToken;
            }

            if (token == null && StringUtils.isNoneBlank(cookieToken)) {
                token = cookieToken;
            }

            if (token != null) {
                try {
                    Claims claims = Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build().parseClaimsJws(token).getBody();

                    String username = claims.getSubject();
                    UserAccount userAccount = userAccountService.findByUsername(username);
                    result.put("username", username);
                    result.put("email", userAccount.getEmail());
                    result.put("openId", userAccount.getOpenId());
                    if(StringUtils.isNotBlank(userAccount.getMobile())){
                        result.put("mobile", userAccount.getMobile());
                    }
                    //result.put("unionId", "" + userAccount.getId());
                    return HttpUtils.buildJsonResponse(result);
                } catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug("exception", e);
                    }
                    return HttpUtils.buildJsonResponse(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage(),null);
                }
            }
        } catch (Exception e) {
            if (log.isInfoEnabled()) {
                log.info("/user/me exception", e);
            }
            errMsg = "access_token无效";
        }

        return HttpUtils.buildJsonResponse(HttpStatus.BAD_REQUEST,errMsg);
    }

    @GetMapping(value = {"", "/", "/user/profile"})
    public String profile(Principal principal,
                          Model model) {
        try {
            UserAccount userAccount = userAccountService.findByUsername(principal.getName());
            model.addAttribute("userAccount", userAccount);
        } catch (EntityNotFoundException e) {
            if (log.isErrorEnabled()) {
                log.error("findByUsername exception", e);
            }
        }

        return "profile";
    }

    @PostMapping("/user/profile")
    public String handleProfile(Principal principal,
                                @RequestParam(value = "nickName", required = false) String nickName,
                                @RequestParam(value = "avatarUrl", required = false) String avatarUrl,
                                @RequestParam(value = "email", required = false) String email,
                                @RequestParam(value = "mobile", required = false) String mobile,
                                @RequestParam(value = "province", required = false) String province,
                                @RequestParam(value = "city", required = false) String city,
                                @RequestParam(value = "address", required = false) String address,
                                @JsonFormat(pattern = "yyyy-MM-dd") @DateTimeFormat(pattern = "yyyy-MM-dd")
                                @RequestParam(value = "birthday", required = false) LocalDate birthday,
                                Model model) {

        try {
            UserAccount userAccount = userAccountService.findByUsername(principal.getName());
            /*userAccount.setNickName(StringEscapeUtils.escapeHtml4(nickName));
            userAccount.setAvatarUrl(StringEscapeUtils.escapeHtml4(avatarUrl));*/
            userAccount.setEmail(StringEscapeUtils.escapeHtml4(email));
            userAccount.setMobile(StringEscapeUtils.escapeHtml4(mobile));
            /*userAccount.setProvince(StringEscapeUtils.escapeHtml4(province));
            userAccount.setCity(StringEscapeUtils.escapeHtml4(city));
            userAccount.setAddress(StringEscapeUtils.escapeHtml4(address));
            userAccount.setBirthday(birthday);*/
            userAccount = userAccountService.updateById(userAccount);
            model.addAttribute("userAccount", userAccount);
            model.addAttribute("updated", true);
        } catch (EntityNotFoundException e) {
            if (log.isErrorEnabled()) {
                log.error("findByUsername exception", e);
            }
        }
        return "profile";
    }
}
