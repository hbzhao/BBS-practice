package service;

import dao.*;
import domain.*;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ForumService {
    private UserDao userDao;
    private TopicDao topicDao;
    private PostDao postDao;
    private BoardDao boardDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setTopicDao(TopicDao topicDao) {
        this.topicDao = topicDao;
    }

    @Autowired
    public void setPostDao(PostDao postDao) {
        this.postDao = postDao;
    }

    @Autowired
    public void setBoardDao(BoardDao boardDao) {
        this.boardDao = boardDao;
    }

//    publish a topic

    public void addTopic(Topic topic) {
        //根据topic的board id获得相应的board数据
        Board board = boardDao.get(topic.getBoardId());
        board.setTopicNum(board.getTopicNum() + 1);
        topicDao.save(topic);

        topic.getMainPost().setTopic(topic);
        MainPost mainPost = topic.getMainPost();
        mainPost.setBoardId(topic.getBoardId());
        mainPost.setCreateTime(new Date());
        mainPost.setUser(topic.getUser());
        mainPost.setPostTitle(topic.getTopicTitle());
        postDao.save(mainPost);

        User user = topic.getUser();
        user.setCredits(user.getCredits() + 10);
        userDao.update(user);
    }

    public void removeTopic(int topicId) {
        Topic topic = topicDao.get(topicId);

        Board board = boardDao.get(topic.getBoardId());
        board.setTopicNum(board.getTopicNum() - 1);

        User user = topic.getUser();
        user.setCredits(user.getCredits() - 50);

        topicDao.remove(topic);
        postDao.deleteTopicPosts(topicId);
    }

    public void addPost(Post post) {
        postDao.save(post);

        User user = post.getUser();
        user.setCredits(user.getCredits() + 5);
        userDao.update(user);

        Topic topic = post.getTopic();
        topic.setReplies(topic.getReplies() + 1);
        topic.setLastPost(new Date());
        topicDao.update(topic);
    }

    public void removePost(int postId) {
        Post post = postDao.get(postId);
        postDao.remove(post);

        Topic topic = post.getTopic();
        topic.setReplies(topic.getReplies() - 1);

        User user = post.getUser();
        user.setCredits(user.getCredits() - 20);

    }

    public void addBoard(Board board) {
        boardDao.save(board);
    }

    public void removeBoard(int boardId) {
        Board board = boardDao.get(boardId);
        boardDao.remove(board);
    }

    public void makeDigestTopic(int topicId) {
        Topic topic = topicDao.get(topicId);
        topic.setDigest(Topic.DIGEST_TOPIC);

        User user = topic.getUser();
        user.setCredits(user.getCredits() + 100);
    }

    public List<Board> getAllBoard() {
        return boardDao.loadAll();
    }

    //   这一部分的功能实现逻辑要回来细看
    public Page getPagedTopics(int boardId, int pageNo, int pageSize) {
        return postDao.getPagePosts(boardId, pageNo, pageSize);
    }

    public Page getPagePosts(int topicId, int pageNo, int pageSize) {
        return postDao.getPagePosts(topicId, pageNo, pageSize);
    }

    public Page queryTopicByTitle(String title, int pageNo, int pagesize) {
        return topicDao.queryTopicByTitle(title, pageNo, pagesize);
    }

    public Board getBoardById(int boardId) {
		return boardDao.get(boardId);
	}


	public Topic getTopicByTopicId(int topicId) {
		return topicDao.get(topicId);
	}


	public Post getPostByPostId(int postId){
		return postDao.get(postId);
	}


	public void updateTopic(Topic topic){
		topicDao.update(topic);
	}


	public void updatePost(Post post){
		postDao.update(post);
	}

	public void addBoardManager(int boardId,String userName){
	   	User user = userDao.getUserByUserName(userName);
	   	if(user == null){
	   		throw new RuntimeException("用户名为"+userName+"的用户不存在。");
	   	}else{
            Board board = boardDao.get(boardId);
            user.getManBoards().add(board);
            userDao.update(user);
	   	}
	}

}
