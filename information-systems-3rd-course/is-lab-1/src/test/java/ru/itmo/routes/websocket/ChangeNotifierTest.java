package ru.itmo.routes.websocket;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import org.junit.jupiter.api.Test;
import ru.itmo.routes.service.RouteChanged;
import static org.assertj.core.api.Assertions.assertThat;

class ChangeNotifierTest {
    @Test
    void clientsAreNotifiedOnlyAfterSuccessfulCommit() throws Exception {
        Observes observes = (Observes) ChangeNotifier.class
                .getDeclaredMethod("onRouteChanged", RouteChanged.class)
                .getParameterAnnotations()[0][0];
        assertThat(observes.during()).isEqualTo(TransactionPhase.AFTER_SUCCESS);
    }
}
