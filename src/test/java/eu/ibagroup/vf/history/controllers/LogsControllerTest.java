package eu.ibagroup.vf.history.controllers;

import eu.ibagroup.vf.history.dto.LogDto;
import eu.ibagroup.vf.history.services.LogsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LogsControllerTest {
    @Mock
    private LogsService logsService;
    private LogsController logsController;

    @BeforeEach
    void setUp() {
        logsController = new LogsController(logsService);
    }

    @Test
    void testGetJobLogsById() {
        String id = "id";
        String logs = "Test logs";
        when(logsService.findLogById(id)).thenReturn(LogDto.builder().id(id).log(logs).build());
        ResponseEntity<LogDto> response = logsController.getJobLogsById(id);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Statuses should be equals");
        verify(logsService).findLogById(anyString());
    }

    @Test
    void testGetJobLogsByIdNotFound() {
        String id = "id";
        when(logsService.findLogById(id)).thenReturn(LogDto.builder().build());
        ResponseEntity<LogDto> response_not_found = logsController.getJobLogsById(id);
        assertEquals(HttpStatus.NOT_FOUND, response_not_found.getStatusCode(), "Statuses should be equals");
        verify(logsService).findLogById(anyString());
    }
}
