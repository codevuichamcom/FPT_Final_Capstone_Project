package com.gasstation.managementsystem.service.impl;

import com.gasstation.managementsystem.entity.*;
import com.gasstation.managementsystem.model.FuelStatistic;
import com.gasstation.managementsystem.model.TankStatistic;
import com.gasstation.managementsystem.model.dto.dashboard.FuelStatisticDTO;
import com.gasstation.managementsystem.model.dto.dashboard.FuelStatisticDTOFilter;
import com.gasstation.managementsystem.model.dto.dashboard.TankStatisticDTO;
import com.gasstation.managementsystem.model.dto.dashboard.TankStatisticDTOFilter;
import com.gasstation.managementsystem.model.mapper.DashboardMapper;
import com.gasstation.managementsystem.repository.criteria.DashboardRepositoryCriteria;
import com.gasstation.managementsystem.utils.UserHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DashboardServiceImplTest {
    @Mock
    private DashboardRepositoryCriteria dashboardCriteria;
    @Mock
    private UserHelper userHelper;
    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fuelStatistic() {
        Long startTime = 16094540000000L;
        Long endTime = 16094640000000L;
        List<FuelStatistic> fuelStatisticList = new ArrayList<>();
        fuelStatisticList.add(FuelStatistic.builder().fuelId(1)
                .totalVolume(100d)
                .totalRevenue(200d)
                .totalDebt(3000d)
                .totalCash(400d)
                .build());
        fuelStatisticList.add(FuelStatistic.builder().fuelId(2)
                .totalVolume(100d)
                .totalRevenue(200d)
                .totalDebt(3000d)
                .totalCash(400d)
                .build());
        HashMap<String, Object> temp = new HashMap<>();
        temp.put("totalElement", 10);
        temp.put("data", fuelStatisticList);
        temp.put("totalPage", 3);
        FuelStatisticDTOFilter filter = FuelStatisticDTOFilter.builder()
                .startTime(startTime)
                .endTime(endTime)
                .build();
        List stationIds = new ArrayList();
        stationIds.add(1);
        stationIds.add(2);
        Mockito.when(userHelper.getListStationIdOfOwner(userHelper.getUserLogin()))
                .thenReturn(stationIds);
        Mockito.when(dashboardCriteria.fuelStatistic(filter)).thenReturn(temp);
        HashMap<String, Object> map;
        map = dashboardService.fuelStatistic(filter);
        List<FuelStatisticDTO> fuelStatisticDTOList = (List<FuelStatisticDTO>) map.get("data");
        for (int i = 0; i < fuelStatisticList.size(); i++) {
            FuelStatisticDTO o1 = DashboardMapper.toFuelStatisticDTO(fuelStatisticList.get(i));
            FuelStatisticDTO o2 = fuelStatisticDTOList.get(i);
            assertEquals(o1, o2);
        }
    }

    @Test
    void tankStatistic() {
        Long startTime = 16094540000000L;
        Long endTime = 16094640000000L;
        Station station = Station.builder()
                .id(1)
                .name("Hoang Long")
                .address("Me Linh- Ha Noi")
                .longitude(39d)
                .latitude(46d)
                .build();
        Fuel fuel = Fuel.builder()
                .id(23)
                .name("E92")
                .unit("litter")
                .price(32460d)
                .type("TYPE_E92").build();
        Tank tank = Tank.builder()
                .id(100)
                .name("tank_100")
                .volume(900d)
                .remain(20d)
                .currentPrice(505500d)
                .station(station)
                .fuel(fuel).build();

        List<TankStatistic> tankStatisticList = new ArrayList<>();
        tankStatisticList.add(TankStatistic.builder()
                .tank(tank)
                .totalImport(123456789d)
                .totalExport(234567899d)
                .build());
        tankStatisticList.add(TankStatistic.builder()
                .tank(tank)
                .totalImport(123456780d)
                .totalExport(234567890d)
                .build());
        HashMap<String, Object> temp = new HashMap<>();
        temp.put("totalElement", 10);
        temp.put("data", tankStatisticList);
        temp.put("totalPage", 3);
        TankStatisticDTOFilter filter = TankStatisticDTOFilter.builder()
                .startTime(startTime)
                .endTime(endTime)
                .build();
        List stationIds = new ArrayList();
        stationIds.add(1);
        stationIds.add(2);
        Mockito.when(userHelper.getListStationIdOfOwner(userHelper.getUserLogin()))
                .thenReturn(stationIds);
        Mockito.when(dashboardCriteria.tankStatistic(filter)).thenReturn(temp);
        HashMap<String, Object> map;
        map = dashboardService.tankStatistic(filter);
        List<TankStatisticDTO> tankStatisticDTOS = (List<TankStatisticDTO>) map.get("data");
        for (int i = 0; i < tankStatisticDTOS.size(); i++) {
            TankStatisticDTO o1 = DashboardMapper.toTankStatisticDTO(tankStatisticList.get(i));
            TankStatisticDTO o2 = tankStatisticDTOS.get(i);
            assertEquals(o1, o2);
        }
    }
}