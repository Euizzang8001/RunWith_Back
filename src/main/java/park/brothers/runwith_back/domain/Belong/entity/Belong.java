package park.brothers.runwith_back.domain.Belong.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import park.brothers.runwith_back.domain.Group.entity.Group;
import park.brothers.runwith_back.domain.Runner.entity.Runner;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "belongs")
public class Belong {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //Belong을 조회할 때 Belog만 조회하고, Runner는 나중에 조회(지연 로딩)
    @JoinColumn(name = "runners")
    private Runner runner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groups")
    private Group group;

    @Column(nullable = false)
    private boolean isLeader;

    @Column(nullable = false)
    private String nickname;

    @CreationTimestamp
    @Column
    private LocalDateTime joinAt;
}
