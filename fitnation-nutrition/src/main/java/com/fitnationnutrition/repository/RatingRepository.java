package com.fitnationnutrition.repository;

import com.fitnationnutrition.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.plan.id = :planId")
    Double getAverageRating(Long planId);
}