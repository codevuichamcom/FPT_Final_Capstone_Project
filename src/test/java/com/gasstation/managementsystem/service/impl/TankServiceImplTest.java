package com.gasstation.managementsystem.service.impl;

import com.gasstation.managementsystem.entity.*;
import com.gasstation.managementsystem.exception.custom.CustomBadRequestException;
import com.gasstation.managementsystem.exception.custom.CustomDuplicateFieldException;
import com.gasstation.managementsystem.exception.custom.CustomNotFoundException;
import com.gasstation.managementsystem.model.dto.fuel.FuelDTO;
import com.gasstation.managementsystem.model.dto.station.StationDTO;
import com.gasstation.managementsystem.model.dto.tank.TankDTO;
import com.gasstation.managementsystem.model.dto.tank.TankDTOCreate;
import com.gasstation.managementsystem.model.dto.tank.TankDTOUpdate;
import com.gasstation.managementsystem.model.dto.user.UserDTO;
import com.gasstation.managementsystem.model.dto.userType.UserTypeDTO;
import com.gasstation.managementsystem.model.mapper.TankMapper;
import com.gasstation.managementsystem.repository.FuelRepository;
import com.gasstation.managementsystem.repository.PriceChangeHistoryRepository;
import com.gasstation.managementsystem.repository.StationRepository;
import com.gasstation.managementsystem.repository.TankRepository;
import com.gasstation.managementsystem.utils.DateTimeHelper;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TankServiceImplTest {
    @Mock
    private TankRepository tankRepository;
    @Mock
    private PriceChangeHistoryRepository priceChangeHistoryRepository;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private FuelRepository fuelRepository;
    @Mock
    private OptionalValidate optionalValidate;
    @Mock
    private UserHelper userHelper;
    @InjectMocks
    private TankServiceImpl tankService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    /**
     * param Login is ADMIN
     */
    @Test
    void findAll_UTCID01() {
        List<Tank> mockRepository = new ArrayList<>();
        List<TankDTO> mockResult = new ArrayList<>();
        mockData(mockRepository, mockResult);

        Mockito.when(tankRepository.findAll()).thenReturn(mockRepository);
        User user = User.builder().id(1).userType(UserType.builder().id(1).type("ADMIN").build()).build();
        Mockito.when(tankRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))).thenReturn(mockRepository);
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        List<TankDTO> listResultService = (List<TankDTO>) tankService.findAll().get("data");
        for (int i = 0; i < listResultService.size(); i++) {
            TankDTO o1 = mockResult.get(i);
            TankDTO o2 = listResultService.get(i);
            assertEquals(o1, o2);
        }
    }

    /**
     * param Login is OWNER
     */
    @Test
    void findAll_UTCID02() {
        List<Tank> mockRepository = new ArrayList<>();
        List<TankDTO> mockResult = new ArrayList<>();
        mockData(mockRepository, mockResult);

        Mockito.when(tankRepository.findAll()).thenReturn(mockRepository);
        User user = User.builder().id(1).userType(UserType.builder().id(2).type("OWNER").build()).build();
        List<Integer> stationIds = new ArrayList<>();
        stationIds.add(1);
        stationIds.add(2);
        List<Station> stationList = new ArrayList<>();
        stationList.add(Station.builder().id(1).build());
        stationList.add(Station.builder().id(2).build());
        user.setStationList(stationList);
        Mockito.when(userHelper.getListStationIdOfOwner(user)).thenReturn(stationIds);
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(tankRepository.findAllByOwnerId(user.getId(), Sort.by(Sort.Direction.DESC, "id"))).thenReturn(mockRepository);
        List<TankDTO> listResultService = (List<TankDTO>) tankService.findAll().get("data");
        for (int i = 0; i < listResultService.size(); i++) {
            TankDTO o1 = mockResult.get(i);
            TankDTO o2 = listResultService.get(i);
            assertEquals(o1, o2);
        }
    }

    private void mockData(List<Tank> mockRepository, List<TankDTO> mockResult) {
        for (int i = 1; i <= 10; i++) {
            Tank tank = Tank.builder().id(i).build();
            mockRepository.add(tank);
            mockResult.add(TankMapper.toTankDTO(tank));
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
        Tank mockRepository = Tank.builder().id(1).name("tank1")
                .station(Station.builder().owner(user1).build())
                .volume(333d)
                .currentPrice(444d)
                .remain(0d).build();
        TankDTO mockResult = TankDTO.builder().id(1).name("tank1")
                .station(StationDTO.builder().owner(userDTO).build())
                .volume(333d)
                .currentPrice(444d)
                .remain(0d).build();
        Mockito.when(optionalValidate.getTankById(1)).thenReturn(mockRepository);
        assertThrows(CustomNotFoundException.class, () -> {
            assertEquals(mockResult, tankService.findById(1));
        });
    }

    /**
     * param Login is # OWNER
     */
    @Test
    void findById_UTCID02() throws CustomNotFoundException {
        User user = User.builder().id(1).userType(UserType.builder().id(1).type("ADMIN").build()).build();
        User user1 = User.builder().id(2).userType(UserType.builder().id(1).type("ADMIN").build()).build();
        UserDTO userDTO = UserDTO.builder().id(2).userType(UserTypeDTO.builder().id(1).type("ADMIN").build()).build();

        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Tank mockRepository = Tank.builder().id(1).name("tank1")
                .station(Station.builder().id(1).owner(user1).build())
                .volume(333d)
                .currentPrice(444d)
                .remain(0d).build();
        TankDTO mockResult = TankDTO.builder().id(1).name("tank1")
                .station(StationDTO.builder().id(1).owner(userDTO).build())
                .volume(333d)
                .currentPrice(444d)
                .remain(0d).build();
        Mockito.when(optionalValidate.getTankById(1)).thenReturn(mockRepository);
        TankDTO act = tankService.findById(1);
        assertEquals(mockResult.getId(), act.getId());
        assertEquals(mockResult.getVolume(), act.getVolume());
        assertEquals(mockResult.getCurrentPrice(), act.getCurrentPrice());
        assertEquals(mockResult.getRemain(), act.getRemain());
    }

    @Test
    void create_UTCID01() throws CustomNotFoundException, CustomDuplicateFieldException {
        Fuel fuel = Fuel.builder()
                .id(1)
                .name("E92")
                .unit("litter")
                .price(32460d)
                .type("TYPE_E92").build();
        Station station = Station.builder()
                .id(1)
                .name("station")
                .address("address")
                .owner(User.builder()
                        .id(1)
                        .name("owner")
                        .build()).build();
        Tank mockRepository = Tank.builder()
                .id(1)
                .name("tank1")
                .volume(333d)
                .currentPrice(444d)
                .remain(0d)
                .build();
        TankDTO mockResult = TankDTO.builder()
                .id(1)
                .name("tank1")
                .volume(333d)
                .currentPrice(444d)
                .remain(0d)
                .build();
        TankDTOCreate tankDTOCreate = TankDTOCreate.builder()
                .name("tank1").stationId(2)
                .volume(333d)
                .currentPrice(444d)
                .fuelId(3).build();
        Mockito.when(optionalValidate.getStationById(1)).thenReturn(station);
        Mockito.when(optionalValidate.getFuelById(1)).thenReturn(fuel);
        Mockito.when(tankRepository.save(Mockito.any(Tank.class))).thenReturn(mockRepository);
        assertEquals(mockResult, tankService.create(tankDTOCreate));
    }

    /**
     * check duplicate
     */
    @Test
    void create_UTCID02() throws CustomNotFoundException {
        Station station = Station.builder().id(2).build();
        Tank tank = Tank.builder().name("tank1")
                .id(1)
                .station(station)
                .volume(333d)
                .currentPrice(444d)
                .remain(0d)
                .build();

        TankDTOCreate tankDTOCreate = TankDTOCreate.builder().name("tank1")
                .stationId(2)
                .volume(333d)
                .currentPrice(444d)
                .fuelId(3).build();

        Optional<Tank> tankOptional = Optional.of(tank);

        Mockito.when(tankRepository.save(Mockito.any(Tank.class))).thenReturn(tank);
        Mockito.when(tankRepository.findByNameAndStationId("tank1", station.getId())).thenReturn(tankOptional);
        assertThrows(CustomDuplicateFieldException.class, () -> {
            tankService.create(tankDTOCreate);
        });
    }


    @Test
    void update_UTCID01() throws CustomNotFoundException, CustomDuplicateFieldException {
        Station station = Station.builder().id(3).build();
        Station station2 = Station.builder().id(4).build();
        Fuel fuel = Fuel.builder().id(3).build();
        Tank mockRepository = Tank.builder().name("tank1")
                .id(1)
                .station(station)
                .fuel(fuel)
                .volume(333d)
                .currentPrice(444d)
                .remain(0d)
                .build();
        TankDTO mockResult = TankDTO.builder().name("tank1")
                .id(1)
                .station(StationDTO.builder().id(3).build())
                .fuel(FuelDTO.builder().id(3).build())
                .volume(333d)
                .currentPrice(555d)
                .remain(0d)
                .build();

        TankDTOUpdate tankDTOUpdate = TankDTOUpdate.builder().name("tank1")
                .currentPrice(555d)
                .build();
        User editor = User.builder().id(2).build();
        PriceChangeHistory priceChangeHistory = PriceChangeHistory.builder()
                .time(DateTimeHelper.getCurrentUnixTime())
                .oldPrice(444d)
                .newPrice(555d)
                .editor(editor)
                .station(station)
                .tank(mockRepository).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(editor);
        Mockito.when(priceChangeHistoryRepository.save(Mockito.any(PriceChangeHistory.class))).thenReturn(priceChangeHistory);
        Mockito.when(tankRepository.save(Mockito.any(Tank.class))).thenReturn(mockRepository);
        Mockito.when(stationRepository.save(Mockito.any(Station.class))).thenReturn(station);
        Mockito.when(stationRepository.save(Mockito.any(Station.class))).thenReturn(station2);
        Mockito.when(fuelRepository.save(Mockito.any(Fuel.class))).thenReturn(fuel);

        Mockito.when(optionalValidate.getStationById(3)).thenReturn(station);
        Mockito.when(optionalValidate.getStationById(4)).thenReturn(station);
        Mockito.when(optionalValidate.getFuelById(3)).thenReturn(fuel);
        Mockito.when(optionalValidate.getTankById(1)).thenReturn(mockRepository);
        assertEquals(mockResult, tankService.update(1, tankDTOUpdate));
    }

    /**
     * check duplicate name
     */
    @Test
    void update_UTCID02() throws CustomNotFoundException {
        Tank mockOldTank = Tank.builder()
                .id(1)
                .station(Station.builder()
                        .id(1).build())
                .name("tank").build();
        Mockito.when(optionalValidate.getTankById(Mockito.anyInt())).thenReturn(mockOldTank);
        TankDTOUpdate tankDTOUpdate = TankDTOUpdate.builder()
                .name("tank_update").build();
        Optional<Tank> tankOptional = Optional.of(Tank.builder()
                .id(2)
                .name("tank_update").build());
        Mockito.when(tankRepository.findByNameAndStationId(Mockito.anyString(), Mockito.anyInt())).thenReturn(tankOptional);

        assertThrows(CustomDuplicateFieldException.class, () -> tankService.update(1, tankDTOUpdate));
    }

    @Test
    void delete_UTCID01() throws CustomNotFoundException, CustomBadRequestException {
        Tank mockRepository = Tank.builder()
                .id(1)
                .name("tank1")
                .volume(333d)
                .currentPrice(444d)
                .remain(0d)
                .build();
        TankDTO mockResult = TankDTO.builder().name("tank1")
                .id(1)
                .volume(333d)
                .currentPrice(444d)
                .remain(0d)
                .build();

        Mockito.when(optionalValidate.getTankById(1)).thenReturn(mockRepository);
        assertEquals(mockResult, tankService.delete(1));
    }
    @Test
    void delete_UTCID02() throws CustomNotFoundException, CustomBadRequestException {
        Tank mockRepository = Tank.builder()
                .id(1)
                .name("tank1")
                .volume(333d)
                .currentPrice(444d)
                .remain(0d)
                .build();
        TankDTO mockResult = TankDTO.builder().name("tank1")
                .id(1)
                .volume(333d)
                .currentPrice(444d)
                .remain(0d)
                .build();

        Mockito.when(optionalValidate.getTankById(1)).thenReturn(mockRepository);
        Mockito.doThrow(DataIntegrityViolationException.class).when(tankRepository).delete(mockRepository);

        assertThrows(CustomBadRequestException.class, () -> {
            assertEquals(mockResult, tankService.delete(1));
        });
    }
}