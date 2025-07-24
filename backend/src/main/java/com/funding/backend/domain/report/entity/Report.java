package com.funding.backend.domain.report.entity;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.enums.ReportCategory;
import com.funding.backend.enums.ReportStatus;
import com.funding.backend.global.auditable.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "reports", uniqueConstraints = @UniqueConstraint(columnNames = {"reporter_id", "project_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Report extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_category")
    private ReportCategory reportCategory;

    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_status", nullable = false)
    private ReportStatus reportStatus;

}
