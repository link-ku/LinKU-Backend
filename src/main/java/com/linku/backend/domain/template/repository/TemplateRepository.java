package com.linku.backend.domain.template.repository;

import com.linku.backend.domain.template.Template;
import com.linku.backend.domain.common.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    @Query("SELECT DISTINCT t FROM Template t LEFT JOIN FETCH t.items " +
            "WHERE t.templateId = :templateId " +
            "AND t.owner.userId = :userId " +
            "AND t.status = :status")
    Optional<Template> findTemplateWithItemsByIdAndOwnerIdAndStatus(
            @Param("templateId") Long templateId,
            @Param("userId") Long userId,
            @Param("status") Status status);

    @Query("SELECT t FROM Template t WHERE t.owner.userId = :userId AND t.cloned = false AND t.status = :status ORDER BY " +
            "CASE WHEN :sort = 'newest' THEN t.createdAt END DESC, " +
            "CASE WHEN :sort = 'oldest' THEN t.createdAt END ASC, " +
            "t.createdAt DESC")
    List<Template> findByOwner_UserIdAndClonedFalseAndStatusOrderBySort(
            @Param("userId") Long userId,
            @Param("status") Status status,
            @Param("sort") String sort);

    @Query("SELECT t FROM Template t WHERE t.owner.userId = :userId AND t.cloned = false AND t.status = :status AND t.name LIKE %:query% ORDER BY " +
            "CASE WHEN :sort = 'newest' THEN t.createdAt END DESC, " +
            "CASE WHEN :sort = 'oldest' THEN t.createdAt END ASC, " +
            "t.createdAt DESC")
    List<Template> findByOwner_UserIdAndClonedFalseAndStatusAndNameContainingOrderBySort(
            @Param("userId") Long userId,
            @Param("status") Status status,
            @Param("query") String query,
            @Param("sort") String sort);

    @Query("SELECT t FROM Template t WHERE t.owner.userId = :userId AND t.cloned = true AND t.status = :status ORDER BY " +
            "CASE WHEN :sort = 'newest' THEN t.createdAt END DESC, " +
            "CASE WHEN :sort = 'oldest' THEN t.createdAt END ASC, " +
            "t.createdAt DESC")
    List<Template> findByOwner_UserIdAndClonedTrueAndStatusOrderBySort(
            @Param("userId") Long userId,
            @Param("status") Status status,
            @Param("sort") String sort);

    @Query("SELECT t FROM Template t WHERE t.owner.userId = :userId AND t.cloned = true AND t.status = :status AND t.name LIKE %:query% ORDER BY " +
            "CASE WHEN :sort = 'newest' THEN t.createdAt END DESC, " +
            "CASE WHEN :sort = 'oldest' THEN t.createdAt END ASC, " +
            "t.createdAt DESC")
    List<Template> findByOwner_UserIdAndClonedTrueAndStatusAndNameContainingOrderBySort(
            @Param("userId") Long userId,
            @Param("status") Status status,
            @Param("query") String query,
            @Param("sort") String sort);
}
