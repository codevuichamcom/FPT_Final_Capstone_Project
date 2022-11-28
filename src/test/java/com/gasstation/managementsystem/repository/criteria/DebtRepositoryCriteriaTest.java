package com.gasstation.managementsystem.repository.criteria;

import com.gasstation.managementsystem.model.dto.card.CardDTO;
import com.gasstation.managementsystem.model.dto.debt.DebtDTOSummary;
import com.gasstation.managementsystem.model.dto.debt.DebtDTOSummaryFilter;
import com.gasstation.managementsystem.model.dto.station.StationDTO;
import com.gasstation.managementsystem.model.dto.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DebtRepositoryCriteriaTest {

    @Mock
    EntityManager em;
    @Mock
    Query query;
    @InjectMocks
    private DebtRepositoryCriteria debtCriteria;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDetail() {
    }

    @Test
    void summary() {
        final int CARD_ID = 0;
        final int STATION_ID = 1;
        final int STATION_NAME = 2;
        final int STATION_ADDRESS = 3;
        final int CUSTOMER_ID = 4;
        final int CUSTOMER_NAME = 5;
        final int CUSTOMER_PHONE = 6;
        final int TOTAL_ACCOUNTS_PAYABLE = 7;
        List<Object[]> mockList = IntStream.range(1, 6).mapToObj(i -> {
            Object[] objects = new Object[8];
            objects[CARD_ID] = "76616805-769f-4b55-b5cc-122a9157a1c"+i;
            objects[STATION_ID] = i;
            objects[STATION_NAME] = "station" + i;
            objects[STATION_ADDRESS] = "address" + i;
            objects[CUSTOMER_ID] = i;
            objects[CUSTOMER_NAME] = "customer" + i;
            objects[CUSTOMER_PHONE] = "" + 1000000001 * i;
            objects[TOTAL_ACCOUNTS_PAYABLE] = 1000.0 + i;
            return objects;
        }).collect(Collectors.toList());
        List<DebtDTOSummary> mockDebtDTOSummaryList = mockList.stream().map(objects -> DebtDTOSummary.builder()
                .card(CardDTO.builder()
                        .id(UUID.fromString((String) objects[0]))
                        .customer(UserDTO.builder()
                                .id((Integer) objects[4])
                                .name((String) objects[5])
                                .phone((String) objects[6]).build()).build())
                .station(StationDTO.builder()
                        .id((Integer) objects[1])
                        .name((String) objects[2])
                        .address((String) objects[3]).build())
                .totalAccountsPayable((Double) objects[7])
                .build()).collect(Collectors.toList());
        Mockito.when(em.createNativeQuery(Mockito.anyString())).thenReturn(query);
        Mockito.when(query.getSingleResult()).thenReturn(new BigInteger("10"));
        Mockito.when(query.getResultList()).thenReturn(mockList);
        HashMap<String, Object> mapResult = debtCriteria.summary(DebtDTOSummaryFilter.builder().build());
        assertTrue(mapResult.containsKey("data"));
        assertEquals(mockDebtDTOSummaryList, mapResult.get("data"));
    }
}