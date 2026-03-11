package park.brothers.runwith_back.domain.Recognize.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Schedule.entity.Schedule;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "recognizes")
public class Recognize {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "belongs")
    private Belong recognizingBelong; //인정을 해준 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedules")
    private Schedule recognizedSchedule; //인증을 받는 스케줄

    @Column
    private boolean recognizing; //인정하는지, 안하는지

    @CreationTimestamp
    @Column
    private LocalDateTime recognizedAt;

}
