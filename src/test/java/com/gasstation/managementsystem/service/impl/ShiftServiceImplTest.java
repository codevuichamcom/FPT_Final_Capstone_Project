package com.gasstation.managementsystem.service.impl;

import com.gasstation.managementsystem.entity.Shift;
import com.gasstation.managementsystem.entity.Station;
import com.gasstation.managementsystem.entity.User;
import com.gasstation.managementsystem.entity.UserType;
import com.gasstation.managementsystem.exception.custom.CustomBadRequestException;
import com.gasstation.managementsystem.exception.custom.CustomDuplicateFieldException;
import com.gasstation.managementsystem.exception.custom.CustomNotFoundException;
import com.gasstation.managementsystem.model.dto.shift.ShiftDTO;
import com.gasstation.managementsystem.model.dto.shift.ShiftDTOCreate;
import com.gasstation.managementsystem.model.dto.shift.ShiftDTOUpdate;
import com.gasstation.managementsystem.model.dto.station.StationDTO;
import com.gasstation.managementsystem.model.dto.user.UserDTO;
import com.gasstation.managementsystem.model.mapper.ShiftMapper;
import com.gasstation.managementsystem.repository.ShiftRepository;
import com.gasstation.managementsystem.utils.OptionalValidate;
import com.gasstation.managementsystem.utils.UserHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShiftServiceImplTest {
    @Mock
    private ShiftRepository shiftRepository;
    @Mock
    private UserHelper userHelper;
    @Mock
    private OptionalValidate optionalValidate;

    @InjectMocks
    private ShiftServiceImpl shiftService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * param Login is ADMIN
     */
    @Test
    void findAll_UTCID01() {
        List<Shift> mockRepository = new ArrayList<>();
        List<ShiftDTO> mockResult = new ArrayList<>();
        mockData(mockRepository, mockResult);
        User user = User.builder().id(1).userType(UserType.builder().id(1).type("ADMIN").build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(shiftRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))).thenReturn(mockRepository);
        List<ShiftDTO> listResultService = (List<ShiftDTO>) shiftService.findAll().get("data");
        for (int i = 0; i < listResultService.size(); i++) {
            ShiftDTO o1 = mockResult.get(i);
            ShiftDTO o2 = listResultService.get(i);
            assertEquals(o1, o2);
        }
    }

    /**
     * param Login is OWNER
     */
    @Test
    void findAll_UTCID02() {
        List<Shift> mockRepository = new ArrayList<>();
        List<ShiftDTO> mockResult = new ArrayList<>();
        mockData(mockRepository, mockResult);
        User user = User.builder().id(1).userType(UserType.builder().id(2).type("OWNER").build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(shiftRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))).thenReturn(mockRepository);
        List<ShiftDTO> listResultService = (List<ShiftDTO>) shiftService.findAll().get("data");
        for (int i = 0; i < listResultService.size(); i++) {
            ShiftDTO o1 = mockResult.get(i);
            ShiftDTO o2 = listResultService.get(i);
            assertEquals(o1, o2);
        }
    }

    private void mockData(List<Shift> mockRepository, List<ShiftDTO> mockResult) {
        Long startTime = 25200000L;
        Long endTime = 43200000L;
        for (int i = 1; i <= 10; i++) {
            Shift shift = Shift.builder()
                    .id(1)
                    .name("Morning")
                    .startTime(startTime)
                    .endTime(endTime)
                    .station(Station.builder()
                            .id(1)
                            .name("station")
                            .address("address")
                            .owner(User.builder()
                                    .id(1)
                                    .name("owner")
                                    .build())
                            .build())
                    .build();
            mockRepository.add(shift);
            mockResult.add(ShiftMapper.toShiftDTO(shift));
        }
    }

    /**
     * Param Shift not of the owner
     */
    @Test
    void findById_UTCID01() throws CustomNotFoundException {
        User user = User.builder().id(1).userType(UserType.builder().id(2).type("OWNER").build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Long startTime = 25200000L;
        Long endTime = 43200000L;
        Shift shift = Shift.builder()
                .id(1)
                .name("Morning")
                .startTime(startTime)
                .endTime(endTime)
                .station(Station.builder()
                        .id(1)
                        .name("station")
                        .address("address")
                        .owner(User.builder()
                                .id(2)
                                .name("owner")
                                .build())
                        .build())
                .build();

        ShiftDTO mockResult = ShiftDTO.builder()
                .id(1)
                .name("Morning")
                .startTime("07:00")
                .endTime("12:00")
                .station(StationDTO.builder()
                        .id(1)
                        .name("station")
                        .address("address")
                        .owner(UserDTO.builder()
                                .id(2)
                                .name("owner")
                                .build())
                        .build())
                .build();
        Mockito.when(optionalValidate.getShiftById(1)).thenReturn(shift);
        assertThrows(CustomNotFoundException.class, () -> {
            assertEquals(mockResult, shiftService.findById(1));
        });
    }

    @Test
    void findById_UTCID02() throws CustomNotFoundException {
        User user = User.builder().id(1).userType(UserType.builder().id(2).type("OWNER").build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Long startTime = 25200000L;
        Long endTime = 43200000L;
        Shift shift = Shift.builder()
                .id(1)
                .name("Morning")
                .startTime(startTime)
                .endTime(endTime)
                .station(Station.builder()
                        .id(1)
                        .name("station")
                        .address("address")
                        .owner(User.builder()
                                .id(1)
                                .name("owner")
                                .build())
                        .build())
                .build();

        ShiftDTO mockResult = ShiftDTO.builder()
                .id(1)
                .name("Morning")
                .startTime("07:00")
                .endTime("12:00")
                .station(StationDTO.builder()
                        .id(1)
                        .name("station")
                        .address("address")
                        .owner(UserDTO.builder()
                                .id(1)
                                .name("owner")
                                .build())
                        .build())
                .build();
        Mockito.when(optionalValidate.getShiftById(1)).thenReturn(shift);
        assertEquals(mockResult, shiftService.findById(1));
    }

    @Test
    void create_UTCID01() throws CustomNotFoundException, CustomDuplicateFieldException {
        Long startTime = 25200000L;
        Long endTime = 43200000L;
        Shift shift1 = Shift.builder()
                .id(1)
                .name("Morning")
                .startTime(startTime)
                .endTime(endTime)
                .build();
        List<Shift> shiftList = new ArrayList<>();
        shiftList.add(shift1);

        Station station = Station.builder()
                .id(1)
                .name("station")
                .address("address")
                .shiftList(shiftList)
                .owner(User.builder()
                        .id(1)
                        .name("owner")
                        .build()).build();
        Shift shift = Shift.builder()
                .id(1)
                .name("Morning")
                .startTime(startTime)
                .endTime(endTime)
                .station(station)
                .build();

        ShiftDTO mockResult = ShiftDTO.builder()
                .id(1)
                .name("Morning")
                .startTime("07:00")
                .endTime("12:00")
                .station(StationDTO.builder()
                        .id(1)
                        .name("station")
                        .address("address")
                        .owner(UserDTO.builder()
                                .id(1)
                                .name("owner")
                                .build())
                        .build())
                .build();
        ShiftDTOCreate shiftDTOCreate = ShiftDTOCreate.builder()
                .name("Morning")
                .stationId(1)
                .startTime("07:00")
                .endTime("12:00")
                .stationId(1)
                .build();
        Mockito.when(optionalValidate.getStationById(1)).thenReturn(station);
        Mockito.when(shiftRepository.save(Mockito.any(Shift.class))).thenReturn(shift);
        assertEquals(mockResult, shiftService.create(shiftDTOCreate));
    }

    /**
     * param check duplicate
     */
    @Test
    void create_UTCID02() throws CustomNotFoundException, CustomDuplicateFieldException {
        Long startTime = 25200000L;
        Long endTime = 43200000L;
        Shift shift1 = Shift.builder()
                .id(1)
                .name("Morning")
                .startTime(startTime)
                .endTime(endTime)
                .build();
        List<Shift> shiftList = new ArrayList<>();
        shiftList.add(shift1);

        Station station = Station.builder()
                .id(1)
                .name("station")
                .address("address")
                .shiftList(shiftList)
                .owner(User.builder()
                        .id(1)
                        .name("owner")
                        .build()).build();
        Shift shift = Shift.builder()
                .id(1)
                .name("Morning")
                .startTime(startTime)
                .endTime(endTime)
                .station(station)
                .build();

        ShiftDTO mockResult = ShiftDTO.builder()
                .id(1)
                .name("Morning")
                .startTime("07:00")
                .endTime("12:00")
                .station(StationDTO.builder()
                        .id(1)
                        .name("station")
                        .address("address")
                        .owner(UserDTO.builder()
                                .id(1)
                                .name("owner")
                                .build())
                        .build())
                .build();
        ShiftDTOCreate shiftDTOCreate = ShiftDTOCreate.builder()
                .name("Morning")
                .stationId(1)
                .startTime("07:00")
                .endTime("12:00")
                .stationId(1)
                .build();
        Mockito.when(optionalValidate.getStationById(1)).thenReturn(station);
        Mockito.when(shiftRepository.save(Mockito.any(Shift.class))).thenReturn(shift);
        Optional<Shift> shiftOptional = Optional.of(shift);
        Mockito.when(shiftRepository.findByNameAndStationId("Morning", 1)).thenReturn(shiftOptional);
        assertThrows(CustomDuplicateFieldException.class, () -> {
            assertEquals(mockResult, shiftService.create(shiftDTOCreate));
        });
    }

    /**
     * param Shift of station is existed
     */
    @Test
    void update_UTCID01() throws CustomNotFoundException, CustomBadRequestException, CustomDuplicateFieldException {
        Long startTime1 = 25200000L;
        Long endTime1 = 43200000L;
        Shift shift1 = Shift.builder()
                .id(2)
                .name("Morning")
                .startTime(startTime1)
                .endTime(endTime1)
                .build();
        List<Shift> shiftList = new ArrayList<>();
        shiftList.add(shift1);

        Station station = Station.builder()
                .id(1)
                .name("station")
                .address("address")
                .shiftList(shiftList)
                .owner(User.builder()
                        .id(1)
                        .name("owner")
                        .build()).build();

        Long startTime = 10800000L;
        Long endTime = 14400000L;
        Shift shift = Shift.builder()
                .id(1)
                .name("Morning")
                .startTime(startTime)
                .endTime(endTime)
                .station(station)
                .build();

        ShiftDTO mockResult = ShiftDTO.builder()
                .id(1)
                .name("Morning")
                .startTime("11:00")
                .endTime("12:00")
                .station(StationDTO.builder()
                        .id(1)
                        .name("station")
                        .address("address")
                        .owner(UserDTO.builder()
                                .id(1)
                                .name("owner")
                                .build())
                        .build())
                .build();

        ShiftDTOUpdate shiftDTOUpdate = ShiftDTOUpdate.builder()
                .name("Morning")
                .startTime("11:00")
                .endTime("12:00")
                .build();

        Mockito.when(optionalValidate.getShiftById(1)).thenReturn(shift);
        Mockito.when(shiftRepository.save(Mockito.any(Shift.class))).thenReturn(shift);
        assertThrows(CustomDuplicateFieldException.class, () -> {
            assertEquals(mockResult, shiftService.update(1, shiftDTOUpdate));
        });
    }

    /**
     * param Shift of station is not existed
     */
    @Test
    void update_UTCID02() throws CustomNotFoundException, CustomBadRequestException, CustomDuplicateFieldException {
        Long startTime1 = 1609448400000L;
        Long endTime1 = 1609455600000L;
        Shift shift1 = Shift.builder()
                .id(2)
                .name("Morning")
                .startTime(startTime1)
                .endTime(endTime1)
                .build();
        List<Shift> shiftList = new ArrayList<>();
        shiftList.add(shift1);

        Station station = Station.builder()
                .id(1)
                .name("station")
                .address("address")
                .shiftList(shiftList)
                .owner(User.builder()
                        .id(1)
                        .name("owner")
                        .build()).build();
        Long startTime = 1609437600000L;
        Long endTime = 1609444800000L;
        Shift shift = Shift.builder()
                .id(1)
                .name("Molning")
                .startTime(startTime)
                .endTime(endTime)
                .station(station)
                .build();

        ShiftDTO mockResult = ShiftDTO.builder()
                .id(1)
                .name("Morning")
                .startTime("07:30")
                .endTime("12:30")
                .station(StationDTO.builder()
                        .id(1)
                        .name("station")
                        .address("address")
                        .owner(UserDTO.builder()
                                .id(1)
                                .name("owner")
                                .build())
                        .build())
                .build();

        ShiftDTOUpdate shiftDTOUpdate = ShiftDTOUpdate.builder()
                .name("Morning")
                .startTime("07:30")
                .endTime("12:30")
                .build();

        Mockito.when(optionalValidate.getShiftById(1)).thenReturn(shift);
        Mockito.when(shiftRepository.save(Mockito.any(Shift.class))).thenReturn(shift);
        assertEquals(mockResult, shiftService.update(1, shiftDTOUpdate));
    }

    @Test
    void delete_UTCID01() throws CustomNotFoundException, CustomBadRequestException {
        Station station = Station.builder()
                .id(1)
                .name("station")
                .address("address")
                .owner(User.builder()
                        .id(1)
                        .name("owner")
                        .build()).build();
        Long startTime = 25200000L;
        Long endTime = 43200000L;
        Shift shift = Shift.builder()
                .id(1)
                .name("Morning")
                .startTime(startTime)
                .endTime(endTime)
                .station(station)
                .build();

        ShiftDTO mockResult = ShiftDTO.builder()
                .id(1)
                .name("Morning")
                .startTime("07:00")
                .endTime("12:00")
                .station(StationDTO.builder()
                        .id(1)
                        .name("station")
                        .address("address")
                        .owner(UserDTO.builder()
                                .id(1)
                                .name("owner")
                                .build())
                        .build())
                .build();
        Mockito.when(optionalValidate.getShiftById(1)).thenReturn(shift);
        assertEquals(mockResult, shiftService.delete(1));
    }

    @Test
    void delete_UTCID02() throws CustomNotFoundException, CustomBadRequestException {
        Station station = Station.builder()
                .id(1)
                .name("station")
                .address("address")
                .owner(User.builder()
                        .id(1)
                        .name("owner")
                        .build()).build();
        Long startTime = 25200000L;
        Long endTime = 43200000L;
        Shift shift = Shift.builder()
                .id(1)
                .name("Morning")
                .startTime(startTime)
                .endTime(endTime)
                .station(station)
                .build();

        ShiftDTO mockResult = ShiftDTO.builder()
                .id(1)
                .name("Morning")
                .startTime("07:00")
                .endTime("12:00")
                .station(StationDTO.builder()
                        .id(1)
                        .name("station")
                        .address("address")
                        .owner(UserDTO.builder()
                                .id(1)
                                .name("owner")
                                .build())
                        .build())
                .build();
        Mockito.when(optionalValidate.getShiftById(1)).thenReturn(shift);
        Mockito.doThrow(DataIntegrityViolationException.class).when(shiftRepository).delete(shift);

        assertThrows(CustomBadRequestException.class, () -> {
            assertEquals(mockResult, shiftService.delete(1));
        });
    }
}