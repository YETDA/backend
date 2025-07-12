package com.funding.backend.domain.donation.repository;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.donation.entity.Donation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

  Optional<Donation> findByProject(Project project);
}
