package zerobase.finiance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zerobase.finiance.persist.entitiy.DividendEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity, Long> {
    List<DividendEntity> findAllByCompanyId(Long companyId);

    boolean existsByCompanyIdAndDate(Long CompanyId, LocalDateTime date); // where절 사용 하는 것 보다 빠르게 조회 가능

    @Transactional
    void deleteAllByCompanyId(Long companyId);
}
