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

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.NamespacedKubernetesClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KubernetesService {
    private final NamespacedKubernetesClient client;
    private final String JOB_CONTAINER = "main";

    /**
     * Getting pod status in namespace by name.
     *
     * @param namespaceId namespace id
     * @param name        pod name
     * @return pod
     */
    public Pod getPod(final String namespaceId, final String name) {
        return client
                .pods()
                .inNamespace(namespaceId)
                .withName(name)
                .require();
    }

    /**
     * Getting pod logs in namespace by name.
     *
     * @param namespaceId namespace id
     * @param name        pod name
     * @return string logs
     */
    public String getPodLogs(final String namespaceId, final String name) {
        return client
                .pods()
                .inNamespace(namespaceId)
                .withName(name)
                .inContainer(JOB_CONTAINER)
                .getLog();
    }
}
