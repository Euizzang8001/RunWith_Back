package park.brothers.runwith_back.domain.Runner.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import park.brothers.runwith_back.domain.Runner.entity.Runner;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Repository
public class MemoryRunnerRepository {

    private static Map<Long, Runner> store = new HashMap<>();
    private static long sequence = 0L;

    public void save(Runner runner){
        runner.setId(++sequence);
        runner.setCreatedAt(LocalDateTime.now());
        log.info("save: runner ={}", runner);
        store.put(runner.getId(), runner);
    }

    public Runner findById(Long id){
        return store.get(id);
    }

    public Optional<Runner> findByEmail(String email){
        return findAll().stream()
                .filter(r -> r.getEmail().equals(email))
                .findFirst();
    }

    public List<Runner> findAll(){
        return new ArrayList<>(store.values());
    }

    public void clearStore(){
        store.clear();
    }
}
