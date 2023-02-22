package eu.ibagroup.vf.history;

import eu.ibagroup.vf.history.controllers.JobHistoryController;
import eu.ibagroup.vf.history.controllers.LogsController;
import eu.ibagroup.vf.history.controllers.PipelineHistoryController;
import eu.ibagroup.vf.history.repositories.JobHistoryRepository;
import eu.ibagroup.vf.history.repositories.LogsRepository;
import eu.ibagroup.vf.history.repositories.PipelineHistoryRepository;
import eu.ibagroup.vf.history.services.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class VfApiHistoryApplicationTests {
	@Autowired
	private JobHistoryRepository jobHistoryRepository;
	@Autowired
	private LogsRepository logsRepository;
	@Autowired
	private PipelineHistoryRepository pipelineHistoryRepository;
	@Autowired
	private JobHistoryController jobHistoryController;
	@Autowired
	private LogsController logsController;
	@Autowired
	private PipelineHistoryController pipelineHistoryController;
	@Autowired
	private ArgoKubernetesService argoKubernetesService;
	@Autowired
	private JobHistoryService jobHistoryService;
	@Autowired
	private KubernetesService kubernetesService;
	@Autowired
	private LogsService logsService;
	@Autowired
	private PipelineHistoryService pipelineHistoryService;

	@Test
	void contextLoads() {
		assertNotNull(jobHistoryRepository, "JobHistoryRepository should not be null!");
		assertNotNull(logsRepository, "LogsRepository should not be null!");
		assertNotNull(pipelineHistoryRepository, "PipelineHistoryRepository should not be null!");
		assertNotNull(jobHistoryController, "JobHistoryController should not be null!");
		assertNotNull(logsController, "LogsController should not be null!");
		assertNotNull(pipelineHistoryController, "PipelineHistoryController should not be null!");
		assertNotNull(argoKubernetesService, "ArgoKubernetesService should not be null!");
		assertNotNull(jobHistoryService, "JobHistoryService should not be null!");
		assertNotNull(kubernetesService, "KubernetesService should not be null!");
		assertNotNull(logsService, "LogsService should not be null!");
		assertNotNull(pipelineHistoryService, "PipelineHistoryService should not be null!");
	}
}
