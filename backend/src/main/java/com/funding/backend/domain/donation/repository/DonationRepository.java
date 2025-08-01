package com.funding.backend.domain.donation.repository;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.donation.entity.Donation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    Optional<Donation> findByProject(Project project);


    @Query("SELECT d FROM Donation d JOIN FETCH d.project pr JOIN FETCH pr.user u WHERE d.id = :donationId")
    Optional<Donation> findByIdWithProjectAndUser(@Param("donationId") Long donationId);
}

