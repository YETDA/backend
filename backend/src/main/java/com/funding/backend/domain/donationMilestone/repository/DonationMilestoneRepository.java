package com.funding.backend.domain.donationMilestone.repository;

import com.funding.backend.domain.donation.entity.Donation;
import com.funding.backend.domain.donationMilestone.entity.DonationMilestone;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationMilestoneRepository extends JpaRepository<DonationMilestone, Long> {

  List<DonationMilestone> findAllByDonation(Donation donation);
}
