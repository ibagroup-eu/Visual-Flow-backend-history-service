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

import eu.ibagroup.vf.history.repositories.LogsRepository;
import eu.ibagroup.vf.history.dto.LogDto;
import eu.ibagroup.vf.history.model.Log;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogsService {
    private final LogsRepository logRepository;

    /**
     * Find job logs by job id.
     *
     * @param logId log id
     * @return job logs
     */
    @Transactional
    public LogDto findLogById(String logId) {
        Optional<Log> log = logRepository.findById(logId);
        if(log.isPresent()) {
            Log result = log.get();
            return LogDto.builder().id(result.getId()).log(result.getLog()).build();
        } else {
            return LogDto.builder().build();
        }
    }
}
