package com.funding.backend.domain.donationReward.repository;

import com.funding.backend.domain.donation.entity.Donation;
import com.funding.backend.domain.donationReward.entity.DonationReward;
import com.funding.backend.domain.project.entity.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRewardRepository extends JpaRepository<DonationReward, Long> {

  List<DonationReward> findAllByDonation(Donation donation);
}
