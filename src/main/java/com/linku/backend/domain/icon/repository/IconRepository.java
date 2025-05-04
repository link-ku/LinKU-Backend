package com.linku.backend.domain.icon.repository;

import com.linku.backend.domain.icon.Icon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IconRepository extends JpaRepository<Icon, Long> {
}
