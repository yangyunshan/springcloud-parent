package com.sensorweb.datacentergateway.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nimbusds.jose.JWSObject;
import com.sensorweb.datacentergateway.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;

@Slf4j
@Component
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Autowired
    private UserService userService;

    private final Set<String> permitAll = new HashSet<>();
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public AuthorizationManager() {
        permitAll.add("/");
        permitAll.add("/error");
        permitAll.add("favicon.ico");
        permitAll.add("/**/swagger-resources/**");
        permitAll.add("/swagger-ui.html");
        permitAll.add("/**/oauth/**");

    }
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        //获取当前路径可访问角色列表
        ServerWebExchange exchange = authorizationContext.getExchange();
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        //1.对应跨域的预检请求直接放行
        HttpMethod httpMethod = request.getMethod();
        if (request.getMethod()== HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }
        if (checkPath(permitAll, path)) {
            return Mono.just(new AuthorizationDecision(true));
        }
        //2.token为空拒绝访问
        String token = request.getHeaders().getFirst("Authorization");
        if (StringUtils.isBlank(token)) {
            return Mono.just(new AuthorizationDecision(false));
        }
        List<String> paths = new ArrayList<>();
        //解析token
        try {
            String realToken = token.replace("Bearer ", "");
            JWSObject jwsObject = JWSObject.parse(realToken);
            String jsonStr = jwsObject.getPayload().toString();
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            BigInteger epoch = jsonObject.getBigInteger("exp");
            Date date = new Date(epoch.longValue() * 1000);
            if (date.before(new Date(System.currentTimeMillis()))) {
                System.out.println("过期");
                return Mono.just(new AuthorizationDecision(false));
            }
            JSONArray jsonArray = jsonObject.getJSONArray("authorities");
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            for (int i=0; i<jsonArray.size(); i++) {
                authorities.add(new SimpleGrantedAuthority(jsonArray.getString(i)));
            }
            if (authorities.size()>0) {
                for (GrantedAuthority authority:authorities) {
                    String role = authority.getAuthority();
                    List<String> temp = userService.getPathByRoleName(role);
                    if (temp!=null && temp.size()>0) {
                        paths.addAll(temp);
                    }
                }
            }
            return Mono.just(new AuthorizationDecision(checkPath(paths, path)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return Mono.just(new AuthorizationDecision(false));
    }

    private boolean checkPath(Collection<String> patterns, String path) {
        for (String str:patterns) {
            if (antPathMatcher.match(str, path)) {
                return true;
            }
        }
        return false;
    }

}