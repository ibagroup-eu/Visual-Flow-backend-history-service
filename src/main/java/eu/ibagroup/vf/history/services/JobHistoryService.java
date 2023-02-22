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

import eu.ibagroup.vf.history.repositories.JobHistoryRepository;
import eu.ibagroup.vf.history.dto.JobHistoryDto;
import eu.ibagroup.vf.history.model.JobHistory;
import eu.ibagroup.vf.history.model.Log;
import eu.ibagroup.vf.history.common.HistoryFields;
import io.fabric8.kubernetes.api.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * JobHistoryService class.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobHistoryService {
    private final KubernetesService kubernetesService;
    private final JobHistoryRepository jobHistoryRepository;

    /**
     * Save job history.
     *
     * @param projectId job history
     * @param jobId job history
     */
    @Transactional
    public String saveJobHistory(String projectId, String jobId) {
        JobHistory jobHistory = jobHistoryRepository.save(jobHistory(projectId, jobId));
        return jobHistory.getJobId();
    }

    /**
     * Return job history by id.
     *
     * @param jobId job id
     * @return list of job history
     */
    @Transactional
    public List<JobHistoryDto> getJobHistoryById(String jobId) {
        return toJobHistoryDto(jobHistoryRepository.findAllByJobId(jobId));
    }

    /**
     * Cast JobHistory to JobHistoryDto.
     *
     * @param jobHistories list of job histories
     * @return list of job history dto
     */
    public List<JobHistoryDto> toJobHistoryDto(List<JobHistory> jobHistories) {
        List<JobHistoryDto> jobHistoryDtos = new ArrayList<>();
        for (JobHistory jobHistory : jobHistories) {
            JobHistoryDto jobHistoryDto = JobHistoryDto.builder()
                    .jobId(jobHistory.getJobId())
                    .jobName(jobHistory.getJobName())
                    .type(jobHistory.getType())
                    .operation(jobHistory.getOperation())
                    .startedAt(jobHistory.getStartedAt())
                    .finishedAt(jobHistory.getFinishedAt())
                    .startedBy(jobHistory.getStartedBy())
                    .status(jobHistory.getStatus())
                    .logId(jobHistory.getLog().getId())
                    .build();
            jobHistoryDtos.add(jobHistoryDto);
        }
        return jobHistoryDtos;
    }

    /**
     * Generate job history.
     *
     * @param projectId project id
     * @param jobId     job id
     * @return job history
     */
    public JobHistory jobHistory(String projectId, String jobId) {
        Pod pod = kubernetesService.getPod(projectId, jobId);
        String log = kubernetesService.getPodLogs(projectId, jobId);
        ObjectMeta podMetadata = pod.getMetadata();
        PodStatus podStatus = pod.getStatus();
        return new JobHistory(
                podMetadata.getName(),
                podMetadata.getLabels().get(HistoryFields.NAME.toString()),
                podMetadata.getLabels().get(HistoryFields.TYPE.toString()),
                podMetadata.getLabels().get(HistoryFields.OPERATION.toString()),
                podStatus.getStartTime(),
                extractTerminatedStateField(podStatus, ContainerStateTerminated::getFinishedAt),
                podMetadata.getLabels().get(HistoryFields.STARTED_BY.toString()),
                podStatus.getPhase(),
                new Log(log));
    }

    /**
     * Extract terminated state field
     *
     * @param podStatus pod status
     * @param method    function
     * @return terminated property
     */
    public static String extractTerminatedStateField(
            PodStatus podStatus,
            Function<ContainerStateTerminated, String> method
    ) {
        return podStatus
                .getContainerStatuses()
                .stream()
                .map((ContainerStatus cs) -> cs.getState().getTerminated())
                .filter(Objects::nonNull)
                .map(method)
                .max(Comparator.comparing(ZonedDateTime::parse, ZonedDateTime::compareTo))
                .orElse(null);
    }
}
