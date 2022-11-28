package com.gasstation.managementsystem.model.mapper;

import com.gasstation.managementsystem.entity.Fuel;
import com.gasstation.managementsystem.entity.Station;
import com.gasstation.managementsystem.entity.Tank;
import com.gasstation.managementsystem.model.FuelStatistic;
import com.gasstation.managementsystem.model.TankStatistic;
import com.gasstation.managementsystem.model.dto.dashboard.FuelStatisticDTO;
import com.gasstation.managementsystem.model.dto.dashboard.TankStatisticDTO;
import com.gasstation.managementsystem.model.dto.debt.DebtDTO;
import com.gasstation.managementsystem.model.dto.fuel.FuelDTO;
import com.gasstation.managementsystem.model.dto.station.StationDTO;
import com.gasstation.managementsystem.model.dto.tank.TankDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DashboardMapperTest {
    /**
     * param fuelStatistic is null
     */
    @Test
    void toFuelStatisticDTO_UTCID01() {
        assertNull(DashboardMapper.toFuelStatisticDTO(null));
    }
    /**
     * param fuelStatistic is not null
     */
    @Test
    void toFuelStatisticDTO_UTCID02() {
        FuelDTO fuelDTO = FuelDTO.builder()
                .id(1)
                .name("E92")
                .unit("litter")
                .price(32460d)
                .type("TYPE_E92").build();
        StationDTO stationDTO = StationDTO.builder()
                .id(1)
                .name("Hoang Long")
                .address("Me Linh- Ha Noi")
                .longitude(39d)
                .latitude(46d)
                .build();
        FuelStatistic fuelStatistic = FuelStatistic.builder()
                .fuelId(1)
                .totalCash(1000000d)
                .totalDebt(10000d)
                .totalRevenue(2000000d)
                .totalVolume(20000d)
                .build();
        FuelStatisticDTO result = FuelStatisticDTO.builder()
                .fuel(fuelDTO)
                .station(stationDTO)
                .totalCash(1000000d)
                .totalDebt(10000d)
                .totalRevenue(2000000d)
                .totalVolume(20000d)
                .build();
        FuelStatisticDTO actual = DashboardMapper.toFuelStatisticDTO(fuelStatistic);
        assertEquals(result.getTotalCash(), actual.getTotalCash());
        assertEquals(result.getTotalDebt(), actual.getTotalDebt());
        assertEquals(result.getTotalRevenue(), actual.getTotalRevenue());
        assertEquals(result.getTotalVolume(), actual.getTotalVolume());
    }
    /**
     * param tankStatistic is null
     */
    @Test
    void toTankStatisticDTO_UTCID01() {
        assertNull(DashboardMapper.toTankStatisticDTO(null));
    }
    /**
     * param tankStatistic is not null
     */
    @Test
    void toTankStatisticDTO_UTCID02() {
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
        StationDTO stationDTO = StationDTO.builder()
                .id(1)
                .name("Hoang Long")
                .address("Me Linh- Ha Noi")
                .longitude(39d)
                .latitude(46d)
                .build();
        FuelDTO fuelDTO = FuelDTO.builder()
                .id(23)
                .name("E92")
                .unit("litter")
                .price(32460d)
                .type("TYPE_E92").build();


        TankStatistic tankStatistic = TankStatistic.builder()
                .tank(tank)
                .totalImport(123456789d)
                .totalExport(234567899d)
                .build();

        TankDTO tankDTO = TankDTO.builder()
                .id(100)
                .name("tank_100")
                .volume(900d)
                .remain(20d)
                .currentPrice(505500d)
                .station(stationDTO)
                .fuel(fuelDTO).build();

        TankStatisticDTO result = TankStatisticDTO.builder()
                .tank(tankDTO)
                .totalImport(123456789d)
                .totalExport(234567899d)
                .build();
        TankStatisticDTO actual = DashboardMapper.toTankStatisticDTO(tankStatistic);
        assertEquals(result.getTank().getId(), actual.getTank().getId());
        assertEquals(result.getTank().getName(), actual.getTank().getName());
        assertEquals(result.getTank().getStation().getAddress(),actual.getTank().getStation().getAddress());
        assertEquals(result.getTank().getStation().getId(),actual.getTank().getStation().getId());
        assertEquals(result.getTank().getFuel().getName(),actual.getTank().getFuel().getName());
        assertEquals(result.getTank().getFuel().getId(),actual.getTank().getFuel().getId());
        assertEquals(result.getTotalImport(),actual.getTotalImport());
        assertEquals(result.getTotalExport(),actual.getTotalExport());
    }
}