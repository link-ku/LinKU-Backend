package com.linku.backend.domain.template.repository;

import com.linku.backend.domain.template.TemplateItem;
import com.linku.backend.domain.common.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateItemRepository extends JpaRepository<TemplateItem, Long> {

    List<TemplateItem> findAllByTemplate_TemplateIdAndStatus(Long templateId, Status status);

    boolean existsByIcon_IconIdAndStatus(Long iconId, Status status);
}
