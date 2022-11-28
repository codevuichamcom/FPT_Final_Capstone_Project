package com.gasstation.managementsystem.service.impl;

import com.gasstation.managementsystem.entity.PriceChangeHistory;
import com.gasstation.managementsystem.entity.User;
import com.gasstation.managementsystem.model.dto.priceChangeHistory.PriceChangeHistoryDTO;
import com.gasstation.managementsystem.model.mapper.PriceChangeHistoryMapper;
import com.gasstation.managementsystem.repository.PriceChangeHistoryRepository;
import com.gasstation.managementsystem.utils.DateTimeHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriceChangeHistoryServiceImplTest {
    @Mock
    private PriceChangeHistoryRepository priceChangeHistoryRepository;

    @InjectMocks
    private PriceChangeHistoryServiceImpl priceChangeHistoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllByTankId() {
        List<PriceChangeHistory> mockRepository = new ArrayList<>();
        List<PriceChangeHistoryDTO> mockResult = new ArrayList<>();
        mockData(mockRepository, mockResult);
        final int PAGE_INDEX = 1;
        final int PAGE_SIZE = 3;
        Page<PriceChangeHistory> priceChangeHistoryPage = new PageImpl<>(mockRepository.subList(PAGE_INDEX - 1, PAGE_SIZE));
        List<PriceChangeHistoryDTO> mockResultPaged = mockResult.subList(PAGE_INDEX - 1, PAGE_SIZE);
        Mockito.when(priceChangeHistoryRepository.findAllByTankId(1, PageRequest.of(PAGE_INDEX - 1, PAGE_SIZE)))
                .thenReturn(priceChangeHistoryPage);
        HashMap<String, Object> map;
        map = priceChangeHistoryService.findAllByTankId(1,PageRequest.of(PAGE_INDEX - 1, PAGE_SIZE));
        List<PriceChangeHistoryDTO> listResultService = (List<PriceChangeHistoryDTO>) map.get("data");
        for (int i = 0; i < listResultService.size(); i++) {
            PriceChangeHistoryDTO o1 = mockResultPaged.get(i);
            PriceChangeHistoryDTO o2 = listResultService.get(i);
            assertEquals(o1, o2);
        }
    }
    private void mockData(List<PriceChangeHistory> mockRepository, List<PriceChangeHistoryDTO> mockResult) {
        long time = DateTimeHelper.toUnixTime("2021-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
        for (int i = 1; i <= 10; i++) {
            PriceChangeHistory priceChangeHistory = PriceChangeHistory.builder()
                    .id(1)
                    .oldPrice(100d)
                    .newPrice(200d)
                    .time(time)
                    .editor(User.builder().id(1).name("employee1").build())
                    .build();
            mockRepository.add(priceChangeHistory);
            mockResult.add(PriceChangeHistoryMapper.toPriceChangeHistoryDTO(priceChangeHistory));
        }
    }

    @Test
    void testFindAllByTankId() {
        List<PriceChangeHistory> mockRepository = new ArrayList<>();
        List<PriceChangeHistoryDTO> mockResult = new ArrayList<>();
        mockData(mockRepository, mockResult);
        Mockito.when(priceChangeHistoryRepository.findAllByTankId(1, Sort.by(Sort.Direction.DESC, "time")))
                .thenReturn(mockRepository);
        HashMap<String, Object> map;
        map = priceChangeHistoryService.findAllByTankId(1);
        List<PriceChangeHistoryDTO> listResultService = (List<PriceChangeHistoryDTO>) map.get("data");
        for (int i = 0; i < listResultService.size(); i++) {
            PriceChangeHistoryDTO o1 = mockResult.get(i);
            PriceChangeHistoryDTO o2 = listResultService.get(i);
            assertEquals(o1, o2);
        }
    }
}