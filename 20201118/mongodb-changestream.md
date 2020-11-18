#mongodb使用changeSteam
```java
 MongoCollection<Document> service = mongoTemplate.getCollection("t_sys_user");
        if (ObjectUtils.isEmpty(service)) {
            return;
        }
        List<Bson> pipeline = singletonList(Aggregates.match(Filters.or(
                Filters.in("operationType", asList("delete", "insert", "update")))));

        MongoCursor<ChangeStreamDocument<Document>> cursor = service.watch(pipeline).fullDocument(FullDocument.UPDATE_LOOKUP).iterator();
        log.info("--------------t_sys_user listening------------");
        while (cursor.hasNext()) {
            ChangeStreamDocument<Document> next = cursor.next();
            log.info("change event info : {}", JSONObject.toJSONString(next));
            if (next.getFullDocument() != null) {
                SysUserEntity sysUserEntity = JSONObject.parseObject(JSONObject.toJSONString(next.getFullDocument()), SysUserEntity.class);
                if ((next.getOperationType() == OperationType.DELETE) || (next.getOperationType() == OperationType.UPDATE && sysUserEntity.getIsDel().equals(CommonConstants.IS_DEL_INVALID))) {
                    redisUtil.delete(CommonConstants.SYS_USER_CACHE_PREFIX + sysUserEntity.getId());
                } else {
                    redisUtil.set(CommonConstants.SYS_USER_CACHE_PREFIX + sysUserEntity.getId(), JSONObject.toJSONString(sysUserEntity));
                }
            }
        }
```
