package web;

import cons.CommonConstant;
import domain.User;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
    protected static final String ERROR_MSG_KEY="errorMsg";

    protected User getSessionUser(HttpServletRequest request){
        return (User)request.getSession().getAttribute(CommonConstant.USER_CONTEXT);
    }

    protected void setSessionUser(HttpServletRequest request,User user){
        request.getSession().setAttribute(CommonConstant.USER_CONTEXT,user);
    }

    public final String getAppbaseUrl(HttpServletRequest request,String url){
        Assert.hasLength(url, "url不能为空");
        Assert.isTrue(url.startsWith("/"), "必须以/打头");
        return request.getContextPath() + url;
    }
}
