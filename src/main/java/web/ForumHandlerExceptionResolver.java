package web;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ForumHandlerExceptionResolver extends SimpleMappingExceptionResolver {
    protected ModelAndView doResolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                              java.lang.Object o, java.lang.Exception e) {
        httpServletRequest.setAttribute("ex", e);
        e.printStackTrace();
        return super.doResolveException(httpServletRequest, httpServletResponse, o, e);
    }
}
