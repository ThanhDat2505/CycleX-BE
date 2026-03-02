package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.BikeListingStatus;
import com.example.cyclexbe.domain.enums.Role;
import com.example.cyclexbe.entity.BikeListing;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.BikeListingRepository;
import com.example.cyclexbe.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Service for auto-assigning listings to inspectors using least-load strategy.
 *
 * Strategy: Assign to the ACTIVE inspector who currently has the fewest
 * PENDING + REVIEWING listings (least-load / round-robin effect).
 * This ensures even distribution across all inspectors.
 */
@Service
public class InspectorAssignmentService {

    private static final Logger log = LoggerFactory.getLogger(InspectorAssignmentService.class);

    private final UserRepository userRepository;
    private final BikeListingRepository bikeListingRepository;

    public InspectorAssignmentService(UserRepository userRepository,
                                      BikeListingRepository bikeListingRepository) {
        this.userRepository = userRepository;
        this.bikeListingRepository = bikeListingRepository;
    }

    /**
     * Auto-assign an inspector to the given listing.
     * Uses least-load strategy: pick the inspector with fewest active (PENDING + REVIEWING) listings.
     *
     * @param listing the BikeListing to assign an inspector to
     * @return the assigned User (inspector), or null if no inspectors available
     */
    public User assignInspector(BikeListing listing) {
        // 1. Get all active inspectors
        List<User> inspectors = userRepository.findByRoleAndStatus(Role.INSPECTOR, "ACTIVE");

        if (inspectors.isEmpty()) {
            log.warn("No active inspectors available for listing ID: {}", listing.getListingId());
            return null;
        }

        // 2. Find inspector with least active listings (PENDING + REVIEWING)
        User leastBusyInspector = inspectors.stream()
                .min(Comparator.comparingLong(this::countActiveListings))
                .orElse(inspectors.get(0));

        long currentLoad = countActiveListings(leastBusyInspector);
        log.info("Auto-assigning listing ID {} to inspector {} (ID: {}, current load: {})",
                listing.getListingId(),
                leastBusyInspector.getFullName(),
                leastBusyInspector.getUserId(),
                currentLoad);

        // 3. Assign inspector to listing
        listing.setInspector(leastBusyInspector);

        return leastBusyInspector;
    }

    /**
     * Count the number of active (PENDING + REVIEWING) listings assigned to an inspector.
     */
    private long countActiveListings(User inspector) {
        long pendingCount = bikeListingRepository.countByInspectorAndStatus(inspector, BikeListingStatus.PENDING);
        long reviewingCount = bikeListingRepository.countByInspectorAndStatus(inspector, BikeListingStatus.REVIEWING);
        return pendingCount + reviewingCount;
    }
}
