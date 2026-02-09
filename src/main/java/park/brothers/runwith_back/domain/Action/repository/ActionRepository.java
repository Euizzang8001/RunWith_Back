package park.brothers.runwith_back.domain.Action.repository;

import park.brothers.runwith_back.domain.Action.entity.Action;

import java.util.List;

public interface ActionRepository {
    void save(Action action);

    void delete(Action action);

    Action getById(Long id);

    List<Action> getOverlappedActions(Long scheduleId, int startHour, int startMinute, int endHour, int endMinute);

    void reviseAction(Long id, String name, String description, int startHour, int startMinute, int endHour, int endMinute);

    List<Action> getActionsByScheduleId(Long scheduleId);
}
