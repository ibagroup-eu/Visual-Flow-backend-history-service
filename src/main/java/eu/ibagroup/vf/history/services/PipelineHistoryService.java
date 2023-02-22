/*
 * Copyright (c) 2021 IBA Group, a.s. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.ibagroup.vf.history.services;

import eu.ibagroup.vf.history.common.Statuses;
import eu.ibagroup.vf.history.repositories.PipelineHistoryRepository;
import eu.ibagroup.vf.history.common.HistoryFields;
import eu.ibagroup.vf.history.model.PipelineHistory;
import eu.ibagroup.vf.history.model.argo.Workflow;
import eu.ibagroup.vf.history.model.argo.WorkflowStatus;
import eu.ibagroup.vf.history.dto.PipelineHistoryDto;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineHistoryService {
    private final JobHistoryService jobHistoryService;
    private final ArgoKubernetesService argoKubernetesService;
    private final PipelineHistoryRepository pipelineHistoryRepository;

    /**
     * Save pipeline history.
     *
     * @param projectId  pipeline history
     * @param pipelineId  pipeline history
     */
    @Transactional
    public String savePipelineHistory(String projectId, String pipelineId) {
        PipelineHistory history = pipelineHistoryRepository.save(pipelineHistory(projectId, pipelineId));
        return history.getPipelineId();
    }

    /**
     * Retrieve pipeline history by id.
     *
     * @param jobId  job id
     * @return list of pipeline history dto
     */
    @Transactional
    public List<PipelineHistoryDto> getPipelineHistoryById(String jobId) {
        List<PipelineHistoryDto> pipelineHistoryDtos = new ArrayList<>();
        for (PipelineHistory pipelineHistory : pipelineHistoryRepository.findAllByPipelineId(jobId)) {
            PipelineHistoryDto pipelineHistoryDto = PipelineHistoryDto.builder()
                    .pipelineId(pipelineHistory.getPipelineId())
                    .type(pipelineHistory.getType())
                    .startedAt(pipelineHistory.getStartedAt())
                    .finishedAt(pipelineHistory.getFinishedAt())
                    .startedBy(pipelineHistory.getStartedBy())
                    .status(pipelineHistory.getStatus())
                    .jobHistory(jobHistoryService.toJobHistoryDto(pipelineHistory.getJobHistory()))
                    .build();

            pipelineHistoryDtos.add(pipelineHistoryDto);
        }
        return pipelineHistoryDtos;
    }

    /**
     * Generate pipeline history.
     *
     * @param projectId  project id
     * @param pipelineId pipeline id
     * @return pipeline history
     */
    public PipelineHistory pipelineHistory(String projectId, String pipelineId) {
        Workflow workflow = argoKubernetesService.getWorkflow(projectId, pipelineId);
        WorkflowStatus workflowStatus = workflow.getStatus();
        ObjectMeta workflowMetadata = workflow.getMetadata();

        PipelineHistory pipelineHistory = new PipelineHistory(
                workflowMetadata.getName(),
                workflowMetadata.getLabels().get(HistoryFields.TYPE.toString()),
                workflowStatus.getStartedAt().toString(),
                workflowStatus.getFinishedAt().toString(),
                workflowMetadata.getLabels().get(HistoryFields.STARTED_BY.toString()),
                workflowStatus.getPhase()
        );
        workflowJobIds(workflow).forEach(jobId ->
                pipelineHistory.addJobHistory(
                        jobHistoryService.jobHistory(workflowMetadata.getNamespace(), jobId)
                )
        );
        return pipelineHistory;
    }

    /**
     * Retrieving job ids from workflow.
     *
     * @param workflow workflow
     * @return list of workflow's jobs
     */
    private List<String> workflowJobIds(Workflow workflow) {
        WorkflowStatus workflowStatus = workflow.getStatus();
        return workflowStatus != null && List.of(
                Statuses.FAILED.toString(),
                Statuses.SUCCEEDED.toString()).contains(workflowStatus.getPhase()) ?
                workflowStatus
                        .getNodes()
                        .keySet()
                        .stream()
                        .filter(k -> !k.equals(workflow.getMetadata().getName()) &&
                                List.of(Statuses.FAILED.toString(),
                                        Statuses.SUCCEEDED.toString()).contains(
                                        workflowStatus.getNodes().get(k).getPhase()
                                )
                        ).collect(Collectors.toList()) :
                Collections.emptyList();
    }
}
