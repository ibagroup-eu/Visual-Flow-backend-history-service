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

import eu.ibagroup.vf.history.model.argo.Workflow;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;

import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ArgoKubernetesServiceTest {
    private ArgoKubernetesService argoKubernetesService;
    private final KubernetesServer server = new KubernetesServer();

    @BeforeEach
    void setUp() {
        server.before();
        argoKubernetesService = new ArgoKubernetesService(server.getClient());
    }

    @Test
    void testGetWorkflow() {
        Workflow workflow = new Workflow();
        workflow.setMetadata(new ObjectMetaBuilder().withName("id").addToLabels("name", "vf").build());

        server
                .expect()
                .get()
                .withPath("/apis/argoproj.io/v1alpha1/namespaces/vf/workflows/id")
                .andReturn(HttpURLConnection.HTTP_OK, workflow)
                .once();

        Workflow actual = argoKubernetesService.getWorkflow("vf", "id");

        assertEquals("id", actual.getMetadata().getName(), "Name must be equals to expected");
        assertEquals("vf", actual.getMetadata().getLabels().get("name"), "Label must be equals to expected");
    }
}
