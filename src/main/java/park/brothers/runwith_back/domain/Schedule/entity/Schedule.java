package park.brothers.runwith_back.domain.Schedule.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import park.brothers.runwith_back.domain.Belong.entity.Belong;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "belongs")
    private Belong belong;

    @Column(nullable = false)
    private int scheduleYear;

    @Column(nullable = false)
    private int scheduleMonth;

    @Column(nullable = false)
    private int scheduleDate;

    @Column(nullable = false)
    private String description;
}
