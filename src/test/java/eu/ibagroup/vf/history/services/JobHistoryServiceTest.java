package eu.ibagroup.vf.history.services;

import eu.ibagroup.vf.history.dto.JobHistoryDto;
import eu.ibagroup.vf.history.common.HistoryFields;
import eu.ibagroup.vf.history.model.JobHistory;
import eu.ibagroup.vf.history.model.Log;
import eu.ibagroup.vf.history.repositories.JobHistoryRepository;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodStatusBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class JobHistoryServiceTest {
    @Mock
    private JobHistoryRepository repository;
    @Mock
    private KubernetesService kubernetesService;
    private JobHistoryService jobHistoryService;
    private JobHistory jobHistory;

    @BeforeEach
    void setUp() {
        jobHistoryService = new JobHistoryService(kubernetesService, repository);
        jobHistory = new JobHistory(
                "id",
                "name",
                "type",
                "operation",
                "startedAt",
                "finishedAt",
                "startedBy",
                "status",
                new Log("logs"));
    }

    @Test
    void testGetJobHistoryById() {
        when(repository.findAllByJobId(anyString())).thenReturn(List.of(jobHistory));
        List<JobHistoryDto> history = jobHistoryService.getJobHistoryById("id");
        assertEquals(history.get(0).getJobName(), "name");
    }

    @Test
    void testToJobHistoryDto() {
        List<JobHistoryDto> history = jobHistoryService.toJobHistoryDto(List.of(jobHistory));
        assertEquals(history.get(0).getJobName(), jobHistory.getJobName());
    }

    @Test
    void testSaveJobHistory() {
        String logs = "Logs for 'pod1' pod";
        Pod pod = new PodBuilder()
                .withMetadata(new ObjectMetaBuilder()
                        .withName("pod1")
                        .addToLabels(HistoryFields.NAME.toString(), "name")
                        .addToLabels(HistoryFields.TYPE.toString(), "type")
                        .addToLabels(HistoryFields.OPERATION.toString(), "operation")
                        .addToLabels(HistoryFields.STARTED_BY.toString(), "test")
                        .build())
                .withStatus(new PodStatusBuilder()
                        .withPhase("Failed")
                        .withStartTime(
                                "2022-10-27T10:14:46Z")
                        .build())
                .build();
        when(repository.save(any())).thenReturn(jobHistory);
        when(kubernetesService.getPod(anyString(), anyString())).thenReturn(pod);
        when(kubernetesService.getPodLogs(anyString(), anyString())).thenReturn(logs);
        String response = jobHistoryService.saveJobHistory(anyString(), anyString());
        assertEquals(jobHistory.getJobId(), response, "Pipeline ids must be equals");
    }
}
