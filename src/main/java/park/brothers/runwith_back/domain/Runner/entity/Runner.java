package park.brothers.runwith_back.domain.Runner.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "runners")
public class Runner {

    @Id
    @Column(updatable = false, nullable = false)
    private String id;

    @ColumnDefault("'이름을 설정해주세요.'")
    private String name;

    @CreationTimestamp //data insert 시 자동 현재 시간 대입
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
