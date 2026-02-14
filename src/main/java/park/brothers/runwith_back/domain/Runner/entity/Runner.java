package park.brothers.runwith_back.domain.Runner.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "runners")
public class Runner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String imageLink;

    @CreationTimestamp //data insert 시 자동 현재 시간 대입
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
