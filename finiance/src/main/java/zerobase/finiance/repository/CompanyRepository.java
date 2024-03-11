package zerobase.finiance.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.finiance.persist.entitiy.CompanyEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByTicker(String ticker);
    Optional<CompanyEntity> findByName(String name);
    //Optional -> nullpointexception 반환, 코드 적으로 깔끔

    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);

    Optional<CompanyEntity> findByTicker(String ticker);
}
