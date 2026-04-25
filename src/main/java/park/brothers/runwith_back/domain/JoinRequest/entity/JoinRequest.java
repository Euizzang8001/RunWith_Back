package park.brothers.runwith_back.domain.JoinRequest.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import park.brothers.runwith_back.domain.Group.entity.Group;
import park.brothers.runwith_back.domain.Runner.entity.Runner;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "join_request")
public class JoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "runners")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Runner runner;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groups")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Group group;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}