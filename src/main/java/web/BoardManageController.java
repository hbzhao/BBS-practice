package web;

import cons.CommonConstant;
import dao.Page;
import domain.Board;
import domain.Post;
import domain.Topic;
import domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import service.ForumService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@RequestMapping(value = "/board")
public class BoardManageController extends BaseController {
    private ForumService forumService;

    @Autowired
    public void setForumService(ForumService forumService) {
        this.forumService = forumService;
    }

    @RequestMapping(value = "/listBoardTopics-{boardId}", method = RequestMethod.GET)
    public ModelAndView listBoardTopics(@PathVariable Integer boardId,
                                        @RequestParam(value = "pageNo", required = false) Integer pageNo) {
        ModelAndView view = new ModelAndView();
        Board board = forumService.getBoardById(boardId);
        pageNo = pageNo == null ? 1 : pageNo;

        Page pagedTopic = forumService.getPagedTopics(boardId, pageNo, CommonConstant.PAGE_SIZE);
        view.addObject("board", board);
        view.addObject("pagedTopic", pagedTopic);
        view.setViewName("/listBoardTopics");
        return view;
    }

    @RequestMapping(value = "/addTopicPage-{boardId}", method = RequestMethod.GET)
    public ModelAndView addTopicPage(@PathVariable Integer boardId) {
        ModelAndView view = new ModelAndView();
        view.addObject("boardId", boardId);
        view.setViewName("/addTopic");
        return view;
    }

    //    返回的字符串应该是一个url，根据这个string服务器会自动请求对应的页面
    @RequestMapping(value = "/addTopic", method = RequestMethod.POST)
    public String addTopic(HttpServletRequest servletRequest, Topic topic) {
        User user = getSessionUser(servletRequest);
        topic.setUser(user);
        Date create = new Date();
        topic.setCreateTime(create);
        topic.setLastPost(create);
        forumService.addTopic(topic);
        String targetUrl = "/board/listBoardTopics-" + topic.getTopicId() + ".html";
        return "redirect:" + targetUrl;
    }

    @RequestMapping(value = "/listTopicPosts-{topicId}", method = RequestMethod.GET)
    public ModelAndView listTopicPosts(@PathVariable Integer topicId,
                                       @RequestParam(value = "pageNo", required = false) Integer pageNo) {
        ModelAndView view = new ModelAndView();
        Topic topic = forumService.getTopicByTopicId(topicId);
        pageNo = pageNo == null ? 1 : pageNo;
        Page pagePost = forumService.getPagePosts(topicId, pageNo, CommonConstant.PAGE_SIZE);
        view.addObject("topic", topic);
        view.addObject("pagePost", pagePost);
        view.setViewName("/listTopicPosts");
        return view;
    }


    @RequestMapping(value = "/addPost")
    public String addPost(HttpServletRequest request, Post post) {
        post.setCreateTime(new Date());
        post.setUser(getSessionUser(request));

        Topic topic = new Topic();
        int topicId = Integer.valueOf(request.getParameter("topicId"));
        if (topicId > 0) {
            topic.setTopicId(topicId);
            post.setTopic(topic);
        }
        forumService.addPost(post);
        String targetUrl = "/board/listTopicPosts-" + post.getTopic().getTopicId() + ".html";
        return "redirect:" + targetUrl;
    }

    @RequestMapping(value = "/removeBoard", method = RequestMethod.GET)
    public String removeBoard(@RequestParam("boardIds") String boardIds) {
        String[] param = boardIds.split(",");
        for (int i = 0; i < param.length; i++) {
            forumService.removeBoard(new Integer(param[i]));
        }
        String targetUrl = "/index.html";
        return "redirect:" + targetUrl;
    }

    @RequestMapping(value = "/removeTopic", method = RequestMethod.GET)
    public String removeTopic(@RequestParam("topicIds") String topicIds) {
        String[] param = topicIds.split(",");
        for (int i = 0; i < param.length; i++) {
            forumService.removeTopic(new Integer(param[i]));
        }
        String targetUrl = "/index.html";
        return "redirect:" + targetUrl;
    }

    @RequestMapping(value = "/makeDigestPost", method = RequestMethod.GET)
    public String makedigesttopic(@RequestParam("topicIds") String topicIds,
                                  @RequestParam("boardId") String boardId) {
        String[] param = topicIds.split(",");
        for (int i = 0; i < param.length; i++) {
            forumService.makeDigestTopic(Integer.valueOf(param[i]));
        }
        String targetUrl = "/board/listBoardTopics-" + boardId + ".html";
        return "redirect:" + targetUrl;
    }

}
