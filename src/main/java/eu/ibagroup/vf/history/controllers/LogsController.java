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

import eu.ibagroup.vf.history.dto.LogDto;
import eu.ibagroup.vf.history.services.LogsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Logs", description = "Logs API")
@RequestMapping("api/project")
public class LogsController {
    private final LogsService logService;

    /**
     * Retrieving job log.
     *
     * @param id log id
     * @return job logs
     */
    @GetMapping("logs/job/{id}")
    public ResponseEntity<LogDto> getJobLogsById(@PathVariable String id) {
        LOGGER.info("Retrieving job logs by id: {}", id);
        LogDto logDto = logService.findLogById(id);
        if(logDto.getId() != null) {
            return ResponseEntity.ok(logDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
