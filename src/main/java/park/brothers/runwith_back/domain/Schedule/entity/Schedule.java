package park.brothers.runwith_back.domain.Schedule.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import park.brothers.runwith_back.domain.Belong.entity.Belong;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
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

    @Formula("(select count(*) from recognizes r where r.schedules = id)") //Schedule 테이블엔 없지만, schedule 테이블 조회시 서브 쿼리로 수행
    private int recognizeCount;
}
