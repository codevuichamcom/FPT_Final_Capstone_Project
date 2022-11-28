package com.gasstation.managementsystem.repository.criteria;

import com.gasstation.managementsystem.entity.Expense;
import com.gasstation.managementsystem.model.dto.expense.ExpenseDTOFilter;
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
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpenseRepositoryCriteriaTest {
    @Mock
    EntityManager em;
    @Mock
    Query countTotalQuery;
    @Mock
    TypedQuery tQuery;
    @Mock
    QueryGenerateHelper qHelper;
    @InjectMocks
    private ExpenseRepositoryCriteria expenseCriteria;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        List<Expense> expenses = IntStream.range(1, 3).mapToObj(value -> Expense.builder().id(value).build()).collect(Collectors.toList());
        Mockito.when(em.createQuery(Mockito.anyString())).thenReturn(countTotalQuery);
        Mockito.when(em.createQuery(Mockito.anyString(), Mockito.any())).thenReturn(tQuery);
        Mockito.when(qHelper.paging(tQuery, countTotalQuery, 1, 10)).thenReturn(new HashMap<>());
        Mockito.when(countTotalQuery.getSingleResult()).thenReturn(10L);
        Mockito.when(tQuery.getResultList()).thenReturn(expenses);
        ExpenseDTOFilter filter = ExpenseDTOFilter.builder()
                .pageIndex(0)
                .pageSize(3).build();
        HashMap<String, Object> mapResult = expenseCriteria.findAll(filter);
        assertTrue(mapResult.containsKey("data"));
        assertTrue(mapResult.containsKey("totalElement"));
        assertTrue(mapResult.containsKey("totalPage"));
        List<Expense> expenseListResult = (List<Expense>) mapResult.get("data");
        assertEquals(expenses, expenseListResult);
    }
}