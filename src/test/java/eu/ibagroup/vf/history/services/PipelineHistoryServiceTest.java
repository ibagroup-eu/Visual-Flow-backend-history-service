package eu.ibagroup.vf.history.services;

import eu.ibagroup.vf.history.common.Statuses;
import eu.ibagroup.vf.history.dto.PipelineHistoryDto;
import eu.ibagroup.vf.history.common.HistoryFields;
import eu.ibagroup.vf.history.model.JobHistory;
import eu.ibagroup.vf.history.model.Log;
import eu.ibagroup.vf.history.model.PipelineHistory;
import eu.ibagroup.vf.history.model.argo.NodeStatus;
import eu.ibagroup.vf.history.model.argo.Workflow;
import eu.ibagroup.vf.history.model.argo.WorkflowStatus;
import eu.ibagroup.vf.history.repositories.JobHistoryRepository;
import eu.ibagroup.vf.history.repositories.PipelineHistoryRepository;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PipelineHistoryServiceTest {
    @Mock
    private PipelineHistoryRepository pipelineHistoryRepository;
    @Mock
    private JobHistoryRepository jobHistoryRepository;
    @Mock
    private KubernetesService kubernetesService;
    @Mock
    private ArgoKubernetesService argoKubernetesService;
    private PipelineHistoryService pipelineHistoryService;
    private PipelineHistory pipelineHistory;

    @BeforeEach
    void setUp() {
        pipelineHistoryService = new PipelineHistoryService(
                new JobHistoryService(
                        kubernetesService,
                        jobHistoryRepository
                ),
                argoKubernetesService,
                pipelineHistoryRepository);
        pipelineHistory = new PipelineHistory(
                "id",
                "type",
                "startedAt",
                "finishedAt",
                "startedBy",
                "status"
        );
        pipelineHistory.addJobHistory(new JobHistory(
                "id",
                "name",
                "type",
                "operation",
                "startedAt",
                "finishedAt",
                "startedBy",
                "status",
                new Log("logs")));
    }

    @Test
    void testSavePipelineHistory() {
        WorkflowStatus workflowStatus = new WorkflowStatus();
        workflowStatus.setPhase(Statuses.SUCCEEDED.toString());
        workflowStatus.setFinishedAt(DateTime.parse("2020-10-27T10:14:46Z"));
        workflowStatus.setStartedAt(DateTime.parse("2020-10-27T10:14:46Z"));

        NodeStatus nodeStatus1 = new NodeStatus();
        nodeStatus1.setDisplayName("pipeline");
        nodeStatus1.setPhase("Running");
        nodeStatus1.setFinishedAt(DateTime.parse("2021-10-28T07:37:46Z"));
        nodeStatus1.setTemplateName("sparkTemplate");
        nodeStatus1.setType("JOB");

        workflowStatus.setNodes(Stream.of(nodeStatus1)
                .collect(Collectors.toMap(NodeStatus::getDisplayName, ns -> ns)));
        Workflow workflow = new Workflow();
        workflow.setMetadata(new ObjectMetaBuilder()
                .withNamespace("vf")
                .withName("wf")
                .addToLabels(HistoryFields.TYPE.toString(), "job")
                .addToLabels(HistoryFields.STARTED_BY.toString(), "test_user")
                .build());
        workflow.setStatus(workflowStatus);

        when(pipelineHistoryRepository.save(any())).thenReturn(pipelineHistory);
        when(argoKubernetesService.getWorkflow(anyString(), anyString())).thenReturn(workflow);
        String response = pipelineHistoryService.savePipelineHistory(anyString(), anyString());

        assertEquals(pipelineHistory.getPipelineId(), response, "Pipeline ids must be equals");
    }

    @Test
    void testGetPipelineHistoryById() {
        when(pipelineHistoryRepository.findAllByPipelineId(anyString())).thenReturn(List.of(pipelineHistory));
        List<PipelineHistoryDto> history = pipelineHistoryService.getPipelineHistoryById(anyString());

        assertEquals(history.get(0).getPipelineId(), pipelineHistory.getPipelineId(),
                "Pipeline ids must be equals");
    }
}
