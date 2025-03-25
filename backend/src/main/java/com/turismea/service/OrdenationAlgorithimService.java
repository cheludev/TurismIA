package com.turismea.service;

import com.turismea.model.entity.Spot;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class OrdenationAlgorithimService {

    /**
     * Sort spots by distance in ascending order.
     * @param spots list of spots
     * @param distances list of distances in same order as spots
     */
    public void sortByDistanceAndRating(List<Spot> spots, List<Double> distances) {
        if (spots.size() != distances.size()) {
            throw new IllegalArgumentException("Spots and distances must have the same size");
        }

        // Pair each spot with its distance and sort
        List<SpotDistancePair> paired = new java.util.ArrayList<>();
        for (int i = 0; i < spots.size(); i++) {
            paired.add(new SpotDistancePair(spots.get(i), distances.get(i)));
        }

        // Sort using Insertion Sort for small datasets
        for (int i = 1; i < paired.size(); i++) {
            SpotDistancePair key = paired.get(i);
            int j = i - 1;
            while (j >= 0 && paired.get(j).distance > key.distance) {
                paired.set(j + 1, paired.get(j));
                j = j - 1;
            }
            paired.set(j + 1, key);
        }

        // Update original list
        for (int i = 0; i < spots.size(); i++) {
            spots.set(i, paired.get(i).spot);
        }
    }

    /**
     * Sort spots by rating in descending order.
     * @param spots list of spots
     */
    public void sortByRating(List<Spot> spots) {
        spots.sort(Comparator.comparingDouble(Spot::getRating).reversed());
    }

    private static class SpotDistancePair {
        Spot spot;
        double distance;

        SpotDistancePair(Spot spot, double distance) {
            this.spot = spot;
            this.distance = distance;
        }
    }
}