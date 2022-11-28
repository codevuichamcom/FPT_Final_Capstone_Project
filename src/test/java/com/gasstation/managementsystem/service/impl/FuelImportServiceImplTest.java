package com.gasstation.managementsystem.service.impl;

import com.gasstation.managementsystem.entity.*;
import com.gasstation.managementsystem.exception.custom.CustomBadRequestException;
import com.gasstation.managementsystem.exception.custom.CustomNotFoundException;
import com.gasstation.managementsystem.model.dto.fuel.FuelDTO;
import com.gasstation.managementsystem.model.dto.fuelImport.FuelImportDTO;
import com.gasstation.managementsystem.model.dto.fuelImport.FuelImportDTOCreate;
import com.gasstation.managementsystem.model.dto.fuelImport.FuelImportDTOUpdate;
import com.gasstation.managementsystem.model.dto.station.StationDTO;
import com.gasstation.managementsystem.model.dto.supplier.SupplierDTO;
import com.gasstation.managementsystem.model.dto.tank.TankDTO;
import com.gasstation.managementsystem.model.dto.user.UserDTO;
import com.gasstation.managementsystem.model.dto.userType.UserTypeDTO;
import com.gasstation.managementsystem.model.mapper.FuelImportMapper;
import com.gasstation.managementsystem.repository.ExpenseRepository;
import com.gasstation.managementsystem.repository.FuelImportRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FuelImportServiceImplTest {
    @Mock
    private FuelImportRepository fuelImportRepository;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private TankRepository tankRepository;
    @Mock
    private OptionalValidate optionalValidate;
    @Mock
    private UserHelper userHelper;

    @InjectMocks
    private FuelImportServiceImpl fuelImportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    /**
     * param login is ADMIN
     */
    @Test
    void findAll_UTCID01() {
        List<FuelImport> mockRepository = new ArrayList<>();
        List<FuelImportDTO> mockResult = new ArrayList<>();
        mockData(mockRepository, mockResult);
        User user = User.builder().id(1).userType(UserType.builder().id(1).type("ADMIN").build()).build();
        Mockito.when(fuelImportRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))).thenReturn(mockRepository);
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        List<FuelImportDTO> listResultService = (List<FuelImportDTO>) fuelImportService.findAll().get("data");
        for (int i = 0; i < listResultService.size(); i++) {
            FuelImportDTO o1 = mockResult.get(i);
            FuelImportDTO o2 = listResultService.get(i);
            assertEquals(o1, o2);
        }
    }
    /**
     * param login is OWNER
     */
    @Test
    void findAll_UTCID02() {
        List<FuelImport> mockRepository = new ArrayList<>();
        List<FuelImportDTO> mockResult = new ArrayList<>();
        mockData(mockRepository, mockResult);
        List<Station> stationList = new ArrayList<>();
        stationList.add(Station.builder().id(1).build());
        stationList.add(Station.builder().id(2).build());
        User user = User.builder().id(1).userType(UserType.builder().id(2).type("OWNER").build()).build();
        user.setStationList(stationList);
        Mockito.when(fuelImportRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))).thenReturn(mockRepository);
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        List<FuelImportDTO> listResultService = (List<FuelImportDTO>) fuelImportService.findAll().get("data");
        for (int i = 0; i < listResultService.size(); i++) {
            FuelImportDTO o1 = mockResult.get(i);
            FuelImportDTO o2 = listResultService.get(i);
            assertEquals(o1, o2);
        }
    }

    /**
     * param pageable
     */
    @Test
    void findAll_UTCID03() {
        List<FuelImport> mockRepository = new ArrayList<>();
        List<FuelImportDTO> mockResult = new ArrayList<>();
        mockData(mockRepository, mockResult);

        final int PAGE_INDEX = 1;
        final int PAGE_SIZE = 3;
        Page<FuelImport> mockRepositoryPaged = new PageImpl<>(mockRepository.subList(PAGE_INDEX - 1, PAGE_SIZE));
        List<FuelImportDTO> mockResultPaged = mockResult.subList(PAGE_INDEX - 1, PAGE_SIZE);
        Mockito.when(fuelImportRepository.findAll(PageRequest.of(PAGE_INDEX - 1, PAGE_SIZE))).thenReturn(mockRepositoryPaged);
//        List<FuelImportDTO> listResultService = (List<FuelImportDTO>) fuelImportService.findAll(PageRequest.of(PAGE_INDEX - 1, PAGE_SIZE)).get("data");
//        for (int i = 0; i < listResultService.size(); i++) {
//            FuelImportDTO o1 = mockResultPaged.get(i);
//            FuelImportDTO o2 = listResultService.get(i);
//            assertEquals(o1, o2);
//        }
    }

    private void mockData(List<FuelImport> mockRepository, List<FuelImportDTO> mockResult) {
        for (int i = 1; i <= 10; i++) {
            Long importDate = 16094340000000L;
            FuelImport fuelImport = FuelImport.builder()
                    .id(1)
                    .name("name")
                    .createdDate(importDate)
                    .volume(10d)
                    .unitPrice(100d)
                    .amountPaid(50d)
                    .vatPercent(0)
                    .note("note")
                    .tank(Tank.builder().id(1).name("tank").build())
                    .supplier(Supplier.builder().id(1).name("supplier").build())
                    .build();
            mockRepository.add(fuelImport);
            mockResult.add(FuelImportMapper.toFuelImportDTO(fuelImport));
        }
    }

    @Test
    void findById_UTCID01() throws CustomNotFoundException {
        Long importDate = 16094340000000L;
        User user = User.builder().id(1).userType(UserType.builder().id(2).type("OWNER").build()).build();
        User user1 = User.builder().id(2).userType(UserType.builder().id(2).type("OWNER").build()).build();
        UserDTO userDTO = UserDTO.builder().id(2).userType(UserTypeDTO.builder().id(2).type("OWNER").build()).build();

        FuelImport mockRepository = FuelImport.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0)
                .note("note")
                .tank(Tank.builder().id(1).name("tank")
                        .station(Station.builder().owner(user1).build())
                        .build())
                .supplier(Supplier.builder().id(1).name("supplier").build())
                .build();
        FuelImportDTO mockResult = FuelImportDTO.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0d)
                .note("note")
                .tank(TankDTO.builder().id(1).name("tank")
                        .station(StationDTO.builder().owner(userDTO).build())
                        .build())
                .supplier(SupplierDTO.builder().id(1).name("supplier").build())
                .build();
        Mockito.when(optionalValidate.getFuelImportById(1)).thenReturn(mockRepository);
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        assertThrows(CustomNotFoundException.class, () -> {
            assertEquals(mockResult, fuelImportService.findById(1));
        });
    }

    @Test
    void findById_UTCID02() throws CustomNotFoundException {
        Long importDate = 16094340000000L;
        User user = User.builder().id(1).userType(UserType.builder().id(2).type("OWNER").build()).build();
        UserDTO userDTO = UserDTO.builder().id(1).userType(UserTypeDTO.builder().id(2).type("OWNER").build()).build();

        FuelImport mockRepository = FuelImport.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0)
                .note("note")
                .tank(Tank.builder().id(1).name("tank")
                        .station(Station.builder().id(1).owner(user).build())
                        .build())
                .supplier(Supplier.builder().id(1).name("supplier").build())
                .build();
        FuelImportDTO mockResult = FuelImportDTO.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0d)
                .note("note")
                .tank(TankDTO.builder().id(1).name("tank")
                        .station(StationDTO.builder().id(1).owner(userDTO).build())
                        .build())
                .supplier(SupplierDTO.builder().id(1).name("supplier").build())
                .build();
        Mockito.when(optionalValidate.getFuelImportById(1)).thenReturn(mockRepository);
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        FuelImportDTO act = fuelImportService.findById(1);
        assertEquals(mockResult.getId(), act.getId());
        assertEquals(mockResult.getName(), act.getName());
        assertEquals(mockResult.getCreatedDate(), act.getCreatedDate());
        assertEquals(mockResult.getVolume(), act.getVolume());
        assertEquals(mockResult.getUnitPrice(), act.getUnitPrice());
        assertEquals(mockResult.getAmountPaid(), act.getAmountPaid());
        assertEquals(mockResult.getVatPercent(), act.getVatPercent());
        assertEquals(mockResult.getNote(), act.getNote());
    }

    /**
     * Import date  greater than current date
     */
    @Test
    void create_UTCID01() throws CustomNotFoundException, CustomBadRequestException {
        Long importDate = 16094340000000L;
        User user = User.builder().id(1).build();
        Tank tank = Tank.builder().id(1).name("tank").build();
        TankDTO tankDTO = TankDTO.builder().id(1).name("tank").build();
        Supplier supplier = Supplier.builder().id(1).name("supplier").build();
        SupplierDTO supplierDTO = SupplierDTO.builder().id(1).name("supplier").build();
        Fuel fuel = Fuel.builder().id(1).build();
        FuelDTO fuelDTO = FuelDTO.builder().id(1).build();
        FuelImport mockRepository = FuelImport.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0)
                .note("note")
                .tank(tank)
                .supplier(supplier)
                .fuel(fuel)
                .build();
        FuelImportDTO mockResult = FuelImportDTO.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0d)
                .note("note")
                .tank(tankDTO)
                .fuel(fuelDTO)
                .supplier(supplierDTO)
                .build();
        FuelImportDTOCreate fuelImportDTOCreate = FuelImportDTOCreate.builder()
                .name("name")
                .importDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0d)
                .note("note")
                .tankId(1)
                .fuelId(1)
                .supplierId(1)
                .build();
        Mockito.when(optionalValidate.getTankById(1)).thenReturn(tank);
        Mockito.when(optionalValidate.getSupplierById(1)).thenReturn(supplier);
        Mockito.when(optionalValidate.getFuelById(1)).thenReturn(fuel);
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(fuelImportRepository.save(Mockito.any(FuelImport.class))).thenReturn(mockRepository);
        assertThrows(CustomBadRequestException.class, () -> {
            assertEquals(mockResult, fuelImportService.create(fuelImportDTOCreate));
        });
    }

    /**
     * Fuel type mismatch with tank
     */
    @Test
    void create_UTCID02() throws CustomNotFoundException, CustomBadRequestException {
        Long importDate = 1577811600000L;
        User user = User.builder().id(1).build();
        Tank tank = Tank.builder().id(1).name("tank").fuel(Fuel.builder().id(2).build()).build();
        TankDTO tankDTO = TankDTO.builder().id(1).name("tank").build();
        Supplier supplier = Supplier.builder().id(1).name("supplier").build();
        SupplierDTO supplierDTO = SupplierDTO.builder().id(1).name("supplier").build();
        Fuel fuel = Fuel.builder().id(1).build();
        FuelDTO fuelDTO = FuelDTO.builder().id(1).build();
        FuelImport mockRepository = FuelImport.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0)
                .note("note")
                .tank(tank)
                .supplier(supplier)
                .fuel(fuel)
                .build();
        FuelImportDTO mockResult = FuelImportDTO.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0d)
                .note("note")
                .tank(tankDTO)
                .fuel(fuelDTO)
                .supplier(supplierDTO)
                .build();
        FuelImportDTOCreate fuelImportDTOCreate = FuelImportDTOCreate.builder()
                .name("name")
                .importDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0d)
                .note("note")
                .tankId(1)
                .fuelId(1)
                .supplierId(1)
                .build();
        Mockito.when(optionalValidate.getTankById(1)).thenReturn(tank);
        Mockito.when(optionalValidate.getSupplierById(1)).thenReturn(supplier);
        Mockito.when(optionalValidate.getFuelById(1)).thenReturn(fuel);
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(fuelImportRepository.save(Mockito.any(FuelImport.class))).thenReturn(mockRepository);
        assertThrows(CustomBadRequestException.class, () -> {
            assertEquals(mockResult, fuelImportService.create(fuelImportDTOCreate));
        });
    }

    /**
     * Tank not enough to hold
     */
    @Test
    void create_UTCID03() throws CustomNotFoundException, CustomBadRequestException {
        Long importDate = 1577811600000L;
        User user = User.builder().id(1).build();
        Tank tank = Tank.builder().id(1).name("tank")
                .volume(100d).remain(10d)
                .fuel(Fuel.builder().id(1).build()).build();
        TankDTO tankDTO = TankDTO.builder().id(1).name("tank").build();
        Supplier supplier = Supplier.builder().id(1).name("supplier").build();
        SupplierDTO supplierDTO = SupplierDTO.builder().id(1).name("supplier").build();
        Fuel fuel = Fuel.builder().id(1).build();
        FuelDTO fuelDTO = FuelDTO.builder().id(1).build();
        FuelImport mockRepository = FuelImport.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0)
                .note("note")
                .tank(tank)
                .supplier(supplier)
                .fuel(fuel)
                .build();
        FuelImportDTO mockResult = FuelImportDTO.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0d)
                .note("note")
                .tank(tankDTO)
                .fuel(fuelDTO)
                .supplier(supplierDTO)
                .build();
        FuelImportDTOCreate fuelImportDTOCreate = FuelImportDTOCreate.builder()
                .name("name")
                .importDate(importDate)
                .volume(100d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0d)
                .note("note")
                .tankId(1)
                .fuelId(1)
                .supplierId(1)
                .build();
        Mockito.when(optionalValidate.getTankById(1)).thenReturn(tank);
        Mockito.when(optionalValidate.getSupplierById(1)).thenReturn(supplier);
        Mockito.when(optionalValidate.getFuelById(1)).thenReturn(fuel);
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(fuelImportRepository.save(Mockito.any(FuelImport.class))).thenReturn(mockRepository);
        assertThrows(CustomBadRequestException.class, () -> {
            assertEquals(mockResult, fuelImportService.create(fuelImportDTOCreate));
        });
    }

    @Test
    void create_UTCID04() throws CustomNotFoundException, CustomBadRequestException {
        Long importDate = 1577811600000L;
        User user = User.builder().id(1).build();
        Tank tank = Tank.builder().id(1).name("tank")
                .volume(100d).remain(10d)
                .fuel(Fuel.builder().id(1).build()).build();
        TankDTO tankDTO = TankDTO.builder().id(1).name("tank").build();
        Supplier supplier = Supplier.builder().id(1).name("supplier").build();
        SupplierDTO supplierDTO = SupplierDTO.builder().id(1).name("supplier").build();
        Fuel fuel = Fuel.builder().id(1).build();
        FuelDTO fuelDTO = FuelDTO.builder().id(1).build();
        FuelImport mockRepository = FuelImport.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0)
                .note("note")
                .tank(tank)
                .supplier(supplier)
                .fuel(fuel)
                .build();
        FuelImportDTO mockResult = FuelImportDTO.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0d)
                .note("note")
                .tank(tankDTO)
                .fuel(fuelDTO)
                .supplier(supplierDTO)
                .build();
        FuelImportDTOCreate fuelImportDTOCreate = FuelImportDTOCreate.builder()
                .name("name")
                .reason("test reason")
                .importDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0d)
                .note("note")
                .tankId(1)
                .fuelId(1)
                .supplierId(1)
                .build();
        Expense expense = Expense.builder()
                .amount(100d)
                .reason("test reason")
                .station(Station.builder().id(1).build())
                .fuelImport(mockRepository)
                .createdDate(DateTimeHelper.getCurrentDate())
                .creator(userHelper.getUserLogin()).build();
        Mockito.when(optionalValidate.getTankById(1)).thenReturn(tank);
        Mockito.when(optionalValidate.getSupplierById(1)).thenReturn(supplier);
        Mockito.when(optionalValidate.getFuelById(1)).thenReturn(fuel);
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(tankRepository.save(Mockito.any(Tank.class))).thenReturn(tank);
        Mockito.when(fuelImportRepository.save(Mockito.any(FuelImport.class))).thenReturn(mockRepository);
        Mockito.when(expenseRepository.save(Mockito.any(Expense.class))).thenReturn(expense);
        assertEquals(mockResult, fuelImportService.create(fuelImportDTOCreate));
    }

    /**
     * Import dat greater than current date
     */
    @Test
    void update_UTCID01() throws CustomNotFoundException, CustomBadRequestException {
        Long importDate = 16094340000000L;
        Tank tank = Tank.builder().id(1).name("tank").build();
        TankDTO tankDTO = TankDTO.builder().id(1).name("tank").build();
        Supplier supplier = Supplier.builder().id(1).name("supplier").build();
        SupplierDTO supplierDTO = SupplierDTO.builder().id(1).name("supplier").build();
        Fuel fuel = Fuel.builder().id(1).build();
        FuelDTO fuelDTO = FuelDTO.builder().id(1).build();
        FuelImport mockRepository = FuelImport.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0)
                .note("note")
                .tank(tank)
                .supplier(supplier)
                .fuel(fuel)
                .build();
        FuelImportDTO mockResult = FuelImportDTO.builder()
                .id(1)
                .name("name_update")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0d)
                .note("note")
                .tank(tankDTO)
                .fuel(fuelDTO)
                .supplier(supplierDTO)
                .build();
        FuelImportDTOUpdate fuelImportDTOUpdate = FuelImportDTOUpdate.builder()
                .importDate(importDate)
                .name("name_update")
                .note("note")
                .build();
        Mockito.when(optionalValidate.getFuelImportById(1)).thenReturn(mockRepository);
        Mockito.when(optionalValidate.getTankById(1)).thenReturn(tank);
        Mockito.when(optionalValidate.getSupplierById(1)).thenReturn(supplier);
        Mockito.when(optionalValidate.getFuelById(1)).thenReturn(fuel);
        Mockito.when(fuelImportRepository.save(Mockito.any(FuelImport.class))).thenReturn(mockRepository);
        assertThrows(CustomBadRequestException.class, () -> {
            assertEquals(mockResult, fuelImportService.update(1, fuelImportDTOUpdate));
        });
    }

    /**
     * reason is required when pay expenses
     */
    @Test
    void update_UTCID02() throws CustomNotFoundException, CustomBadRequestException {
        Long importDate = 1577811600000L;
        Tank tank = Tank.builder().id(1).name("tank").build();
        TankDTO tankDTO = TankDTO.builder().id(1).name("tank").build();
        Supplier supplier = Supplier.builder().id(1).name("supplier").build();
        SupplierDTO supplierDTO = SupplierDTO.builder().id(1).name("supplier").build();
        Fuel fuel = Fuel.builder().id(1).build();
        FuelDTO fuelDTO = FuelDTO.builder().id(1).build();
        FuelImport mockRepository = FuelImport.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0)
                .note("note")
                .tank(tank)
                .supplier(supplier)
                .fuel(fuel)
                .build();
        FuelImportDTO mockResult = FuelImportDTO.builder()
                .id(1)
                .name("name_update")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0d)
                .note("note")
                .tank(tankDTO)
                .fuel(fuelDTO)
                .supplier(supplierDTO)
                .build();
        FuelImportDTOUpdate fuelImportDTOUpdate = FuelImportDTOUpdate.builder()
                .name("name_update")
                .accountsPayable(100d)
                .importDate(importDate)
                .note("note")
                .build();
        Mockito.when(optionalValidate.getFuelImportById(1)).thenReturn(mockRepository);
        Mockito.when(optionalValidate.getTankById(1)).thenReturn(tank);
        Mockito.when(optionalValidate.getSupplierById(1)).thenReturn(supplier);
        Mockito.when(optionalValidate.getFuelById(1)).thenReturn(fuel);
        Mockito.when(fuelImportRepository.save(Mockito.any(FuelImport.class))).thenReturn(mockRepository);
        assertThrows(CustomBadRequestException.class, () -> {
            assertEquals(mockResult, fuelImportService.update(1, fuelImportDTOUpdate));
        });
    }

    @Test
    void update_UTCID03() throws CustomNotFoundException, CustomBadRequestException {
        Long importDate = 1577811600000L;
        Tank tank = Tank.builder().id(1).name("tank").build();
        TankDTO tankDTO = TankDTO.builder().id(1).name("tank").build();
        Supplier supplier = Supplier.builder().id(1).name("supplier").build();
        SupplierDTO supplierDTO = SupplierDTO.builder().id(1).name("supplier").build();
        Fuel fuel = Fuel.builder().id(1).build();
        FuelDTO fuelDTO = FuelDTO.builder().id(1).build();
        FuelImport mockRepository = FuelImport.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0)
                .note("note")
                .tank(tank)
                .supplier(supplier)
                .fuel(fuel)
                .build();
        FuelImportDTO mockResult = FuelImportDTO.builder()
                .id(1)
                .name("name_update")
                .createdDate(importDate)
                .importDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(150d)
                .vatPercent(0d)
                .note("note")
                .tank(tankDTO)
                .fuel(fuelDTO)
                .supplier(supplierDTO)
                .build();
        FuelImportDTOUpdate fuelImportDTOUpdate = FuelImportDTOUpdate.builder()
                .name("name_update")
                .reason("reason")
                .accountsPayable(100d)
                .importDate(importDate)
                .note("note")
                .build();
        Mockito.when(optionalValidate.getFuelImportById(1)).thenReturn(mockRepository);
        Mockito.when(optionalValidate.getTankById(1)).thenReturn(tank);
        Mockito.when(optionalValidate.getSupplierById(1)).thenReturn(supplier);
        Mockito.when(optionalValidate.getFuelById(1)).thenReturn(fuel);
        Mockito.when(fuelImportRepository.save(Mockito.any(FuelImport.class))).thenReturn(mockRepository);
        assertEquals(mockResult, fuelImportService.update(1, fuelImportDTOUpdate));
    }

    @Test
    void delete_UTCID01() throws CustomNotFoundException, CustomBadRequestException {
        Long importDate = 16094340000000L;
        Tank tank = Tank.builder().id(1).name("tank").build();
        TankDTO tankDTO = TankDTO.builder().id(1).name("tank").build();
        Supplier supplier = Supplier.builder().id(1).name("supplier").build();
        SupplierDTO supplierDTO = SupplierDTO.builder().id(1).name("supplier").build();
        Fuel fuel = Fuel.builder().id(1).build();
        FuelDTO fuelDTO = FuelDTO.builder().id(1).build();
        FuelImport mockRepository = FuelImport.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0)
                .note("note")
                .tank(tank)
                .supplier(supplier)
                .fuel(fuel)
                .build();
        FuelImportDTO mockResult = FuelImportDTO.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0d)
                .note("note")
                .tank(tankDTO)
                .fuel(fuelDTO)
                .supplier(supplierDTO)
                .build();
        Mockito.when(optionalValidate.getFuelImportById(1)).thenReturn(mockRepository);
        assertEquals(mockResult, fuelImportService.delete(1));
    }
    @Test
    void delete_UTCID02() throws CustomNotFoundException, CustomBadRequestException {
        Long importDate = 16094340000000L;
        Tank tank = Tank.builder().id(1).name("tank").build();
        TankDTO tankDTO = TankDTO.builder().id(1).name("tank").build();
        Supplier supplier = Supplier.builder().id(1).name("supplier").build();
        SupplierDTO supplierDTO = SupplierDTO.builder().id(1).name("supplier").build();
        Fuel fuel = Fuel.builder().id(1).build();
        FuelDTO fuelDTO = FuelDTO.builder().id(1).build();
        FuelImport mockRepository = FuelImport.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0)
                .note("note")
                .tank(tank)
                .supplier(supplier)
                .fuel(fuel)
                .build();
        FuelImportDTO mockResult = FuelImportDTO.builder()
                .id(1)
                .name("name")
                .createdDate(importDate)
                .volume(10d)
                .unitPrice(100d)
                .amountPaid(50d)
                .vatPercent(0d)
                .note("note")
                .tank(tankDTO)
                .fuel(fuelDTO)
                .supplier(supplierDTO)
                .build();
        Mockito.when(optionalValidate.getFuelImportById(1)).thenReturn(mockRepository);
        Mockito.doThrow(DataIntegrityViolationException.class).when(fuelImportRepository).delete(mockRepository);

        assertThrows(CustomBadRequestException.class, () -> {
            assertEquals(mockResult, fuelImportService.delete(1));
        });
    }
}