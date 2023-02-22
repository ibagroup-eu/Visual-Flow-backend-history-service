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

import eu.ibagroup.vf.history.dto.PipelineHistoryDto;
import eu.ibagroup.vf.history.services.PipelineHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/project")
@Tag(name = "Pipeline History", description = "Pipeline History API")
public class PipelineHistoryController {
    private final PipelineHistoryService pipelineHistoryService;

    /**
     * Saving pipeline history.
     *
     * @param projectId  project id
     * @param pipelineId pipeline id
     */
    @Operation(summary = "Save pipeline history", description = "Save pipeline history")
    @PostMapping("{projectId}/history/pipeline/{pipelineId}")
    public ResponseEntity<String> savePipelineHistory(@PathVariable String projectId, @PathVariable String pipelineId) {
        String id = pipelineHistoryService.savePipelineHistory(projectId, pipelineId);
        LOGGER.info("Pipeline history '{}' in project '{}' successfully saved", id, projectId);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    /**
     * Retrieving pipeline history.
     *
     * @param pipelineId  job id
     */
    @Operation(summary = "Retrieve pipeline history", description = "Retrieve pipeline history by id")
    @GetMapping("history/pipeline/{pipelineId}")
    public List<PipelineHistoryDto> getPipelineHistoryById(@PathVariable String pipelineId) {
        LOGGER.info("Retrieving job history from database source for {} job", pipelineId);
        return pipelineHistoryService.getPipelineHistoryById(pipelineId);
    }
}
