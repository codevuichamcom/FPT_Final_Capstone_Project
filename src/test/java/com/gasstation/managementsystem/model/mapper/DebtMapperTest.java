package com.gasstation.managementsystem.model.mapper;

import com.gasstation.managementsystem.entity.*;
import com.gasstation.managementsystem.model.dto.debt.DebtDTO;
import com.gasstation.managementsystem.model.dto.fuel.FuelDTO;
import com.gasstation.managementsystem.model.dto.station.StationDTO;
import com.gasstation.managementsystem.model.dto.tank.TankDTO;
import com.gasstation.managementsystem.utils.DateTimeHelper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DebtMapperTest {
//    /**
//     * param debt is null
//     */
//    @Test
//    void toDebtDTO_UTCID01() {
//        assertNull(DebtMapper.toDebtDTO(null));
//    }

    /**
     * param debt is not null
     */
    @Test
    void toDebtDTO_UTCID02() {
        //given
        long time = DateTimeHelper.toUnixTime("2021-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
        User customer = User.builder()
                .id(1)
                .name("customer").build();
        Card card = Card.builder()
                .id(UUID.fromString("d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8"))
                .customer(customer)
                .availableBalance(100d)
                .accountsPayable(20d)
                .build();
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
        Tank tank = Tank.builder()
                .id(100)
                .name("tank_100")
                .volume(900d)
                .remain(20d)
                .currentPrice(505500d)
                .station(station)
                .fuel(fuel).build();
        TankDTO tankDTO = TankDTO.builder()
                .id(100)
                .name("tank_100")
                .volume(900d)
                .remain(20d)
                .currentPrice(505500d)
                .station(stationDTO)
                .fuel(fuelDTO).build();
        Pump pump = Pump.builder()
                .id(333)
                .name("Pump_333")
                .tank(tank)
                .note("pump333").build();
        PumpShift pumpShift = PumpShift.builder()
                .id(1)
                .shift(Shift.builder()
                        .id(1)
                        .name("shift")
                        .station(station)
                        .build())
                .pump(pump)
                .build();
        Transaction transaction = Transaction.builder()
                .id(1)
                .time(time)
                .volume(123d)
                .uuid("123456")
                .card(card)
                .pumpShift(pumpShift)
                .unitPrice(50500d).build();

        Long createdDate = 16094340000000L;
        Debt debt = Debt.builder()
                .id(1)
                .accountsPayable(100d)
                .transaction(transaction)
                .build();

        DebtDTO debtDTO = DebtMapper.toDebtDTO(debt);
        assertEquals(debt.getId(), debtDTO.getId());
        assertEquals(debt.getTransaction().getId(), debtDTO.getTransaction().getId());
    }
}