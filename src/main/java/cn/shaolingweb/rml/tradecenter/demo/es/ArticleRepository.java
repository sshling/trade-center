package cn.shaolingweb.rml.tradecenter.demo.es;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/*
只需要继承ElasticsearchRepository泛型，基础的CRUD不用代码就可以直接使用，如果有简单的特殊需求则根据上面提到的命名规则定义好接口名称就能使用。
但如果是聚合特殊数据则需要自行使用ElasticsearchTemplate实现具体的调用逻辑。
 */
@Repository
public interface ArticleRepository extends ElasticsearchRepository<Article,Long> {

    List<Article> findByTitle(String title);


    /**
     * AND 语句查询
     *
     * @param tile
     * @param clickCount
     * @return
     */
    List<Article> findByTitleAndClickCount(String tile, Integer clickCount);
    /**
     * OR 语句查询
     *
     * @param tile
     * @param clickCount
     * @return
     */
    List<Article> findByTitleOrClickCount(String tile, Integer clickCount);
    /**
     * 查询文章内容分页
     *
     * 等同于下面代码
     * @Query("{\"bool\" : {\"must\" : {\"term\" : {\"content\" : \"?0\"}}}}")
     * Page<Article> findByContent(String content, Pageable pageable);
     *
     * @param content
     * @param page
     * @return
     */
    Page<Article> findByContent(String content, Pageable page);
    /**
     * NOT 语句查询
     *
     * @param content
     * @param page
     * @return
     */
    Page<Article> findByContentNot(String content, Pageable page);
    /**
     * LIKE 语句查询
     *
     * @param content
     * @param page
     * @return
     */
    Page<Article> findByContentLike(String content, Pageable page);

}
