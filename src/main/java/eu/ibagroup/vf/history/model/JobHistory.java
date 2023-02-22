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

package eu.ibagroup.vf.history.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.CascadeType;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "job_history")
public class JobHistory implements Serializable {
    @Id
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "id")
    private String jobId;
    @Column(name = "job_name")
    private String jobName;
    @Column(name = "type")
    private String type;
    @Column(name = "operation")
    private String operation;
    @Column(name = "started_at")
    private String startedAt;
    @Column(name = "finished_at")
    private String finishedAt;
    @Column(name = "started_by")
    private String startedBy;
    @Column(name = "status")
    private String status;
    @ManyToOne
    @JoinColumn(name="pipeline_id", referencedColumnName = "id")
    private PipelineHistory pipelineHistory;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "log_id", referencedColumnName = "id")
    private Log log;
    public JobHistory(
            String jobId,
            String jobName,
            String type,
            String operation,
            String startedAt,
            String finishedAt,
            String startedBy,
            String status,
            Log log) {
        this.jobId = jobId;
        this.jobName = jobName;
        this.type = type;
        this.operation = operation;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.startedBy = startedBy;
        this.status = status;
        this.log = log;
    }
}
