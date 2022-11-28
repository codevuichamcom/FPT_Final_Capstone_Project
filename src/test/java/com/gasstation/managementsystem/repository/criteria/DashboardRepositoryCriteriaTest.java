package com.gasstation.managementsystem.repository.criteria;

import com.gasstation.managementsystem.entity.Fuel;
import com.gasstation.managementsystem.entity.Station;
import com.gasstation.managementsystem.entity.Tank;
import com.gasstation.managementsystem.model.FuelStatistic;
import com.gasstation.managementsystem.model.TankStatistic;
import com.gasstation.managementsystem.model.dto.dashboard.FuelStatisticDTOFilter;
import com.gasstation.managementsystem.model.dto.dashboard.TankStatisticDTOFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DashboardRepositoryCriteriaTest {

    @Mock
    EntityManager em;
    @Mock
    Query nativeQuery;
    @InjectMocks
    private DashboardRepositoryCriteria dashboardCriteria;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * no stationIds
     */
    @Test
    void fuelStatistic_UTCID01() {
        List<Object[]> mockList = IntStream.range(1, 6).mapToObj(i -> new Object[]{i, i + 10.0, i + 100.0, i + 1000.0, i + 10000.0}).collect(Collectors.toList());
        List<FuelStatistic> mockFuelStatisticList = IntStream.range(1, 6).mapToObj(i -> FuelStatistic.builder()
                .fuelId(i)
                .totalVolume(i + 10.0)
                .totalRevenue(i + 100.0)
                .totalDebt(i + 1000.0)
                .totalCash(i + 10000.0).build()).collect(Collectors.toList());
        Mockito.when(em.createNativeQuery(Mockito.anyString())).thenReturn(nativeQuery);
        Mockito.when(nativeQuery.getResultList()).thenReturn(mockList);
        HashMap<String, Object> mapResult = dashboardCriteria.fuelStatistic(FuelStatisticDTOFilter.builder().build());
        assertTrue(mapResult.containsKey("data"));
        assertEquals(mockFuelStatisticList, mapResult.get("data"));
    }

    /**
     * has stationIds
     */
    @Test
    void fuelStatistic_UTCID02() {
        List<Object[]> mockList = IntStream.range(1, 6).mapToObj(i -> new Object[]{i, i + 10.0, i + 100.0, i + 1000.0, i + 10000.0}).collect(Collectors.toList());
        List<FuelStatistic> mockFuelStatisticList = IntStream.range(1, 6).mapToObj(i -> FuelStatistic.builder()
                .fuelId(i)
                .totalVolume(i + 10.0)
                .totalRevenue(i + 100.0)
                .totalDebt(i + 1000.0)
                .totalCash(i + 10000.0).build()).collect(Collectors.toList());
        Mockito.when(em.createNativeQuery(Mockito.anyString())).thenReturn(nativeQuery);
        Mockito.when(nativeQuery.getResultList()).thenReturn(mockList);
        HashMap<String, Object> mapResult = dashboardCriteria.fuelStatistic(FuelStatisticDTOFilter.builder().stationIds(new Integer[]{1, 2, 3}).build());
        assertTrue(mapResult.containsKey("data"));
        assertEquals(mockFuelStatisticList, mapResult.get("data"));
    }

    @Test
    void tankStatistic() {
        final int TANK_ID = 0;
        final int TANK_NAME = 1;
        final int TANK_VOLUME = 2;
        final int TANK_REMAIN = 3;
        final int TANK_CURRENT_PRICE = 4;
        final int FUEL_ID = 5;
        final int FUEL_NAME = 6;
        final int STATION_ID = 7;
        final int STATION_NAME = 8;
        final int TOTAL_IMPORT = 9;
        final int TOTAL_EXPORT = 10;
        List<Object[]> mockList = IntStream.range(1, 6).mapToObj(i -> {
            Object[] objects = new Object[11];
            objects[TANK_ID] = i;
            objects[TANK_NAME] = "tank" + i;
            objects[TANK_VOLUME] = i + 100.0;
            objects[TANK_REMAIN] = i + 100.0 - 50.0;
            objects[TANK_CURRENT_PRICE] = i + 50.0;
            objects[FUEL_ID] = i;
            objects[FUEL_NAME] = "fuel" + i;
            objects[STATION_ID] = i;
            objects[STATION_NAME] = "station" + i;
            objects[TOTAL_IMPORT] = i + 1000.0;
            objects[TOTAL_EXPORT] = i + 2000.0;
            return objects;
        }).collect(Collectors.toList());

        List<TankStatistic> mockTankStatisticList = mockList.stream().map(objects -> TankStatistic.builder()
                .tank(Tank.builder()
                        .id((Integer) objects[TANK_ID])
                        .name((String) objects[TANK_NAME])
                        .volume((Double) objects[TANK_VOLUME])
                        .remain((Double) objects[TANK_REMAIN])
                        .currentPrice((Double) objects[TANK_CURRENT_PRICE])
                        .fuel(Fuel.builder()
                                .id((Integer) objects[FUEL_ID])
                                .name((String) objects[FUEL_NAME]).build())
                        .station(Station.builder()
                                .id((Integer) objects[STATION_ID])
                                .name((String) objects[STATION_NAME]).build()).build())
                .totalImport((Double) objects[TOTAL_IMPORT])
                .totalExport((Double) objects[TOTAL_EXPORT]).build()).collect(Collectors.toList());
        Mockito.when(em.createNativeQuery(Mockito.anyString())).thenReturn(nativeQuery);
        Mockito.when(nativeQuery.getResultList()).thenReturn(mockList);
        HashMap<String, Object> mapResult = dashboardCriteria.tankStatistic(TankStatisticDTOFilter.builder().stationIds(new Integer[]{1}).build());
        assertTrue(mapResult.containsKey("data"));
        assertEquals(mockTankStatisticList,mapResult.get("data"));

    }
}