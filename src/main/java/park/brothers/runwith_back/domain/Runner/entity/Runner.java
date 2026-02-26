package park.brothers.runwith_back.domain.Runner.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "runners")
public class Runner {

    @Id
    @Column(updatable = false, nullable = false)
    private String id;

    @Column(nullable = false)
    private String name;

    @CreationTimestamp //data insert 시 자동 현재 시간 대입
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
