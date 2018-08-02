package web;

import cons.CommonConstant;
import dao.Page;
import domain.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import service.ForumService;

@Controller
public class BoardManageController extends BaseController {
    private ForumService forumService;

    @Autowired
    public void setForumService(ForumService forumService) {
        this.forumService = forumService;
    }

    @RequestMapping(value = "/board/listBoardTopics-{boardId}", method = RequestMethod.GET)
    public ModelAndView listBoardTopics(@PathVariable Integer boardId,
                                        @RequestParam(value = "pageNo",required = false) Integer pageNo){
        ModelAndView view = new ModelAndView();
        Board board = forumService.getBoardById(boardId);
        pageNo=pageNo==null?1:pageNo;

        Page pagedTopic = forumService.getPagedTopics(boardId,pageNo,CommonConstant.PAGE_SIZE);
        view.addObject("board", board);
        view.addObject("pagedTopic", pagedTopic);
        view.setViewName("/listBoardTopics");
        return view;
    }
}
