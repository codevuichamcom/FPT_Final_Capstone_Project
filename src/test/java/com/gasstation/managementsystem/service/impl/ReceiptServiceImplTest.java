package com.gasstation.managementsystem.service.impl;

import com.gasstation.managementsystem.entity.*;
import com.gasstation.managementsystem.exception.custom.CustomNotFoundException;
import com.gasstation.managementsystem.model.dto.card.CardDTO;
import com.gasstation.managementsystem.model.dto.fuel.FuelDTO;
import com.gasstation.managementsystem.model.dto.pump.PumpDTO;
import com.gasstation.managementsystem.model.dto.pumpShift.PumpShiftDTO;
import com.gasstation.managementsystem.model.dto.receipt.ReceiptDTO;
import com.gasstation.managementsystem.model.dto.receipt.ReceiptDTOFilter;
import com.gasstation.managementsystem.model.dto.shift.ShiftDTO;
import com.gasstation.managementsystem.model.dto.station.StationDTO;
import com.gasstation.managementsystem.model.dto.tank.TankDTO;
import com.gasstation.managementsystem.model.dto.transaction.TransactionDTO;
import com.gasstation.managementsystem.model.dto.user.UserDTO;
import com.gasstation.managementsystem.model.dto.userType.UserTypeDTO;
import com.gasstation.managementsystem.model.mapper.ReceiptMapper;
import com.gasstation.managementsystem.repository.ReceiptRepository;
import com.gasstation.managementsystem.repository.criteria.ReceiptRepositoryCriteria;
import com.gasstation.managementsystem.utils.DateTimeHelper;
import com.gasstation.managementsystem.utils.OptionalValidate;
import com.gasstation.managementsystem.utils.UserHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReceiptServiceImplTest {
    @Mock
    private ReceiptRepository receiptRepository;
    @Mock
    private OptionalValidate optionalValidate;
    @Mock
    private UserHelper userHelper;
    @Mock
    private ReceiptRepositoryCriteria receiptCriteria;
    @InjectMocks
    private ReceiptServiceImpl receiptService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        List<Receipt> mockRepository = new ArrayList<>();
        List<ReceiptDTO> mockResult = new ArrayList<>();
        mockData(mockRepository, mockResult);
        ReceiptDTOFilter filter = ReceiptDTOFilter.builder().amountFrom(100d).amountTo(200d).build();
        HashMap<String, Object> temp = new HashMap<>();
        temp.put("totalElement", 10);
        temp.put("data", mockRepository);
        temp.put("totalPage", 3);
        User user = User.builder().id(1).userType(UserType.builder().id(2).type("OWNER").build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(receiptCriteria.findAll(Mockito.any(ReceiptDTOFilter.class))).thenReturn(temp);
        Mockito.when(receiptRepository.findAll(Sort.by(Sort.Direction.DESC, "createdDate"))).thenReturn(mockRepository);
        List<ReceiptDTO> listResultService = (List<ReceiptDTO>) receiptService.findAll(filter).get("data");
        for (int i = 0; i < listResultService.size(); i++) {
            ReceiptDTO o1 = mockResult.get(i);
            ReceiptDTO o2 = listResultService.get(i);
            assertEquals(o1, o2);
        }
    }

    private void mockData(List<Receipt> mockRepository, List<ReceiptDTO> mockResult) {
        Long createdDate = 16094340000000L;
        for (int i = 1; i <= 10; i++) {
            Receipt receipt = Receipt.builder().id(1)
                    .card(Card.builder().id(UUID.fromString("d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8")).build())
                    .createdDate(createdDate)
                    .amount(100d)
                    .creator(User.builder().id(1).build())
                    .reason("reason")
                    .build();
            mockRepository.add(receipt);
            mockResult.add(ReceiptMapper.toReceiptDTO(receipt));
        }
    }

    /**
     * param Receipt not of the owner
     */
    @Test
    void findById_UTCID01() throws CustomNotFoundException {
        long time = DateTimeHelper.toUnixTime("2021-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
        UserDTO customerDTO = UserDTO.builder()
                .id(1)
                .userType(UserTypeDTO.builder().id(2).build())
                .name("customer").build();
        CardDTO cardDTO = CardDTO.builder()
                .id(UUID.fromString("d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8"))
                .customer(customerDTO)
                .build();

        StationDTO stationDTO = StationDTO.builder()
                .id(1)
                .name("Hoang Long")
                .address("Me Linh- Ha Noi")
                .longitude(39d)
                .latitude(46d)
                .owner(customerDTO)
                .build();
        FuelDTO fuelDTO = FuelDTO.builder()
                .id(23)
                .name("E92")
                .unit("litter")
                .price(32460d)
                .type("TYPE_E92").build();
        TankDTO tankDTO = TankDTO.builder()
                .id(100)
                .name("tank_100")
                .volume(900d)
                .remain(20d)
                .currentPrice(505500d)
                .station(stationDTO)
                .fuel(fuelDTO).build();
        PumpDTO pumpDTO = PumpDTO.builder()
                .id(333)
                .name("Pump_333")
                .tank(tankDTO)
                .note("pump333").build();
        TransactionDTO transactionDTO = TransactionDTO
                .builder()
                .id(1)
                .time(time)
                .volume(123d)
                .uuid("123456")
                .card(cardDTO)
                .pumpShift(PumpShiftDTO.builder()
                        .id(1)
                        .shift(ShiftDTO.builder()
                                .id(1)
                                .name("shift")
                                .build())
                        .pump(pumpDTO)
                        .build())
                .unitPrice(50500d).build();
        Long createdDate = 16094340000000L;
        User customer = User.builder()
                .id(1)
                .name("customer").build();

        Card card = Card.builder()
                .id(UUID.fromString("d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8"))
                .customer(customer)
                .build();

        Station station = Station.builder()
                .id(1)
                .name("Hoang Long")
                .address("Me Linh- Ha Noi")
                .owner(customer)
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
        Pump pump = Pump.builder()
                .id(333)
                .name("Pump_333")
                .tank(tank)
                .note("pump333").build();

        Transaction transaction = Transaction.builder()
                .id(1)
                .time(time)
                .volume(123d)
                .uuid("123456")
                .card(card)
                .pumpShift(PumpShift.builder()
                        .id(1)
                        .shift(Shift.builder()
                                .id(1)
                                .name("shift")
                                .build())
                        .pump(pump)
                        .build())
                .unitPrice(50500d).build();
        Receipt receipt = Receipt.builder().id(1)
                .card(Card.builder().id(UUID.fromString("d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8")).build())
                .createdDate(createdDate)
                .amount(100d)
                .creator(User.builder().id(1).build())
                .transaction(transaction)
                .reason("reason")
                .build();
        Mockito.when(optionalValidate.getReceiptById(1)).thenReturn(receipt);
        ReceiptDTO mockResult = ReceiptDTO.builder().id(1)
                .card(CardDTO.builder().id(UUID.fromString("d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8")).build())
                .createdDate(createdDate)
                .amount(100d)
                .creator(UserDTO.builder().id(1).build())
                .reason("reason")
                .transaction(transactionDTO)
                .build();
        User user = User.builder().id(2).userType(UserType.builder().id(2).type("OWNER").build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        assertThrows(CustomNotFoundException.class, () -> {
            assertEquals(mockResult, receiptService.findById(1));
        });
    }

    @Test
    void findById_UTCID02() throws CustomNotFoundException {
        long time = DateTimeHelper.toUnixTime("2021-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
        User customer = User.builder()
                .id(1)
                .userType(UserType.builder().id(2).build())
                .name("customer").build();
        Card card = Card.builder()
                .id(UUID.fromString("d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8"))
                .customer(customer)
                .build();

        Station station = Station.builder()
                .id(1)
                .name("Hoang Long")
                .address("Me Linh- Ha Noi")
                .owner(customer)
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
        Pump pump = Pump.builder()
                .id(333)
                .name("Pump_333")
                .tank(tank)
                .note("pump333").build();

        Transaction transaction = Transaction.builder()
                .id(1)
                .time(time)
                .volume(123d)
                .uuid("123456")
                .card(card)
                .pumpShift(PumpShift.builder()
                        .id(1)
                        .shift(Shift.builder()
                                .id(1)
                                .name("shift")
                                .build())
                        .pump(pump)
                        .build())
                .unitPrice(50500d).build();
        UserDTO customerDTO = UserDTO.builder()
                .id(1)
                .userType(UserTypeDTO.builder().id(2).build())
                .name("customer").build();
        CardDTO cardDTO = CardDTO.builder()
                .id(UUID.fromString("d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8"))
                .customer(customerDTO)
                .build();

        StationDTO stationDTO = StationDTO.builder()
                .id(1)
                .name("Hoang Long")
                .address("Me Linh- Ha Noi")
                .longitude(39d)
                .latitude(46d)
                .owner(customerDTO)
                .build();
        FuelDTO fuelDTO = FuelDTO.builder()
                .id(23)
                .name("E92")
                .unit("litter")
                .price(32460d)
                .type("TYPE_E92").build();
        TankDTO tankDTO = TankDTO.builder()
                .id(100)
                .name("tank_100")
                .volume(900d)
                .remain(20d)
                .currentPrice(505500d)
                .station(stationDTO)
                .fuel(fuelDTO).build();
        PumpDTO pumpDTO = PumpDTO.builder()
                .id(333)
                .name("Pump_333")
                .tank(tankDTO)
                .note("pump333").build();
        TransactionDTO transactionDTO = TransactionDTO
                .builder()
                .id(1)
                .time(time)
                .volume(123d)
                .uuid("123456")
                .card(cardDTO)
                .pumpShift(PumpShiftDTO.builder()
                        .id(1)
                        .shift(ShiftDTO.builder()
                                .id(1)
                                .name("shift")
                                .build())
                        .pump(pumpDTO)
                        .build())
                .unitPrice(50500d).build();
        Long createdDate = 16094340000000L;
        Receipt receipt = Receipt.builder().id(1)
                .card(Card.builder().id(UUID.fromString("d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8")).build())
                .createdDate(createdDate)
                .amount(100d)
                .transaction(transaction)
                .creator(User.builder().id(1).build())
                .reason("reason")
                .build();
        Mockito.when(optionalValidate.getReceiptById(1)).thenReturn(receipt);
        ReceiptDTO mockResult = ReceiptDTO.builder().id(1)
                .card(CardDTO.builder().id(UUID.fromString("d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8")).build())
                .createdDate(createdDate)
                .amount(100d)
                .creator(UserDTO.builder().id(1).build())
                .reason("reason")
                .transaction(transactionDTO)
                .build();
        User user = User.builder().id(1).userType(UserType.builder().id(2).type("OWNER").build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        ReceiptDTO actual = receiptService.findById(1);
        assertEquals(mockResult.getId(),actual.getId());
        assertEquals(mockResult.getAmount(),actual.getAmount());
        assertEquals(mockResult.getCreatedDate(),actual.getCreatedDate());
        assertEquals(mockResult.getReason(),actual.getReason());
        assertEquals(mockResult.getCard().getId(),actual.getCard().getId());
        assertEquals(mockResult.getTransaction().getId(),actual.getTransaction().getId());
    }
}