package com.gasstation.managementsystem.service.impl;

import com.gasstation.managementsystem.entity.*;
import com.gasstation.managementsystem.exception.custom.CustomNotFoundException;
import com.gasstation.managementsystem.model.dto.pumpShift.PumpShiftDTO;
import com.gasstation.managementsystem.model.dto.pumpShift.PumpShiftDTOFilter;
import com.gasstation.managementsystem.model.dto.pump.PumpDTO;
import com.gasstation.managementsystem.model.dto.shift.ShiftDTO;
import com.gasstation.managementsystem.model.dto.station.StationDTO;
import com.gasstation.managementsystem.model.dto.tank.TankDTO;
import com.gasstation.managementsystem.model.dto.user.UserDTO;
import com.gasstation.managementsystem.model.dto.userType.UserTypeDTO;
import com.gasstation.managementsystem.model.mapper.PumpShiftMapper;
import com.gasstation.managementsystem.repository.PumpShiftRepository;
import com.gasstation.managementsystem.repository.criteria.PumpShiftRepositoryCriteria;
import com.gasstation.managementsystem.utils.DateTimeHelper;
import com.gasstation.managementsystem.utils.OptionalValidate;
import com.gasstation.managementsystem.utils.UserHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PumpShiftServiceImplTest {
    @Mock
    private EntityManager em;

    @Mock
    private PumpShiftRepository pumpShiftRepository;

    @Mock
    private PumpShiftRepositoryCriteria pumpShiftCriteria;

    @Mock
    private OptionalValidate optionalValidate;

    @Mock
    private UserHelper userHelper;

    @InjectMocks
    private PumpShiftServiceImpl pumpShiftService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        List<PumpShift> mockRepository = new ArrayList<>();
        List<PumpShiftDTO> mockResult = new ArrayList<>();
        mockData(mockRepository, mockResult);

        Long createDate = 16094340000000L;
        Long closedTime = 16094440000000L;
        final int PAGE_INDEX = 1;
        final int PAGE_SIZE = 3;
        Integer[] shiftIds = new Integer[1];
        shiftIds[0] = 1;
        Integer[] pumpIds = new Integer[1];
        pumpIds[0] = 1;
        Integer[] stationIds = new Integer[1];
        stationIds[0] = 1;

        PumpShiftDTOFilter filter = PumpShiftDTOFilter.builder()
                .pageIndex(PAGE_INDEX)
                .pageSize(PAGE_SIZE)
                .createdDateTo(16094340000000L)
                .createdDateTo(16094540000000L)
                .closedTimeFrom(16094640000000L)
                .closedTimeTo(16094640000000L)
                .pumpName("pumpName")
                .stationIds(null)
                .build();

        HashMap<String, Object> temp = new HashMap<>();
        temp.put("totalElement",1);
        temp.put("data",mockRepository);
        temp.put("totalPage",3);
        Mockito.when(pumpShiftRepository.findAll()).thenReturn(mockRepository);
        List<Station>stationList = new ArrayList<>();
        stationList.add(Station.builder().id(1).build());
        stationList.add(Station.builder().id(2).build());
        User user = User.builder().id(2)
                .userType(UserType.builder().id(2).type("OWNER").build())
                .stationList(stationList)
                .build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(pumpShiftCriteria.findAll(Mockito.any(PumpShiftDTOFilter.class))).thenReturn(temp);
        List<PumpShiftDTO> listResultService = (List<PumpShiftDTO>)
                pumpShiftService.findAll(filter).get("data");
        for (int i = 0; i < listResultService.size(); i++) {
            PumpShiftDTO o1 = mockResult.get(i);
            PumpShiftDTO o2 = listResultService.get(i);
            assertEquals(o1, o2);
        }
    }

    private void mockData(List<PumpShift> mockRepository, List<PumpShiftDTO> mockResult) {
        for (int i = 1; i <= 10; i++) {
            Long startTime = 25200000L;
            Long endTime = 43200000L;
            Long createDate = 16094240000000L;
            Long closedTime = 16094540000000L;
            PumpShift pumpShift = PumpShift.builder()
                    .id(i)
                    .shift(Shift.builder().id(1).startTime(startTime).endTime(endTime).station(Station.builder().id(1).build()).build())
                    .pump(Pump.builder().id(1).build())
                    .executor((User.builder().id(3).name("EMPLOYEE").build()))
                    .closedTime(closedTime)
                    .createdDate(createDate).build();
            mockRepository.add(pumpShift);
            mockResult.add(PumpShiftMapper.toPumpShiftDTO(pumpShift));
        }
    }
    /**
     * param Login is OWNER
     */
    @Test
    void findById_UTCID01() throws CustomNotFoundException {
        User user = User.builder().id(1).userType(UserType.builder().id(2).type("OWNER").build()).build();
        User user1 = User.builder().id(2).userType(UserType.builder().id(2).type("OWNER").build()).build();
        UserDTO userDTO = UserDTO.builder().id(2).userType(UserTypeDTO.builder().id(2).type("OWNER").build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Long startTime = 25200000L;
        Long endTime = 43200000L;
        Long createDate = 16094340000000L;
        Long closedTime = 16094440000000L;
        Tank tank = Tank.builder().id(1).name("tank1")
                .station(Station.builder().id(1).owner(user1).build())
                .volume(333d)
                .currentPrice(444d)
                .remain(0d).build();
        TankDTO tankDTO = TankDTO.builder().id(1).name("tank1")
                .station(StationDTO.builder().id(1).owner(userDTO).build())
                .volume(333d)
                .currentPrice(444d)
                .remain(0d).build();
        PumpShift pumpShift = PumpShift.builder()
                .id(1)
                .shift(Shift.builder().id(1).startTime(startTime).endTime(endTime).build())
                .pump(Pump.builder().tank(tank).id(1).build())
                .executor((User.builder().id(3).name("EMPLOYEE").build()))
                .closedTime(closedTime)
                .createdDate(createDate).build();
        PumpShiftDTO mockResult = PumpShiftDTO.builder()
                .id(1)
                .shift(ShiftDTO.builder().id(1).startTime("07:00")
                        .endTime("12:00")
                        .station(StationDTO.builder().id(1).build()).build())
                .pump(PumpDTO.builder().tank(tankDTO).id(1).build())
                .executor((UserDTO.builder().id(3).name("EMPLOYEE").build()))
                .closedTime(closedTime)
                .createdDate(createDate)
                .build();
        List<PumpShift> pumpShifts = new ArrayList<>();
        pumpShifts.add(pumpShift);
        HashMap<String, Object> map = new HashMap<>();
        map.put("data",pumpShifts);
        map.put("totalVolume",1000d);
        map.put("totalAmount",10000d);
        Mockito.when(pumpShiftCriteria.findAll(Mockito.any(PumpShiftDTOFilter.class))).thenReturn(map);
        Mockito.when(optionalValidate.getPumpShiftById(1)).thenReturn(pumpShift);
        assertThrows(CustomNotFoundException.class, () -> {
            assertEquals(mockResult, pumpShiftService.findById(1));
        });
    }
    /**
     * param Login is ADMIN
     */
    @Test
    void findById_UTCID02() throws CustomNotFoundException {
        User user = User.builder().id(1).userType(UserType.builder().id(2).type("ADMIN").build()).build();
        UserDTO userDTO = UserDTO.builder().id(2).userType(UserTypeDTO.builder().id(2).type("ADMIN").build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Long startTime = 25200000L;
        Long endTime = 43200000L;
        Long createDate = 16094340000000L;
        Long closedTime = 16094440000000L;
        Tank tank = Tank.builder().id(1).name("tank1")
                .station(Station.builder().id(1).owner(user).build())
                .volume(333d)
                .currentPrice(444d)
                .remain(0d).build();
        TankDTO tankDTO = TankDTO.builder().id(1).name("tank1")
                .station(StationDTO.builder().id(1).owner(userDTO).build())
                .volume(333d)
                .currentPrice(444d)
                .remain(0d).build();
        PumpShift pumpShift = PumpShift.builder()
                .id(1)
                .shift(Shift.builder().id(1).startTime(startTime).endTime(endTime).build())
                .pump(Pump.builder().tank(tank).id(1).build())
                .executor((User.builder().id(3).name("EMPLOYEE").build()))
                .closedTime(closedTime)
                .createdDate(createDate).build();
        PumpShiftDTO mockResult = PumpShiftDTO.builder()
                .id(1)
                .shift(ShiftDTO.builder().id(1).startTime("07:00")
                        .endTime("12:00")
                        .station(StationDTO.builder().id(1).build()).build())
                .pump(PumpDTO.builder().tank(tankDTO).id(1).build())
                .executor((UserDTO.builder().id(3).name("EMPLOYEE").build()))
                .closedTime(closedTime)
                .createdDate(createDate)
                .build();
        List<PumpShift> pumpShifts = new ArrayList<>();
        pumpShifts.add(pumpShift);
        HashMap<String, Object> map = new HashMap<>();
        map.put("data",pumpShifts);
        map.put("totalVolume",1000d);
        map.put("totalAmount",10000d);
        Mockito.when(pumpShiftCriteria.findAll(Mockito.any(PumpShiftDTOFilter.class))).thenReturn(map);
        Mockito.when(optionalValidate.getPumpShiftById(1)).thenReturn(pumpShift);
        PumpShiftDTO act =  pumpShiftService.findById(1);
        assertEquals(mockResult.getId(), act.getId());
        assertEquals(mockResult.getExecutor(), act.getExecutor());
        assertEquals(mockResult.getPump().getId(), act.getPump().getId());
        assertEquals(mockResult.getShift().getId(), act.getShift().getId());
        assertEquals(mockResult.getClosedTime(), act.getClosedTime());
    }
    @Test
    void update() throws CustomNotFoundException {
        Long startTime = 25200000L;
        Long endTime = 43200000L;
        Long createDate = 16094340000000L;
        PumpShift pumpShift = PumpShift.builder()
                .id(1)
                .shift(Shift.builder().id(1).startTime(startTime).endTime(endTime).build())
                .pump(Pump.builder().id(1).build())
                .closedTime(DateTimeHelper.getCurrentUnixTime())
                .createdDate(createDate).build();
        PumpShiftDTO mockResult = PumpShiftDTO.builder()
                .id(1)
                .shift(ShiftDTO.builder().id(1).startTime("07:00")
                        .endTime("12:00")
                        .build())
                .pump(PumpDTO.builder().id(1).build())
                .closedTime(DateTimeHelper.getCurrentUnixTime())
                .createdDate(createDate)
                .build();
        User user = User.builder().id(1).name("OWNER").build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(optionalValidate.getPumpShiftById(1)).thenReturn(pumpShift);
        Mockito.when(pumpShiftRepository.save(Mockito.any(PumpShift.class)))
                .thenReturn(pumpShift);
        assertEquals(mockResult.getShift(), pumpShiftService.update(1).getShift());
    }

    @Test
    void updateAllByStationId() throws CustomNotFoundException {
        Long createDate = 16094340000000L;
        Station station = Station.builder().id(1).build();
        StationDTO stationDTO = StationDTO.builder().id(1).build();
        PumpShift pumpShift = PumpShift.builder()
                .id(1)
                .pump(Pump.builder().id(1).tank(Tank.builder().station(station).build()).build())
                .createdDate(createDate)
                .build();
        PumpShiftDTO mockResult = PumpShiftDTO.builder()
                .id(1)
                .pump(PumpDTO.builder().id(1).tank(TankDTO.builder().station(stationDTO).build()).build())
                .createdDate(createDate)
                .build();
        List<PumpShift> pumpShiftList = new ArrayList<>();
        pumpShiftList.add(pumpShift);
//     User user = User.builder().id(1).name("OWNER").build();
//    Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(optionalValidate.getStationById(1)).thenReturn(station);
        Mockito.when(pumpShiftRepository.save(Mockito.any(PumpShift.class))).thenReturn(pumpShift);
        Mockito.when(pumpShiftRepository.findAllByStationId(Mockito.anyInt(),Mockito.anyLong()))
                .thenReturn(pumpShiftList);
        pumpShiftService.updateAllByStationId(1);
//        assertEquals(mockResult, handOverShiftService.updateAllByStationId(1).get("data"));
    }
}