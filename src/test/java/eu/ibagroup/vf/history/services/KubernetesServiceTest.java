package eu.ibagroup.vf.history.services;

import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class KubernetesServiceTest {
    private KubernetesService kubernetesService;
    private final KubernetesServer server = new KubernetesServer();

    @BeforeEach
    void setUp() {
        server.before();
        kubernetesService = new KubernetesService(server.getClient());
    }

    @Test
    void testGetPodLogs() {

        String namespace = "namespace";
        String name = "pod1";

        server
                .expect()
                .get()
                .withPath("/api/v1/namespaces/namespace/pods/pod1/log?pretty=false&container=main")
                .andReturn(HttpURLConnection.HTTP_OK, "logs")
                .once();

        String result = kubernetesService.getPodLogs(namespace, name);

        assertEquals("logs", result, "Pod must be equals to expected");
    }

    @Test
    void testGetPod() {
        String name = "pod1";
        String namespace = "namespace";
        Pod pod = new PodBuilder()
                .withMetadata(new ObjectMetaBuilder()
                        .withName("pod1")
                        .build())
                .build();

        server
                .expect()
                .get()
                .withPath("/api/v1/namespaces/namespace/pods/pod1")
                .andReturn(HttpURLConnection.HTTP_OK, new PodBuilder(pod).build())
                .once();

        Pod result = kubernetesService.getPod(namespace, name);

        assertEquals(pod, result, "Pod must be equals to expected");
    }
}
