package cn.shaolingweb.rml.tradecenter;

import cn.shaolingweb.rml.tradecenter.demo.es.Article;
import cn.shaolingweb.rml.tradecenter.demo.es.ArticleRepository;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TradeCenterApplicationTests {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /*测试自动创建mapping
     *curl '192.168.0.91:9200/_cat/indices?v'
     * curl -XGET "http://192.168.0.91:9200/article_index/_mapping?pretty"
     *
     *  */
    @Test
    public void test(){
        System.out.println("演示初始化");
    }
    /*
     *保存测试
     * curl '192.168.0.91:9200/article_index/article/_search?q=*&pretty'
     */
    @Test
    public void testSave(){
        Article article1=new Article(1L,"教程1","srpignMVC","srpignMVC入门到放弃",new Date(),22L);
        Article article2=new Article(2L,"教程2","spring","spring入门到放弃",new Date(),20L);
        Article article3=new Article(3L,"教程3","spring","spring入门到放弃",new Date(),20L);
        //bulk index 批量方式插入
        List<Article> sampleEntities = Arrays.asList( article2,article3);
        articleRepository.save(article1);
        articleRepository.saveAll(sampleEntities);
    }

    /*
     *获取所有测试
     * curl '192.168.0.91:9200/article_index/article/_search?q=*&pretty'
     */
    @Test
    public void testFetchAll(){
        for (Article article : articleRepository.findAll()) {
            System.out.println(article.toString());
        }
    }
    /*
     *分页测试
     * curl '192.168.0.91:9200/article_index/article/_search?q=*&pretty'
     */
    @Test
    public void testPage(){

        List<Article> list;
        // list=articleSearchRepository.findByTitleAndClickCount("教程",20 );//and
        // list=articleSearchRepository.findByTitleOrClickCount("教程",20 );//or

        // 分页参数:分页从0开始，clickCount倒序
        Pageable pageable = new PageRequest(0, 5, Sort.Direction.DESC,"clickCount");
        Page<Article> pageageRsutl=articleRepository.findByContent("入门",pageable );
        System.out.println("总页数"+pageageRsutl.getTotalPages());
        list= pageageRsutl.getContent();//结果

        for (Article article : list) {
            System.out.println(article.toString());
        }
    }

    /*
     *其他查找
     * curl '192.168.0.91:9200/article_index/article/_search?q=*&pretty'
     */
    @Test
    public void testDls(){

        List<Article> list;

        // 创建搜索 DSL:多条件搜索
        /* 搜索模式: boolQuery */
       /* Pageable pageable = new PageRequest(0, 5);//分页
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.constantScoreQuery()
                .add(boolQuery().should(QueryBuilders.matchQuery("content", "教程")),
                        ScoreFunctionBuilders.weightFactorFunction(100))
                .add(boolQuery().should(QueryBuilders.matchQuery("clickCount", 20)),
                        ScoreFunctionBuilders.weightFactorFunction(1000));



        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(functionScoreQueryBuilder).build();

        System.out.println("\n search  DSL  = \n " + searchQuery.getQuery().toString());

        Page<Article> searchPageResults = articleRepository.search(searchQuery);
        list= searchPageResults.getContent();//结果

        for (Article article : list) {
            System.out.println(article.toString());
        }*/
    }

    /*
     *聚合查询测试
     * curl '192.168.0.91:9200/article_index/article/_search?q=*&pretty'
     */
    @Test
    public void testScore(){

        /*List<Article> list;

        // 创建搜索 DSL 查询:weightFactorFunction是评分函数，官网的控制相关度中有详细讲解价格，地理位置因素
        *//* 搜索模式 *//*
        String SCORE_MODE_SUM = "sum"; // 权重分求和模式
        Float  MIN_SCORE = 10.0F;      // 由于无相关性的分值默认为 1 ，设置权重分最小值为 10
        // Function Score Query
        Pageable pageable = new PageRequest(0, 5);
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
                .add(boolQuery().should(QueryBuilders.matchQuery("content", "教程")),
                        ScoreFunctionBuilders.weightFactorFunction(1000))
                .add(boolQuery().should(QueryBuilders.matchQuery("clickCount", 20)),
                        ScoreFunctionBuilders.weightFactorFunction(1000)).
                        scoreMode(SCORE_MODE_SUM).setMinScore(MIN_SCORE);//分值模式设置为:求和,


        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable).build();

        System.out.println("\n search  DSL  = \n " + searchQuery.getQuery().toString());

        Page<Article> searchPageResults = articleRepository.search(searchQuery);
        list= searchPageResults.getContent();//结果

        for (Article article : list) {
            System.out.println(article.toString());
        }*/
    }

    /*
     *elasticsearchTemplate自定义查询：提交时间倒叙
     *elasticsearchTemplate
     * curl '192.168.0.91:9200/article_index/article/_search?q=*&pretty'
     */
    @Test
    public void etmTest() {


        //查询关键字
        String word="c入门";

        // 分页设置,postTime倒序
        Pageable pageable = new PageRequest(0, 10, Sort.Direction.DESC,"postTime");

        SearchQuery searchQuery;

        //0.使用queryStringQuery完成单字符串查询queryStringQuery(word, "title")
        //1.multiMatchQuery多个字段匹配 .operator(MatchQueryBuilder.Operator.AND)多项匹配使用and查询即完全匹配都存在才查出来
        //searchQuery = new NativeSearchQueryBuilder().withQuery(multiMatchQuery(word, "title", "content").operator(MatchQueryBuilder.Operator.AND)).withPageable(pageable).build();

        //2.多条件查询：title和content必须包含word=“XXX”且clickCount必须大于200的以postTime倒序分页结果
        word="教程";
        searchQuery = new NativeSearchQueryBuilder().withQuery(boolQuery().must(multiMatchQuery(word, "title", "content").operator(Operator.AND)).must(rangeQuery("clickCount").gt(200))).withPageable(pageable).build();

        List<Article> list= elasticsearchTemplate.queryForList(searchQuery, Article.class);

        for (Article article : list) {
            System.out.println(article.toString());
        }
    }


    @Test
	public void contextLoads() {
	}

}
