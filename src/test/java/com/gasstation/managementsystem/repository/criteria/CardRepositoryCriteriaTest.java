package com.gasstation.managementsystem.repository.criteria;

import com.gasstation.managementsystem.entity.Card;
import com.gasstation.managementsystem.model.dto.card.CardDTOFilter;
import com.gasstation.managementsystem.utils.QueryGenerateHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardRepositoryCriteriaTest {

    @Mock
    EntityManager em;
    @Mock
    Query countTotalQuery;
    @Mock
    TypedQuery tQuery;
    @Mock
    QueryGenerateHelper qHelper;
    @InjectMocks
    private CardRepositoryCriteria cardCriteria;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * statuses = [ACTIVATED,DEACTIVATED]
     */
    @Test
    void findAll_UTCID01() {
        List<Card> cards = IntStream.range(1, 3).mapToObj(value -> Card.builder().id(UUID.randomUUID()).build()).collect(Collectors.toList());
        Mockito.when(em.createQuery(Mockito.anyString())).thenReturn(countTotalQuery);
        Mockito.when(em.createQuery(Mockito.anyString(), Mockito.any())).thenReturn(tQuery);
        Mockito.when(qHelper.paging(tQuery, countTotalQuery, 1, 10)).thenReturn(new HashMap<>());
        Mockito.when(countTotalQuery.getSingleResult()).thenReturn(10L);
        Mockito.when(tQuery.getResultList()).thenReturn(cards);
        List<String> statuses = Arrays.asList(CardDTOFilter.STATUS_ACTIVATED, CardDTOFilter.STATUS_DEACTIVATED);
        CardDTOFilter filter = CardDTOFilter.builder()
                .pageIndex(0)
                .pageSize(3)
                .statuses(statuses.toArray(String[]::new))
                .build();
        HashMap<String, Object> mapResult = cardCriteria.findAll(filter);
        assertTrue(mapResult.containsKey("data"));
        assertTrue(mapResult.containsKey("totalElement"));
        assertTrue(mapResult.containsKey("totalPage"));
        List<Card> expenseListResult = (List<Card>) mapResult.get("data");
        assertEquals(cards, expenseListResult);
    }

    /**
     * statuses = [STATUS_DEACTIVATED]
     */
    @Test
    void findAll_UTCID02() {
        List<Card> cards = IntStream.range(1, 3).mapToObj(value -> Card.builder().id(UUID.randomUUID()).build()).collect(Collectors.toList());
        Mockito.when(em.createQuery(Mockito.anyString())).thenReturn(countTotalQuery);
        Mockito.when(em.createQuery(Mockito.anyString(), Mockito.any())).thenReturn(tQuery);
        Mockito.when(qHelper.paging(tQuery, countTotalQuery, 1, 10)).thenReturn(new HashMap<>());
        Mockito.when(countTotalQuery.getSingleResult()).thenReturn(10L);
        Mockito.when(tQuery.getResultList()).thenReturn(cards);
        List<String> statuses = List.of(CardDTOFilter.STATUS_DEACTIVATED);
        CardDTOFilter filter = CardDTOFilter.builder()
                .pageIndex(0)
                .pageSize(3)
                .statuses(statuses.toArray(String[]::new))
                .build();
        HashMap<String, Object> mapResult = cardCriteria.findAll(filter);
        assertTrue(mapResult.containsKey("data"));
        assertTrue(mapResult.containsKey("totalElement"));
        assertTrue(mapResult.containsKey("totalPage"));
        List<Card> expenseListResult = (List<Card>) mapResult.get("data");
        assertEquals(cards, expenseListResult);
    }
}