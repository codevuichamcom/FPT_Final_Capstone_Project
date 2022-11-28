package com.gasstation.managementsystem.service.impl;

import com.gasstation.managementsystem.entity.*;
import com.gasstation.managementsystem.exception.custom.CustomBadRequestException;
import com.gasstation.managementsystem.exception.custom.CustomDuplicateFieldException;
import com.gasstation.managementsystem.exception.custom.CustomNotFoundException;
import com.gasstation.managementsystem.model.dto.employee.EmployeeDTO;
import com.gasstation.managementsystem.model.dto.shift.ShiftDTO;
import com.gasstation.managementsystem.model.dto.station.StationDTO;
import com.gasstation.managementsystem.model.dto.user.UserDTO;
import com.gasstation.managementsystem.model.dto.workSchedule.WorkScheduleDTO;
import com.gasstation.managementsystem.model.dto.workSchedule.WorkScheduleDTOCreate;
import com.gasstation.managementsystem.model.dto.workSchedule.WorkScheduleDTOUpdate;
import com.gasstation.managementsystem.model.mapper.WorkScheduleMapper;
import com.gasstation.managementsystem.repository.WorkScheduleRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkScheduleServiceImplTest {
    @Mock
    private WorkScheduleRepository workScheduleRepository;

    @Mock
    private OptionalValidate optionalValidate;

    @Mock
    private UserHelper userHelper;

    @InjectMocks
    private WorkScheduleServiceImpl workScheduleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    /**
     * param Login is ADMIN
     */
    @Test
    void findAll_UTCID01() {
        List<WorkSchedule> mockRepository = new ArrayList<>();
        List<WorkScheduleDTO> mockResult = new ArrayList<>();
        mockData(mockRepository, mockResult);

        //giả lập
        User user = User.builder().id(1).userType(UserType.builder().id(1).type("ADMIN").build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(workScheduleRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))).thenReturn(mockRepository);
        List<WorkScheduleDTO> listResultService = (List<WorkScheduleDTO>) workScheduleService.findAll().get("data");
        for (int i = 0; i < listResultService.size(); i++) {
            WorkScheduleDTO o1 = mockResult.get(i);
            WorkScheduleDTO o2 = listResultService.get(i);
            assertEquals(o1, o2);
        }
    }

    /**
     * param Login is OWNER
     */
    @Test
    void findAll_UTCID02() {
        List<WorkSchedule> mockRepository = new ArrayList<>();
        List<WorkScheduleDTO> mockResult = new ArrayList<>();
        mockData(mockRepository, mockResult);

        //giả lập
        User user = User.builder().id(1).userType(UserType.builder().id(2).type("OWNER").build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(workScheduleRepository.findAllByOwnerId(user.getId(), Sort.by(Sort.Direction.DESC, "id"))).thenReturn(mockRepository);
        List<WorkScheduleDTO> listResultService = (List<WorkScheduleDTO>) workScheduleService.findAll().get("data");
        for (int i = 0; i < listResultService.size(); i++) {
            WorkScheduleDTO o1 = mockResult.get(i);
            WorkScheduleDTO o2 = listResultService.get(i);
            assertEquals(o1, o2);
        }
    }

    private void mockData(List<WorkSchedule> mockRepository, List<WorkScheduleDTO> mockResult) {
        Long startDate = 16094340000000L;
        Long endDate = 16094440000000L;
        Long startTime = 25200000L;
        Long endTime = 43200000L;
        for (int i = 1; i <= 10; i++) {
            Employee employee = new Employee();
            employee.setId(i);
            employee.setStation(Station.builder().id(1).owner(User.builder().id(2).build()).build());
            Shift shift = new Shift();
            shift.setId(i);
            shift.setStartTime(startTime);
            shift.setEndTime(endTime);
            shift.setStation(Station.builder().id(1).owner(User.builder().id(2).build()).build());
            WorkSchedule workSchedule = WorkSchedule.builder().
                    employee(employee).shift(shift).
                    id(1).startDate(startDate).endDate(endDate).build();
            mockRepository.add(workSchedule);
            mockResult.add(WorkScheduleMapper.toWorkScheduleDTO(workSchedule));
        }
    }

    /**
     * param work schedule not of the owner
     */
    @Test
    void findById_UTCID01() throws CustomNotFoundException {
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
        Long startDate = 16094340000000L;
        Long endDate = 16094440000000L;
        Employee employee = Employee.builder().id(1)
                .station(Station.builder().id(1).owner(User.builder().id(2).build()).build())
                .build();
        EmployeeDTO employeeDTO = EmployeeDTO.builder().id(1)
                .station(StationDTO.builder().id(1).owner(UserDTO.builder().id(2).build()).build())
                .build();
        WorkSchedule mockRepository = WorkSchedule.builder().
                id(1).
                employee(employee).
                shift(shift).
                startDate(startDate).endDate(endDate).build();
        WorkScheduleDTO mockResult = WorkScheduleDTO.builder().
                id(1).
                employee(employeeDTO).
                startDate(startDate).endDate(endDate).build();
        User user = User.builder().id(2).userType(UserType.builder().id(2).type("OWNER").build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(optionalValidate.getWorkScheduleById(1)).thenReturn(mockRepository);
        assertThrows(CustomNotFoundException.class, () -> assertEquals(mockResult, workScheduleService.findById(1)));
    }

    /**
     * param work schedule not of the owner
     */
    @Test
    void findById_UTCID02() throws CustomNotFoundException {
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
        ShiftDTO shiftDTO = ShiftDTO.builder()
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
        Long startDate = 16094340000000L;
        Long endDate = 16094440000000L;
        Employee employee = Employee.builder().id(1)
                .station(Station.builder().id(1).owner(User.builder().id(2).build()).build())
                .build();
        EmployeeDTO employeeDTO = EmployeeDTO.builder().id(1)
                .station(StationDTO.builder().id(1).owner(UserDTO.builder().id(2).build()).build())
                .build();
        WorkSchedule mockRepository = WorkSchedule.builder().
                id(1).
                employee(employee).
                shift(shift).
                startDate(startDate).endDate(endDate).build();
        WorkScheduleDTO mockResult = WorkScheduleDTO.builder().
                id(1).
                shift(shiftDTO).
                employee(employeeDTO).
                startDate(startDate).endDate(endDate).build();
        User user = User.builder().id(1).userType(UserType.builder().id(2).type("OWNER").build()).build();
        Mockito.when(userHelper.getUserLogin()).thenReturn(user);
        Mockito.when(optionalValidate.getWorkScheduleById(1)).thenReturn(mockRepository);
        assertEquals(mockResult, workScheduleService.findById(1));
    }

    /**
     * Work schedule is existed
     */
    @Test
    void create_UCTID01() throws CustomNotFoundException {
        Long startDate = 1610247600000L;
        Long endDate = 1610265600000L;
        Long startDate1 = 1610258400000L;
        Long endDate1 = 1610276400000L;
        Long startTime = 25200000L;
        Long endTime = 43200000L;
        Shift shift = Shift.builder().id(1).startTime(startTime).endTime(endTime).build();
        ShiftDTO shiftDTO = ShiftDTO.builder().id(1).startTime("07:00").endTime("12:00").build();
        List<WorkSchedule> workScheduleList = new ArrayList<>();
        workScheduleList.add(WorkSchedule.builder().id(1)
                .startDate(startDate1).endDate(endDate1).shift(shift).build());
        workScheduleList.add(WorkSchedule.builder().id(2)
                .startDate(startDate1).endDate(endDate1).shift(shift).build());
        Employee employee = Employee.builder().id(1)
                .workScheduleList(workScheduleList)
                .station(Station.builder().id(1).owner(User.builder().id(2).build()).build())
                .build();
        EmployeeDTO employeeDTO = EmployeeDTO.builder().id(1)
                .station(StationDTO.builder().id(1).owner(UserDTO.builder().id(2).build()).build())
                .build();
        WorkSchedule mockRepository = WorkSchedule.builder().
                id(1).
                shift(shift).
                employee(employee).
                startDate(startDate).endDate(endDate).build();
        WorkScheduleDTO mockResult = WorkScheduleDTO.builder().
                id(1).
                employee(employeeDTO).
                shift(shiftDTO).
                startDate(startDate).endDate(endDate).build();
        WorkScheduleDTOCreate workScheduleDTOCreate = WorkScheduleDTOCreate.builder().
                employeeId(1).
                shiftId(1).
                startDate(startDate).endDate(endDate).build();
        Mockito.when(optionalValidate.getEmployeeById(1)).thenReturn(employee);
        Mockito.when(optionalValidate.getShiftById(1)).thenReturn(shift);
        Mockito.when(workScheduleRepository.save(Mockito.any(WorkSchedule.class))).thenReturn(mockRepository);
        assertThrows(CustomDuplicateFieldException.class, () -> assertEquals(mockResult, workScheduleService.create(workScheduleDTOCreate)));
    }

    /**
     * Work schedule is not existed
     */
    @Test
    void create_UCTID02() throws CustomNotFoundException, CustomDuplicateFieldException {
        Long startDate = 1609434000000L;
        Long endDate = 1609520400000L;
        Long startDate1 = 1609606800000L;
        Long endDate1 = 1610038800000L;
        Long startTime = 25200000L;
        Long endTime = 43200000L;
        Shift shift = Shift.builder().id(1).startTime(startTime).endTime(endTime).build();
        ShiftDTO shiftDTO = ShiftDTO.builder().id(1).startTime("07:00").endTime("12:00").build();
        List<WorkSchedule> workScheduleList = new ArrayList<>();
        workScheduleList.add(WorkSchedule.builder().id(1)
                .startDate(startDate).endDate(endDate).shift(shift).build());
        workScheduleList.add(WorkSchedule.builder().id(2)
                .startDate(startDate).endDate(endDate).shift(shift).build());
        Employee employee = Employee.builder().id(1)
                .workScheduleList(workScheduleList)
                .station(Station.builder().id(1).owner(User.builder().id(2).build()).build())
                .build();
        EmployeeDTO employeeDTO = EmployeeDTO.builder().id(1)
                .station(StationDTO.builder().id(1).owner(UserDTO.builder().id(2).build()).build())
                .build();
        WorkSchedule mockRepository = WorkSchedule.builder().
                id(1).
                shift(shift).
                employee(employee).
                startDate(startDate1).endDate(endDate1).build();
        WorkScheduleDTO mockResult = WorkScheduleDTO.builder().
                id(1).
                employee(employeeDTO).
                shift(shiftDTO).
                startDate(startDate1).endDate(endDate1).build();
        WorkScheduleDTOCreate workScheduleDTOCreate = WorkScheduleDTOCreate.builder().
                employeeId(1).
                shiftId(1).
                startDate(startDate1).endDate(endDate1).build();
        Mockito.when(optionalValidate.getEmployeeById(1)).thenReturn(employee);
        Mockito.when(optionalValidate.getShiftById(1)).thenReturn(shift);
        Mockito.when(workScheduleRepository.save(Mockito.any(WorkSchedule.class))).thenReturn(mockRepository);
        assertEquals(mockResult, workScheduleService.create(workScheduleDTOCreate));

    }

    @Test
    void update() throws CustomNotFoundException, CustomBadRequestException, CustomDuplicateFieldException {
        Long startDate = 1609434000000L;
        Long endDate = 1609520400000L;
        Long startDate1 = 1609606800000L;
        Long endDate1 = 1610038800000L;
        Long startTime = 25200000L;
        Long endTime = 43200000L;
        Shift shift = Shift.builder().id(1).startTime(startTime).endTime(endTime).build();
        ShiftDTO shiftDTO = ShiftDTO.builder().id(1).startTime("07:00").endTime("12:00").build();
        List<WorkSchedule> workScheduleList = new ArrayList<>();
        workScheduleList.add(WorkSchedule.builder().id(1)
                .startDate(startDate).endDate(endDate).shift(shift).build());
        workScheduleList.add(WorkSchedule.builder().id(2)
                .startDate(startDate).endDate(endDate).shift(shift).build());
        Employee employee = Employee.builder().id(1)
                .workScheduleList(workScheduleList)
                .station(Station.builder().id(1).owner(User.builder().id(2).build()).build())
                .build();
        EmployeeDTO employeeDTO = EmployeeDTO.builder().id(1)
                .station(StationDTO.builder().id(1).owner(UserDTO.builder().id(2).build()).build())
                .build();
        WorkSchedule mockRepository = WorkSchedule.builder().
                id(1).
                shift(shift).
                employee(employee).
                startDate(startDate1).endDate(endDate1).build();
        WorkScheduleDTO mockResult = WorkScheduleDTO.builder().
                id(1).
                employee(employeeDTO).
                shift(shiftDTO).
                startDate(startDate1).endDate(endDate1).build();
        WorkScheduleDTOUpdate workScheduleDTOUpdate = WorkScheduleDTOUpdate.builder().
                shiftId(1).
                startDate(startDate1).endDate(endDate1).build();

        Mockito.when(workScheduleRepository.save(Mockito.any(WorkSchedule.class))).thenReturn(mockRepository);
        Mockito.when(optionalValidate.getWorkScheduleById(1)).thenReturn(mockRepository);
        Mockito.when(optionalValidate.getShiftById(1)).thenReturn(shift);
        assertEquals(mockResult, workScheduleService.update(1, workScheduleDTOUpdate));
    }

    @Test
    void delete_UTCID01() throws CustomNotFoundException, CustomBadRequestException {
        Long startDate = 16094340000000L;
        Long endDate = 16094440000000L;
        Long startTime = 25200000L;
        Long endTime = 43200000L;
        Shift shift = Shift.builder().id(1).startTime(startTime).endTime(endTime).build();
        ShiftDTO shiftDTO = ShiftDTO.builder().id(1).startTime("07:00").endTime("12:00").build();
        Employee employee = Employee.builder().id(1)
                .station(Station.builder().id(1).owner(User.builder().id(2).build()).build())
                .build();
        EmployeeDTO employeeDTO = EmployeeDTO.builder().id(1)
                .station(StationDTO.builder().id(1).owner(UserDTO.builder().id(2).build()).build())
                .build();
        WorkSchedule mockRepository = WorkSchedule.builder().
                id(1).
                shift(shift).
                employee(employee).
                startDate(startDate).endDate(endDate).build();
        WorkScheduleDTO mockResult = WorkScheduleDTO.builder().
                id(1).
                employee(employeeDTO).
                shift(shiftDTO).
                startDate(startDate).endDate(endDate).build();
        Mockito.when(optionalValidate.getWorkScheduleById(1)).thenReturn(mockRepository);
        assertEquals(mockResult, workScheduleService.delete(1));
    }
    @Test
    void delete_UTCID02() throws CustomNotFoundException, CustomBadRequestException {
        Long startDate = 16094340000000L;
        Long endDate = 16094440000000L;
        Long startTime = 25200000L;
        Long endTime = 43200000L;
        Shift shift = Shift.builder().id(1).startTime(startTime).endTime(endTime).build();
        ShiftDTO shiftDTO = ShiftDTO.builder().id(1).startTime("07:00").endTime("12:00").build();
        Employee employee = Employee.builder().id(1)
                .station(Station.builder().id(1).owner(User.builder().id(2).build()).build())
                .build();
        EmployeeDTO employeeDTO = EmployeeDTO.builder().id(1)
                .station(StationDTO.builder().id(1).owner(UserDTO.builder().id(2).build()).build())
                .build();
        WorkSchedule mockRepository = WorkSchedule.builder().
                id(1).
                shift(shift).
                employee(employee).
                startDate(startDate).endDate(endDate).build();
        WorkScheduleDTO mockResult = WorkScheduleDTO.builder().
                id(1).
                employee(employeeDTO).
                shift(shiftDTO).
                startDate(startDate).endDate(endDate).build();
        Mockito.when(optionalValidate.getWorkScheduleById(1)).thenReturn(mockRepository);
        Mockito.doThrow(DataIntegrityViolationException.class).when(workScheduleRepository).delete(mockRepository);
        assertThrows(CustomBadRequestException.class, () -> {
            assertEquals(mockResult, workScheduleService.delete(1));
        });
    }
}