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

package eu.ibagroup.vf.history.controllers;

import eu.ibagroup.vf.history.dto.JobHistoryDto;
import eu.ibagroup.vf.history.services.JobHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/project")
@Tag(name = "Job History", description = "Job History API")
public class JobHistoryController {
    private final JobHistoryService jobHistoryService;

    /**
     * Saving job history.
     *
     * @param projectId project id
     * @param jobId     job id
     */
    @Operation(summary = "Save job history", description = "Save job history")
    @PostMapping("{projectId}/history/job/{jobId}")
    public ResponseEntity<String> saveJobHistory(@PathVariable String projectId, @PathVariable String jobId) {
        String id = jobHistoryService.saveJobHistory(projectId, jobId);
        LOGGER.info("Job history '{}' in project '{}' successfully saved", id, projectId);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    /**
     * Retrieving job history.
     *
     * @param jobId  job id
     */
    @Operation(summary = "Retrieve job history", description = "Retrieve job history by id")
    @GetMapping("history/job/{jobId}")
    public List<JobHistoryDto> getJobHistoryById(@PathVariable String jobId) {
        LOGGER.info("Retrieving job history from database source for {} job", jobId);
        return jobHistoryService.getJobHistoryById(jobId);
    }
}
