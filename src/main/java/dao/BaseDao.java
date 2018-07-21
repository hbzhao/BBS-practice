package dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//创建一个基础的Dao类
//T是操作的PO类
public class BaseDao<T> {
    //使用泛型定义操作的PO类
    private Class<T> entityClass;
    //使用Hibernate Template来控制hibernate
    private HibernateTemplate hibernateTemplate;

    //1. 获得进入的泛型类的父类，也就是BaseDao
    //2. 获得BaseDao的类型参数<T>
    //3. 根据这个类型参数来获得具体的泛型类
    public BaseDao() {
        Type genType = getClass().getGenericSuperclass();
        Type[] param = ((ParameterizedType) genType).getActualTypeArguments();
        entityClass = (Class) param[0];
    }

    //所有的PO类都实现了Serializablee接口

    //  根据id获得需要的PO类对象，不存在返回ObjectNotFound Exception
    public T load(Serializable id) {
        return (T) getHibernateTemplate().load(entityClass, id);
    }

    //  功能和load一致，返回null
    public T get(Serializable id) {
        return (T) getHibernateTemplate().get(entityClass, id);
    }

    //  返回entityClass对应的所有持久化对象
    public List<T> loadAll() {
        return getHibernateTemplate().loadAll(entityClass);
    }

    //将给定的对象持久化，保存到数据库中？
    public void save(T entity) {
        getHibernateTemplate().save(entity);
    }

    public void remove(T entity) {
        getHibernateTemplate().delete(entity);
    }

    //    执行SQL语句，删除表中所有的数据
    public void removeAll(String tablName) {
        getSession().createQuery("truncate TABLE " + tablName + "").executeUpdate();
    }

    public void update(T entity) {
        getHibernateTemplate().update(entity);
    }

    public List find(String hql) {
        return this.getHibernateTemplate().find(hql);
    }

    public List find(String hql, Object... params) {
        return this.getHibernateTemplate().find(hql, params);
    }

    public void initialize(Object entity) {
        this.getHibernateTemplate().initialize(entity);
    }

//    public Page

    public Query createQuery(String hql, Object... values) {
        Assert.hasText(hql, "hql is empty");
        Query query = getSession().createQuery(hql);
        for (int i = 0; i < values.length; i++) {
            query.setParameter(i, values[i]);
        }
        return query;
    }

    public String removeSelect(String hql) {
        Assert.hasText(hql, "hql is empty");
        int beginPos = hql.toLowerCase().indexOf("from");
        Assert.isTrue(beginPos != 1, "hql" + hql + "must has a keyword 'from'");
        return hql.substring(beginPos);
    }

    public static String removeOrders(String hql) {
        Assert.hasText(hql, "hql is empty");
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(hql);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    //    分页查询函数
    public Page pagedQuery(String hql, int pageNo, int pageSize, Object... values) {
        Assert.hasText(hql, "hql is empty");
        Assert.isTrue(pageNo >= 1, "pageNo should start from 1");
        String countQueryString = "select count (*) " + removeSelect(removeOrders(hql));
        List countList = getHibernateTemplate().find(countQueryString, values);
        long totalCount = (Long) countList.get(0);

        if (totalCount < 1) {
            return new Page();
        }
        int startIndex = Page.getStartOfPage(pageNo, pageSize);
        Query query = createQuery(hql, values);
        List list = query.setFirstResult(startIndex).setMaxResults(pageSize).list();

        return new Page(startIndex, totalCount, list, pageSize);
    }

    public HibernateTemplate getHibernateTemplate() {
        return hibernateTemplate;
    }

    //    自动注入配置好的hibernateTemplate
    @Autowired
    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public Session getSession() {
        return hibernateTemplate.getSessionFactory().getCurrentSession();
    }
}
