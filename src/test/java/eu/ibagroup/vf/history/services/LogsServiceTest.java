package eu.ibagroup.vf.history.services;


import eu.ibagroup.vf.history.dto.LogDto;
import eu.ibagroup.vf.history.model.Log;
import eu.ibagroup.vf.history.repositories.LogsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LogsServiceTest {
    @Mock
    private LogsRepository logsRepository;
    private LogsService logsService;

    @BeforeEach
    void setUp() {
        logsService = new LogsService(logsRepository);
    }

    @Test
    void testFindLogById() {
        String logs = "Test logs";
        when(logsRepository.findById(anyString())).thenReturn(Optional.of(new Log(logs)));
        LogDto logDto = logsService.findLogById(anyString());
        assertEquals(logDto.getLog(), logs);
    }
}
