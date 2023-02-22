package eu.ibagroup.vf.history.controllers;

import eu.ibagroup.vf.history.dto.JobHistoryDto;
import eu.ibagroup.vf.history.dto.PipelineHistoryDto;
import eu.ibagroup.vf.history.services.PipelineHistoryService;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PipelineHistoryControllerTest {
    @Mock
    private PipelineHistoryService pipelineHistoryService;
    private PipelineHistoryController pipelineHistoryController;

    @BeforeEach
    void setUp() {
        pipelineHistoryController = new PipelineHistoryController(pipelineHistoryService);
    }

    @Test
    void testSavePipelineHistory() {
        String projectId = "test";
        String jobId = "test";
        when(pipelineHistoryService.savePipelineHistory(projectId, jobId)).thenReturn(projectId);
        ResponseEntity<String> response = pipelineHistoryController.savePipelineHistory(jobId, jobId);

        verify(pipelineHistoryService).savePipelineHistory(anyString(), anyString());
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status must be OK");
    }

    @Test
    void testGetPipelineHistoryById() {
        String id = "id";
        JobHistoryDto jobHistoryDto = JobHistoryDto.builder()
                .jobId("id")
                .jobName("name")
                .type("type")
                .operation("operation")
                .startedAt("startedAt")
                .finishedAt("finishedAt")
                .status("status")
                .logId("1")
                .build();
        when(pipelineHistoryService.getPipelineHistoryById(id)).thenReturn(List.of(
                PipelineHistoryDto
                        .builder()
                        .pipelineId(id)
                        .type("type")
                        .startedAt("startedAt")
                        .finishedAt("finishedAt")
                        .startedBy("startedBy")
                        .status("status")
                        .jobHistory(List.of(jobHistoryDto))
                        .build()
        ));
        List<PipelineHistoryDto> response = pipelineHistoryController.getPipelineHistoryById("id");

        assertEquals(1, response.size(), "Pipelines size must be 1");
        assertEquals(id, response.get(0).getPipelineId(), "JobHistory id's should be equals");
        assertEquals(jobHistoryDto, response.get(0).getJobHistory().get(0), "JobHistories should be equals");
        verify(pipelineHistoryService).getPipelineHistoryById(anyString());
    }
}
