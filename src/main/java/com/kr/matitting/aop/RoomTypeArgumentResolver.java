package com.kr.matitting.aop;

import com.kr.matitting.annotaiton.RoomType;
import com.kr.matitting.constant.ChatRoomType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class RoomTypeArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(RoomType.class) != null
                && parameter.getParameterType().equals(ChatRoomType.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        return resolve(request.getRequestURI());
    }

    private ChatRoomType resolve(String uri) {
        if(uri.contains("1on1")) return ChatRoomType.PRIVATE;
        else if(uri.contains("group")) return ChatRoomType.GROUP;
        throw  new RuntimeException();
    }
}
