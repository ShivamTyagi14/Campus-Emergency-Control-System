package com.campus.emergency.repository;
    import com.campus.emergency.model.CampusLocation;
    import org.springframework.data.jpa.repository.JpaRepository;
    import java.util.*;
    public interface LocationRepository extends JpaRepository<CampusLocation, Long> {

}
