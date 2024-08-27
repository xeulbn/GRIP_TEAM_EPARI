package org.example.grip_demo.climbinggym.domain;

import org.example.grip_demo.condemo.CongestionDemoDto;

import java.util.List;
import java.util.Optional;

public interface ClimbingGymRepository{
    Optional<ClimbingGym> findById(Long id);
    List<ClimbingGym> findAll();
    ClimbingGym save(ClimbingGym climbingGym);
    void deleteById(Long id);

    Optional<CongestionDemoDto> findByTimeZoneAndClimbingGym_Id(int hour, Long gymId);
}
