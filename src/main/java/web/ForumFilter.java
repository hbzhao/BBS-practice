package web;

import cons.CommonConstant;
import domain.User;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ForumFilter implements Filter {

    private static final String FILTERED_REQUEST = "@@session_context_filtered_request";

    private static final String[] INHERENT_ESCAPE_URIS = {"/index.jsp",
            "/index.html", "/login.jsp", "/login/doLogin.html",
            "/register.jsp", "/register.html", "/board/listBoardTopics-",
            "/board/listTopicPosts-"};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest != null && servletRequest.getAttribute(FILTERED_REQUEST) != null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            //设置过滤标识，避免重复过滤
            servletRequest.setAttribute(FILTERED_REQUEST, Boolean.TRUE);
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            User userContext = getSessionUser(httpServletRequest);
//          判断当前是否存在登陆用户，如果不存在登陆用户且访问的是需要权限的页面，则将访问请求的URL打包后转发到登陆界面
            if (userContext == null && isURILogin(httpServletRequest.getRequestURI(), httpServletRequest)) {
                String toUrl = httpServletRequest.getRequestURI().toString();
                if (!StringUtils.isEmpty(httpServletRequest.getQueryString())) {
                    toUrl += "?" + httpServletRequest.getQueryString();
                }
                httpServletRequest.getSession().setAttribute(CommonConstant.LOGIN_TO_URL, toUrl);

                servletRequest.getRequestDispatcher("/login.jsp").forward(servletRequest, servletResponse);
                return;
            }

            filterChain.doFilter(servletRequest, servletResponse);
        }

    }

    @Override
    public void destroy() {

    }

    protected User getSessionUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute(CommonConstant.USER_CONTEXT);
    }

    private boolean isURILogin(String requestURI, HttpServletRequest request) {
        if (request.getContextPath().equalsIgnoreCase(requestURI)
                || (request.getContextPath() + "/").equalsIgnoreCase(requestURI))
            return true;
        for (String uri : INHERENT_ESCAPE_URIS) {
            if (requestURI != null && requestURI.indexOf(uri) >= 0) {
                return true;
            }
        }
        return false;
    }
}
