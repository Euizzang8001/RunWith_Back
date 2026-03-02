package park.brothers.runwith_back.domain.Action.repository;

import park.brothers.runwith_back.domain.Action.entity.Action;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActionRepository {
    Action save(Action action);

    void delete(Action action);

    Optional<Action> findById(UUID id);

    List<Action> findOverlappedActions(UUID scheduleId, int startHour, int startMinute, int endHour, int endMinute);

    Action reviseAction(Action action, String name, String description, int startHour, int startMinute, int endHour, int endMinute, int maxImageSize);

    List<Action> findActionsByScheduleId(UUID scheduleId);
}
