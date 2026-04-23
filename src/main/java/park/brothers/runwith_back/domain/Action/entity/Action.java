package park.brothers.runwith_back.domain.Action.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import park.brothers.runwith_back.domain.Schedule.entity.Schedule;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "actions")
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @JoinColumn(name = "schedules")
    @ManyToOne(fetch = FetchType.LAZY)
    private Schedule schedule;

    @Column
    private String description;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int startHour;

    @Column(nullable = false)
    private int startMinute;

    @Column(nullable = false)
    private int endHour;

    @Column(nullable = false)
    private int endMinute;

    @Column(nullable = false)
    private int maxImageSize;

}
