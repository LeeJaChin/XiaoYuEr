#初始化创建
```java

@Component
@Slf4j
public class SearchConfig implements ApplicationRunner {

    @Resource(name = "elasticsearchTemplate")
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        Reflections reflections = new Reflections("com.search.api.model");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Document.class);
        for (Class<?> document : annotated) {
            Document request = document.getAnnotation(Document.class);
            if (!elasticsearchRestTemplate.indexOps(IndexCoordinates.of(request.indexName())).exists()) {
                log.info("init index : {}", JSONObject.toJSONString(request));
                IndexOperations indexOperations =  elasticsearchRestTemplate.indexOps(document);
                indexOperations.create();
                indexOperations.putMapping(indexOperations.createMapping());
            }
        }
    }
}
```