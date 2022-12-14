package com.gasstation.managementsystem.service.impl;

import com.gasstation.managementsystem.entity.*;
import com.gasstation.managementsystem.exception.custom.CustomNotFoundException;
import com.gasstation.managementsystem.model.dto.expense.ExpenseDTO;
import com.gasstation.managementsystem.model.dto.expense.ExpenseDTOCreate;
import com.gasstation.managementsystem.model.dto.expense.ExpenseDTOFilter;
import com.gasstation.managementsystem.model.dto.expense.ExpenseDTOUpdate;
import com.gasstation.managementsystem.model.dto.fuelImport.FuelImportDTO;
import com.gasstation.managementsystem.model.dto.station.StationDTO;
import com.gasstation.managementsystem.model.dto.user.UserDTO;
import com.gasstation.managementsystem.model.dto.userType.UserTypeDTO;
import com.gasstation.managementsystem.model.mapper.ExpenseMapper;
import com.gasstation.managementsystem.repository.ExpenseRepository;
import com.gasstation.managementsystem.repository.criteria.ExpenseRepositoryCriteria;
import com.gasstation.managementsystem.utils.OptionalValidate;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpenseServiceImplTest {
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private OptionalValidate optionalValidate;
    @Mock
    private UserHelper userHelper;
    @Mock
    private ExpenseRepositoryCriteria expenseCriteria;
    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        List<Expense> mockRepository = new ArrayList<>();
        List<ExpenseDTO> mockResult = new ArrayList<>();
        mockData(mockRepository, mockResult);

        ExpenseDTOFilter filter = ExpenseDTOFilter
                .builder().amountFrom(100d).amountTo(200d).build();
        HashMap<String, Object> temp = new HashMap<>();
        temp.put("totalElement", 10);
        temp.put("data", mockRepository);
        temp.put("totalPage", 3);
        User user = User.builder().id(1).userType(UserType.builder().id(2).build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(expenseCriteria.findAll(Mockito.any(ExpenseDTOFilter.class))).thenReturn(temp);
        List<ExpenseDTO> listResultService = (List<ExpenseDTO>) expenseService.findAll(filter).get("data");
        for (int i = 0; i < listResultService.size(); i++) {
            ExpenseDTO o1 = mockResult.get(i);
            ExpenseDTO o2 = listResultService.get(i);
            assertEquals(o1, o2);
        }
    }

    private void mockData(List<Expense> mockRepository, List<ExpenseDTO> mockResult) {
        for (int i = 1; i <= 10; i++) {
            Expense expense = Expense.builder()
                    .id(1)
                    .reason("reason")
                    .amount(100d)

                    .station(Station.builder()
                            .id(1)
                            .name("station")
                            .build())
                    .fuelImport(FuelImport.builder()
                            .id(1)
                            .name("fuelImport")
                            .build())
                    .build();
            mockRepository.add(expense);
            mockResult.add(ExpenseMapper.toExpenseDTO(expense));
        }
    }

    /**
     * Expense not of the owner
     */
    @Test
    void findById_UTCID01() throws CustomNotFoundException {
        User owner = User.builder().id(2).userType(UserType.builder().id(2).build()).build();
        UserDTO ownerDTO = UserDTO.builder().id(2).userType(UserTypeDTO.builder().id(2).build()).build();
        Expense mockRepository = Expense.builder()
                .id(1)
                .reason("reason")
                .createdDate(0L)
                .amount(100d)
                .station(Station.builder()
                        .id(1)
                        .name("station")
                        .owner(owner)
                        .build())
                .fuelImport(FuelImport.builder()
                        .id(1)
                        .createdDate(0L)
                        .name("fuelImport")
                        .build())
                .build();
        ExpenseDTO mockResult = ExpenseDTO.builder()
                .id(1)
                .reason("reason")
                .createdDate(0L)
                .amount(100d)
                .station(StationDTO.builder()
                        .id(1)
                        .name("station")
                        .owner(ownerDTO)
                        .build())
                .fuelImport(FuelImportDTO.builder()
                        .id(1)
                        .name("fuelImport")
                        .createdDate(0L)
                        .build())
                .build();
        User user = User.builder().id(1).userType(UserType.builder().id(2).build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(optionalValidate.getExpenseById(1)).thenReturn(mockRepository);
        assertThrows(CustomNotFoundException.class, () -> {
            assertEquals(mockResult, expenseService.findById(1));
        });
    }

    @Test
    void findById_UTCID02() throws CustomNotFoundException {
        User owner = User.builder().id(1).userType(UserType.builder().id(2).build()).build();
        UserDTO ownerDTO = UserDTO.builder().id(1).userType(UserTypeDTO.builder().id(2).build()).build();
        Expense mockRepository = Expense.builder()
                .id(1)
                .reason("reason")
                .createdDate(0L)
                .amount(100d)
                .station(Station.builder()
                        .id(1)
                        .name("station")
                        .owner(owner)
                        .build())
                .fuelImport(FuelImport.builder()
                        .id(1)
                        .createdDate(0L)
                        .name("fuelImport")
                        .build())
                .build();
        ExpenseDTO mockResult = ExpenseDTO.builder()
                .id(1)
                .reason("reason")
                .createdDate(0L)
                .amount(100d)
                .station(StationDTO.builder()
                        .id(1)
                        .name("station")
                        .owner(ownerDTO)
                        .build())
                .fuelImport(FuelImportDTO.builder()
                        .id(1)
                        .name("fuelImport")
                        .createdDate(0L)
                        .build())
                .build();
        User user = User.builder().id(1).userType(UserType.builder().id(2).build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(optionalValidate.getExpenseById(1)).thenReturn(mockRepository);
        ExpenseDTO actual = expenseService.findById(1);
        assertEquals(mockResult.getId(), actual.getId());
        assertEquals(mockResult.getAmount(), actual.getAmount());
        assertEquals(mockResult.getReason(), actual.getReason());
        assertEquals(mockResult.getCreatedDate(), actual.getCreatedDate());
        assertEquals(mockResult.getStation().getId(), actual.getStation().getId());
        assertEquals(mockResult.getFuelImport().getId(), actual.getFuelImport().getId());
    }

    @Test
    void create() throws CustomNotFoundException {
        Station station = Station.builder()
                .id(1)
                .name("station")
                .build();
        FuelImport fuelImport = FuelImport.builder()
                .id(1)
                .createdDate(0L)
                .name("fuelImport")
                .build();
        Expense mockRepository = Expense.builder()
                .id(1)
                .reason("reason")
                .amount(100d)
                .station(station)
                .fuelImport(fuelImport)
                .build();
        ExpenseDTO mockResult = ExpenseDTO.builder()
                .id(1)
                .reason("reason")
                .amount(100d)
                .station(StationDTO.builder()
                        .id(1)
                        .build())
                .fuelImport(FuelImportDTO.builder()
                        .id(1)
                        .build())
                .build();
        ExpenseDTOCreate expenseDTOCreate = ExpenseDTOCreate.builder()
                .reason("reason")
                .amount(100d)
                .stationId(1)
                .fuelImportId(1)
                .build();
        User user = User.builder().id(2).name("OWNER").build();
        Mockito.when(optionalValidate.getStationById(1)).thenReturn(station);
        Mockito.when(optionalValidate.getFuelImportById(1)).thenReturn(fuelImport);
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(expenseRepository.save(Mockito.any(Expense.class))).thenReturn(mockRepository);
        ExpenseDTO actual = expenseService.create(expenseDTOCreate);
        assertEquals(mockResult.getId(), actual.getId());
        assertEquals(mockResult.getReason(), actual.getReason());
        assertEquals(mockResult.getAmount(), actual.getAmount());
        assertEquals(mockResult.getStation().getId(), actual.getStation().getId());
        assertEquals(mockResult.getFuelImport().getId(), actual.getFuelImport().getId());
    }

    @Test
    void update() throws CustomNotFoundException {
        Station station = Station.builder()
                .id(1)
                .name("station")
                .build();
        FuelImport fuelImport = FuelImport.builder()
                .id(1)
                .createdDate(0L)
                .name("fuelImport")
                .build();
        Expense mockRepository = Expense.builder()
                .id(1)
                .reason("reason")
                .amount(100d)
                .station(station)
                .fuelImport(fuelImport)
                .build();
        ExpenseDTO mockResult = ExpenseDTO.builder()
                .id(1)
                .reason("reason_update")
                .amount(200d)
                .station(StationDTO.builder()
                        .id(1)
                        .name("station")
                        .build())
                .fuelImport(FuelImportDTO.builder()
                        .id(1)
                        .name("fuelImport")
                        .createdDate(0L)
                        .build())
                .build();
        ExpenseDTOUpdate expenseDTOUpdate = ExpenseDTOUpdate.builder()
                .reason("reason_update")
                .amount(200d)
                .stationId(1)
                .fuelImportId(1)
                .build();
        User user = User.builder().id(2).name("OWNER").build();
        Mockito.when(optionalValidate.getStationById(1)).thenReturn(station);
        Mockito.when(optionalValidate.getExpenseById(1)).thenReturn(mockRepository);
        Mockito.when(optionalValidate.getFuelImportById(1)).thenReturn(fuelImport);
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(expenseRepository.save(Mockito.any(Expense.class))).thenReturn(mockRepository);
        ExpenseDTO actual = expenseService.update(1, expenseDTOUpdate);
        assertEquals(mockResult.getId(), actual.getId());
        assertEquals(mockResult.getReason(), actual.getReason());
        assertEquals(mockResult.getAmount(), actual.getAmount());
        assertEquals(mockResult.getStation().getId(), actual.getStation().getId());
        assertEquals(mockResult.getFuelImport().getId(), actual.getFuelImport().getId());
    }

    @Test
    void delete() throws CustomNotFoundException {
        Station station = Station.builder()
                .id(1)
                .name("station")
                .build();
        FuelImport fuelImport = FuelImport.builder()
                .id(1)
                .createdDate(0L)
                .name("fuelImport")
                .build();
        Expense mockRepository = Expense.builder()
                .id(1)
                .reason("reason")
                .amount(100d)
                .station(station)
                .fuelImport(fuelImport)
                .build();
        ExpenseDTO mockResult = ExpenseDTO.builder()
                .id(1)
                .reason("reason")
                .amount(100d)
                .station(StationDTO.builder()
                        .id(1)
                        .name("station")
                        .build())
                .fuelImport(FuelImportDTO.builder()
                        .id(1)
                        .name("fuelImport")
                        .createdDate(0L)
                        .build())
                .build();
        Mockito.when(optionalValidate.getExpenseById(1)).thenReturn(mockRepository);
        ExpenseDTO actual = expenseService.delete(1);
        assertEquals(mockResult.getId(), actual.getId());
    }
}