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

import eu.ibagroup.vf.history.model.argo.WorkflowList;
import eu.ibagroup.vf.history.model.argo.Workflow;
import io.fabric8.kubernetes.client.NamespacedKubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArgoKubernetesService {
    private final NamespacedKubernetesClient client;

    private MixedOperation<Workflow, WorkflowList, Resource<Workflow>> getWorkflowCrdClient(
        NamespacedKubernetesClient k8sClient) {
        return k8sClient.customResources(Workflow.class, WorkflowList.class);
    }

    /**
     * Getting workflow by name.
     *
     * @param namespaceId namespace name
     * @param name        workflow name
     * @return Workflow
     */
    public Workflow getWorkflow(final String namespaceId, final String name) {
        return getWorkflowCrdClient(client)
            .inNamespace(namespaceId)
            .withName(name)
            .require();
    }
}


