package eu.ibagroup.vf.history.controllers;

import eu.ibagroup.vf.history.dto.JobHistoryDto;
import eu.ibagroup.vf.history.services.JobHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobHistoryControllerTest {
    @Mock
    private JobHistoryService jobHistoryService;
    private JobHistoryController jobHistoryController;

    @BeforeEach
    void setUp() {
        jobHistoryController = new JobHistoryController(jobHistoryService);
    }

    @Test
    void testSaveJobHistory() {
        when(jobHistoryService.saveJobHistory(anyString(), anyString())).thenReturn("id");
        ResponseEntity<String> response = jobHistoryController.saveJobHistory("projectId", "jobId");

        verify(jobHistoryService).saveJobHistory(anyString(), anyString());
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status must be OK");
    }

    @Test
    void testGetJobHistoryById() {
        String id = "id";
        when(jobHistoryService.getJobHistoryById(id)).thenReturn(List.of(
                JobHistoryDto.builder()
                        .jobId("id")
                        .jobName("name")
                        .type("type")
                        .operation("operation")
                        .startedAt("startedAt")
                        .finishedAt("finishedAt")
                        .status("status")
                        .logId("1")
                        .build()
        ));
        List<JobHistoryDto> response = jobHistoryController.getJobHistoryById("id");

        assertEquals(1, response.size(), "Jobs size must be 1");
        verify(jobHistoryService).getJobHistoryById(anyString());
    }
}
