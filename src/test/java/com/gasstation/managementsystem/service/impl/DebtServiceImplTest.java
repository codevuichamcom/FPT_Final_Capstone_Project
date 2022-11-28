package com.gasstation.managementsystem.service.impl;

import com.gasstation.managementsystem.entity.*;
import com.gasstation.managementsystem.exception.custom.CustomNotFoundException;
import com.gasstation.managementsystem.model.dto.card.CardDTO;
import com.gasstation.managementsystem.model.dto.debt.*;
import com.gasstation.managementsystem.model.dto.station.StationDTO;
import com.gasstation.managementsystem.model.dto.user.UserDTO;
import com.gasstation.managementsystem.model.mapper.DebtMapper;
import com.gasstation.managementsystem.repository.DebtRepository;
import com.gasstation.managementsystem.repository.ReceiptRepository;
import com.gasstation.managementsystem.repository.criteria.DebtRepositoryCriteria;
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

class DebtServiceImplTest {
    @Mock
    private DebtRepositoryCriteria debtCriteria;
    @Mock
    private DebtRepository debtRepository;
    @Mock
    private ReceiptRepository receiptRepository;
    @Mock
    private OptionalValidate optionalValidate;
    @Mock
    private UserHelper userHelper;
    @InjectMocks
    private DebtServiceImpl debtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * param login is OWNER
     */
    @Test
    void summary_UTCID01() {
        Integer pageIndex = 1;
        Integer pageSize = 3;
        String cardId = "d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8";
        Integer[] stationIds = new Integer[1];
        stationIds[0] = 1;
        String customerName = "customer test";
        String customerPhone = "123456789";
        Double totalAccountsPayable = 100d;
        DebtDTOSummaryFilter filter = DebtDTOSummaryFilter.builder()
                .pageIndex(pageIndex)
                .pageSize(pageSize)
                .cardId(cardId)
                .customerName(customerName)
                .build();

        UserDTO customer = UserDTO.builder()
                .id(1)
                .name("customer").build();
        CardDTO cardDTO = CardDTO.builder()
                .id(UUID.fromString("d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8"))
                .customer(customer)
                .availableBalance(100d)
                .accountsPayable(20d)
                .build();
        StationDTO stationDTO = StationDTO.builder()
                .id(1)
                .name("Hoang Long")
                .address("Me Linh- Ha Noi")
                .longitude(39d)
                .latitude(46d)
                .build();
        DebtDTOSummary debtDTOSummary1 = DebtDTOSummary.builder()
                .card(cardDTO)
                .station(stationDTO)
                .totalAccountsPayable(1000d)
                .build();
        DebtDTOSummary debtDTOSummary2 = DebtDTOSummary.builder()
                .card(cardDTO)
                .station(stationDTO)
                .totalAccountsPayable(2000d)
                .build();
        List<DebtDTOSummary> debtDTOSummaryList = new ArrayList<>();
        debtDTOSummaryList.add(debtDTOSummary1);
        debtDTOSummaryList.add(debtDTOSummary2);
        HashMap<String, Object> temp = new HashMap<>();
        temp.put("totalElement", 10);
        temp.put("data", debtDTOSummaryList);
        temp.put("totalPage", 3);

        User user = User.builder().id(1).userType(UserType.builder().id(2).type("OWNER").build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(debtCriteria.summary(filter)).thenReturn(temp);
        HashMap<String, Object> map = debtService.summary(filter);

        List<DebtDTOSummary> debtDTOSummaries = (List<DebtDTOSummary>) map.get("data");
        for (int i = 0; i < debtDTOSummaries.size(); i++) {
            DebtDTOSummary o1 = debtDTOSummaryList.get(i);
            DebtDTOSummary o2 = debtDTOSummaries.get(i);
            assertEquals(o1, o2);
        }
    }
    /**
     * param login is CUSTOMER
     */
    @Test
    void summary_UTCID02() {
        Integer pageIndex = 1;
        Integer pageSize = 3;
        String cardId = "d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8";
        Integer[] stationIds = new Integer[1];
        stationIds[0] = 1;
        String customerName = "customer test";
        String customerPhone = "123456789";
        Double totalAccountsPayable = 100d;
        DebtDTOSummaryFilter filter = DebtDTOSummaryFilter.builder()
                .pageIndex(pageIndex)
                .pageSize(pageSize)
                .cardId(cardId)
                .customerName(customerName)
                .build();

        UserDTO customer = UserDTO.builder()
                .id(1)
                .name("customer").build();
        CardDTO cardDTO = CardDTO.builder()
                .id(UUID.fromString("d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8"))
                .customer(customer)
                .availableBalance(100d)
                .accountsPayable(20d)
                .build();
        StationDTO stationDTO = StationDTO.builder()
                .id(1)
                .name("Hoang Long")
                .address("Me Linh- Ha Noi")
                .longitude(39d)
                .latitude(46d)
                .build();
        DebtDTOSummary debtDTOSummary1 = DebtDTOSummary.builder()
                .card(cardDTO)
                .station(stationDTO)
                .totalAccountsPayable(1000d)
                .build();
        DebtDTOSummary debtDTOSummary2 = DebtDTOSummary.builder()
                .card(cardDTO)
                .station(stationDTO)
                .totalAccountsPayable(2000d)
                .build();
        List<DebtDTOSummary> debtDTOSummaryList = new ArrayList<>();
        debtDTOSummaryList.add(debtDTOSummary1);
        debtDTOSummaryList.add(debtDTOSummary2);
        HashMap<String, Object> temp = new HashMap<>();
        temp.put("totalElement", 10);
        temp.put("data", debtDTOSummaryList);
        temp.put("totalPage", 3);

        User user = User.builder().id(1).userType(UserType.builder().id(4).type("CUSTOMER").build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(debtCriteria.summary(filter)).thenReturn(temp);
        HashMap<String, Object> map = debtService.summary(filter);

        List<DebtDTOSummary> debtDTOSummaries = (List<DebtDTOSummary>) map.get("data");
        for (int i = 0; i < debtDTOSummaries.size(); i++) {
            DebtDTOSummary o1 = debtDTOSummaryList.get(i);
            DebtDTOSummary o2 = debtDTOSummaries.get(i);
            assertEquals(o1, o2);
        }
    }
    @Test
    void getDetail() {
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

        Debt debt1 = Debt.builder()
                .id(1)
                .accountsPayable(100d)
                .transaction(transaction)
                .build();
        Debt debt2 = Debt.builder()
                .id(2)
                .accountsPayable(200d)
                .transaction(transaction)
                .build();
        List<Debt> debtList = new ArrayList<>();
        debtList.add(debt1);
        debtList.add(debt2);

        HashMap<String, Object> temp = new HashMap<>();
        temp.put("totalElement", 10);
        temp.put("data", debtList);
        temp.put("totalPage", 3);

        DebtDTOFilter filter = DebtDTOFilter.builder()
                .cardId(UUID.fromString("d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8"))
                .stationId(1)
                .build();
        Mockito.when(debtCriteria.getDetail(filter)).thenReturn(temp);
        HashMap<String, Object> map;
        map = debtService.getDetail(filter);
        List<DebtDTO> debtDTOList = (List<DebtDTO>) map.get("data");
        for (int i = 0; i < debtDTOList.size(); i++) {
            DebtDTO o1 = DebtMapper.toDebtDTO(debtList.get(i));
            DebtDTO o2 = debtDTOList.get(i);
            assertEquals(o1, o2);
        }
    }

    /**
     * param debtDTOPays is null
     */
    @Test
    void payDebts_UTCID01() throws CustomNotFoundException {
        List<DebtDTOPay> debtDTOPays = new ArrayList<>();
        debtService.payDebts(debtDTOPays);
    }

    /**
     * param debtDTOPays is not null
     */
    @Test
    void payDebts_UTCID02() throws CustomNotFoundException {
        //given
        User customer = User.builder().id(1)
                .userType(UserType.builder().id(3).type("CUSTOMER").build())
                .build();
        long time = DateTimeHelper.toUnixTime("2021-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
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
        Receipt receipt = Receipt.builder().id(1)
                .card(Card.builder().id(UUID.fromString("d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8")).build())
                .createdDate(createdDate)
                .amount(100d)
                .creator(User.builder().id(1).build())
                .reason("reason")
                .build();

        Debt debt = Debt.builder()
                .id(1)
                .accountsPayable(100d)
                .transaction(transaction)
                .build();

        List<DebtDTOPay> debtDTOPays = new ArrayList<>();
        DebtDTOPay debtDTOPay1 = DebtDTOPay.builder()
                .debtId(1)
                .transactionId(1)
                .cardId(UUID.fromString("d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8"))
                .build();
        DebtDTOPay debtDTOPay2 = DebtDTOPay.builder()
                .debtId(1)
                .transactionId(1)
                .cardId(UUID.fromString("d9ae22ee-3153-4e2b-9f9d-86ee38d5a7c8"))
                .build();
        debtDTOPays.add(debtDTOPay1);
        debtDTOPays.add(debtDTOPay2);

        Mockito.when(userHelper.getUserLogin()).thenReturn(customer);
        Mockito.when(optionalValidate.getDebtById(1)).thenReturn(debt);
        Mockito.when(optionalValidate.getCardById(debtDTOPay1.getCardId())).thenReturn(card);
        Mockito.when(optionalValidate.getTransactionById(1)).thenReturn(transaction);
        debtRepository.delete(debt);
        Mockito.when(receiptRepository.save(Mockito.any(Receipt.class))).thenReturn(receipt);
        debtService.payDebts(debtDTOPays);
    }
}