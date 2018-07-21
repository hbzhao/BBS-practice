package dao;

import domain.Board;
import org.springframework.stereotype.Repository;

import java.util.Iterator;

@Repository
public class BoardDao extends BaseDao<Board> {
//    HQL is a language use to query Object
    private static final String GET_BOARD_NUM = "select count(f.boardId) from Board f";

    public long getBoardNum(){
        Iterator iterator = getHibernateTemplate().iterate(GET_BOARD_NUM);
        return ((Long)iterator.next());
    }
}
