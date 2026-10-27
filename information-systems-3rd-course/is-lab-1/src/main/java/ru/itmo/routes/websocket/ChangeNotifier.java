package ru.itmo.routes.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import ru.itmo.routes.service.RouteChanged;

@ApplicationScoped
public class ChangeNotifier {
    void onRouteChanged(@Observes(during = TransactionPhase.AFTER_SUCCESS) RouteChanged event) {
        RouteEvents.broadcast(event.action(), event.id());
    }
}
