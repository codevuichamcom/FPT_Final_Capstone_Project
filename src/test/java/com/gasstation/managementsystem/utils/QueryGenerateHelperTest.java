package com.gasstation.managementsystem.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryGenerateHelperTest {

    private QueryGenerateHelper queryGenerateHelper;
    private Map<String, Object> params = new HashMap<>();

    @Mock
    TypedQuery typedQuery;
    @Mock
    Query countTotalQuery;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * values is null
     */
    @Test
    void in_UTCID01() {
        QueryGenerateHelper result = new QueryGenerateHelper();
        result.setParams(null);
        queryGenerateHelper = new QueryGenerateHelper();
        queryGenerateHelper.in("field", "strKey", null);
    }

    /**
     * values is not null
     */
    @Test
    void in_UTCID02() {
        Object[] values = new Object[1];
        values[0] = "value1";
        QueryGenerateHelper result = new QueryGenerateHelper();
        queryGenerateHelper = new QueryGenerateHelper();
        queryGenerateHelper.setQuery(new StringBuilder());

        params = new HashMap<>();
        params.put("strKey", Arrays.asList(values));
        result.setQuery(new StringBuilder(" AND field IN  (:strKey)"));
        result.setParams(params);
        assertEquals(result, queryGenerateHelper.in("field", "strKey", values));
    }

    /**
     * values is null
     */
    @Test
    void like_UTCID01() {
        QueryGenerateHelper result = new QueryGenerateHelper();
        result.setParams(null);
        queryGenerateHelper = new QueryGenerateHelper();
        queryGenerateHelper.like("field", "strKey", null);
    }

    /**
     * values is not null
     */
    @Test
    void like_UTCID02() {
        String value = "value1";
        QueryGenerateHelper result = new QueryGenerateHelper();
        queryGenerateHelper = new QueryGenerateHelper();
        queryGenerateHelper.setQuery(new StringBuilder());

        params = new HashMap<>();
        params.put("strKey", "%" + value + "%");
        result.setQuery(new StringBuilder(" AND LOWER(field) LIKE LOWER (:strKey)"));
        result.setParams(params);
        assertEquals(result, queryGenerateHelper.like("field", "strKey", "value1"));
    }

    /**
     * values is null
     */
    @Test
    void between_UTCID01() {
        QueryGenerateHelper result = new QueryGenerateHelper();
        result.setParams(null);
        queryGenerateHelper = new QueryGenerateHelper();
//        queryGenerateHelper.between("field", 100d, 1000d, "strKey", null);
    }

    /**
     * values is null
     */
    @Test
    void equal_UTCID01() {
        QueryGenerateHelper result = new QueryGenerateHelper();
        result.setParams(null);
        queryGenerateHelper = new QueryGenerateHelper();
        queryGenerateHelper.equal("field", "key", null);
    }

    /**
     * values is not null
     */

    @Test
    void equal_UTCID02() {
        String value = "value1";
        QueryGenerateHelper result = new QueryGenerateHelper();
        queryGenerateHelper = new QueryGenerateHelper();
        queryGenerateHelper.setQuery(new StringBuilder());

        params = new HashMap<>();
        params.put("strKey", value);
        result.setQuery(new StringBuilder(" AND field =  (:strKey)"));
        result.setParams(params);
        assertEquals(result, queryGenerateHelper.equal("field", "strKey", value));
    }

    @Test
    void between() {
        QueryGenerateHelper result = new QueryGenerateHelper();
        queryGenerateHelper = new QueryGenerateHelper();
        queryGenerateHelper.setQuery(new StringBuilder());
        params = new HashMap<>();
        result.setQuery(new StringBuilder(" AND field >= 100.0 AND field <= 1000.0"));
        result.setParams(params);
        assertEquals(result, queryGenerateHelper.between("field", 100d, 1000d));
    }


    /**
     * values is not null
     */
    @Test
    void testBetween() {
        QueryGenerateHelper result = new QueryGenerateHelper();
        queryGenerateHelper = new QueryGenerateHelper();
        queryGenerateHelper.setQuery(new StringBuilder());

        params = new HashMap<>();
        result.setQuery(new StringBuilder(" AND field >= 100 AND field <= 1000"));
        result.setParams(params);
        assertEquals(result, queryGenerateHelper.between("field", 100L, 1000L));
    }

    /**
     * values is null
     */
    @Test
    void sort() {
        String value = null;
        QueryGenerateHelper result = new QueryGenerateHelper();
        queryGenerateHelper = new QueryGenerateHelper();
        queryGenerateHelper.setQuery(new StringBuilder());

        params = new HashMap<>();
        result.setQuery(new StringBuilder(" ORDER BY field ASC"));
        result.setParams(params);
        assertEquals(result, queryGenerateHelper.sort("field", value));
    }

    @Test
    void sort_UTCID02() {
        HashMap<String, String> sortMap = new HashMap<>();
        sortMap.put("id", "DESC");
        sortMap.put("name", "ASC");
        queryGenerateHelper = new QueryGenerateHelper();
        queryGenerateHelper.setQuery(new StringBuilder());
        queryGenerateHelper.getQuery().append("select * from user");
        queryGenerateHelper.sort(sortMap);
        assertEquals("select * from user ORDER BYname ASC, name ASC, id DESC", queryGenerateHelper.getQuery().toString());
    }

    @Test
    void setValueToParams() {
        StringBuilder query = new StringBuilder();
        HashMap<String, Object> params = new HashMap<>();
        params.put("param", 1);
        queryGenerateHelper = QueryGenerateHelper.builder()
                .query(query)
                .params(params).build();

        queryGenerateHelper.setValueToParams(Mockito.mock(Query.class));
    }


    @Test
    void paging() {
        queryGenerateHelper = new QueryGenerateHelper();
        List list = new ArrayList<>();
        list.add(3);
        HashMap<String, Object> result = new HashMap<>();
        result.put("data", list);
        result.put("totalElement", 1000);
        result.put("totalPage", 334);

        assertEquals(result.toString(), queryGenerateHelper.paging(3, 1000, list).toString());
    }

    @Test
    void paging_UTCID02() {
        queryGenerateHelper = new QueryGenerateHelper();
        HashMap<String, Object> params = new HashMap<>();
        params.put("param", 1);
        queryGenerateHelper.setQuery(new StringBuilder());
        queryGenerateHelper.setParams(params);
        Mockito.when(countTotalQuery.getSingleResult()).thenReturn(11L);
        Mockito.when(typedQuery.getResultList()).thenReturn(new ArrayList());
        HashMap<String, Object> mapResult = queryGenerateHelper.paging(typedQuery, countTotalQuery, 0, 5);
    }

    @Test
    void isNULL() {
        QueryGenerateHelper result = new QueryGenerateHelper();
        queryGenerateHelper = new QueryGenerateHelper();
        queryGenerateHelper.setQuery(new StringBuilder());

        params = new HashMap<>();
        result.setQuery(new StringBuilder(" field IS  NULL "));
        result.setParams(params);
        assertEquals(result, queryGenerateHelper.isNULL("field"));
    }

    @Test
    void isNotNULL() {
        QueryGenerateHelper result = new QueryGenerateHelper();
        queryGenerateHelper = new QueryGenerateHelper();
        queryGenerateHelper.setQuery(new StringBuilder());

        params = new HashMap<>();
        result.setQuery(new StringBuilder(" field IS NOT  NULL "));
        result.setParams(params);
        assertEquals(result, queryGenerateHelper.isNotNULL("field"));
    }

    @Test
    void or() {
        QueryGenerateHelper result = new QueryGenerateHelper();
        queryGenerateHelper = new QueryGenerateHelper();
        queryGenerateHelper.setQuery(new StringBuilder());

        params = new HashMap<>();
        result.setQuery(new StringBuilder(" OR "));
        result.setParams(params);
        assertEquals(result, queryGenerateHelper.or());
    }

    @Test
    void and() {
        QueryGenerateHelper result = new QueryGenerateHelper();
        queryGenerateHelper = new QueryGenerateHelper();
        queryGenerateHelper.setQuery(new StringBuilder());

        params = new HashMap<>();
        result.setQuery(new StringBuilder(" AND "));
        result.setParams(params);
        assertEquals(result, queryGenerateHelper.and());
    }

    @Test
    void openBracket() {
        QueryGenerateHelper result = new QueryGenerateHelper();
        queryGenerateHelper = new QueryGenerateHelper();
        queryGenerateHelper.setQuery(new StringBuilder());

        params = new HashMap<>();
        result.setQuery(new StringBuilder(" ( "));
        result.setParams(params);
        assertEquals(result, queryGenerateHelper.openBracket());
    }

    @Test
    void closeBracket() {
        QueryGenerateHelper result = new QueryGenerateHelper();
        queryGenerateHelper = new QueryGenerateHelper();
        queryGenerateHelper.setQuery(new StringBuilder());

        params = new HashMap<>();
        result.setQuery(new StringBuilder(" ) "));
        result.setParams(params);
        assertEquals(result, queryGenerateHelper.closeBracket());
    }
}