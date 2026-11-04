package com.mss301.brand;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Entity
@Table(name = "WatchBrands")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class WatchBrand {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "BrandID")
  Integer brandId;

  @Column(name = "BrandName", nullable = false)
  String brandName;

  @Column(name = "CountryOfOrigin")
  String countryOfOrigin;
}

@Entity
@Table(
  name = "BrandWatches",
  uniqueConstraints = @UniqueConstraint(columnNames = "WatchID")
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class BrandWatch {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "WatchID")
  Integer watchId;

  @Column(name = "BrandID")
  Integer brandId;

  @Column(name = "UpdatedAt")
  LocalDateTime updatedAt;
}

interface BrandRepository extends JpaRepository<WatchBrand, Integer> {}

interface BrandWatchRepository extends JpaRepository<BrandWatch, Long> {
  Optional<BrandWatch> findByWatchId(Integer id);
  long countByBrandId(Integer id);
  List<BrandWatch> findByBrandIdOrderByWatchIdAsc(Integer id);
}

record ApiResponse<T>(boolean success, String message, T data) {
  static <T> ApiResponse<T> ok(T d, String m) {
    return new ApiResponse<>(true, m, d);
  }

  static ApiResponse<Void> error(String m) {
    return new ApiResponse<>(false, m, null);
  }
}

record BrandResponse(
  Integer brandId,
  String brandName,
  String countryOfOrigin,
  long watchCount
) {}

record LinkResponse(
  Integer watchId,
  Integer brandId,
  Integer previousBrandId,
  String status
) {}

record BrandWatchesResponse(
  Integer brandId,
  String brandName,
  long watchCount,
  List<Integer> watchIds
) {}

@Service
@RequiredArgsConstructor
class BrandService {

  final BrandRepository brands;
  final BrandWatchRepository links;

  WatchBrand brand(int id) {
    return brands
      .findById(id)
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "Brand not found with id: " + id
        )
      );
  }

  List<BrandResponse> all() {
    return brands.findAll().stream().map(this::dto).toList();
  }

  BrandResponse one(int i) {
    return dto(brand(i));
  }

  BrandResponse dto(WatchBrand b) {
    return new BrandResponse(
      b.getBrandId(),
      b.getBrandName(),
      b.getCountryOfOrigin(),
      links.countByBrandId(b.getBrandId())
    );
  }

  BrandWatchesResponse watches(int id) {
    WatchBrand b = brand(id);
    List<Integer> x = links
      .findByBrandIdOrderByWatchIdAsc(id)
      .stream()
      .map(BrandWatch::getWatchId)
      .toList();
    return new BrandWatchesResponse(id, b.getBrandName(), (long) x.size(), x);
  }

  @Transactional
  LinkResponse assign(int b, int w) {
    brand(b);
    BrandWatch x = links
      .findByWatchId(w)
      .orElseGet(() -> BrandWatch.builder().watchId(w).build());
    Integer old = x.getBrandId();
    x.setBrandId(b);
    x.setUpdatedAt(LocalDateTime.now());
    links.save(x);
    return new LinkResponse(w, b, old, "ASSIGNED");
  }

  @Transactional
  LinkResponse unassign(int w) {
    return links
      .findByWatchId(w)
      .map(x -> {
        Integer old = x.getBrandId();
        links.delete(x);
        return new LinkResponse(w, null, old, "UNASSIGNED");
      })
      .orElse(new LinkResponse(w, null, null, "NOTHING_TO_DO"));
  }
}

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/brands")
class InternalBrandController {

  final BrandService s;

  @PutMapping("/{brandId}/watches/{watchId}")
  ApiResponse<LinkResponse> assign(
    @PathVariable int brandId,
    @PathVariable int watchId
  ) {
    return ApiResponse.ok(s.assign(brandId, watchId), "Assigned");
  }

  @DeleteMapping("/watches/{watchId}")
  ApiResponse<LinkResponse> unassign(@PathVariable int watchId) {
    return ApiResponse.ok(s.unassign(watchId), "Unassigned");
  }
}

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/brands")
class BrandController {

  final BrandService s;

  @GetMapping
  ApiResponse<List<BrandResponse>> all() {
    return ApiResponse.ok(s.all(), "Success");
  }

  @GetMapping("/{id}")
  ApiResponse<BrandResponse> one(@PathVariable int id) {
    return ApiResponse.ok(s.one(id), "Success");
  }

  @GetMapping("/{id}/watches")
  ApiResponse<BrandWatchesResponse> watches(@PathVariable int id) {
    return ApiResponse.ok(s.watches(id), "Success");
  }
}

@Configuration
class BrandSeed {

  @Bean
  CommandLineRunner seed(BrandRepository b, BrandWatchRepository l) {
    return x -> {
      if (b.count() == 0) b.saveAll(
        List.of(
          WatchBrand.builder()
            .brandName("Rolex")
            .countryOfOrigin("Switzerland")
            .build(),
          WatchBrand.builder()
            .brandName("Omega")
            .countryOfOrigin("Switzerland")
            .build(),
          WatchBrand.builder()
            .brandName("Seiko")
            .countryOfOrigin("Japan")
            .build(),
          WatchBrand.builder()
            .brandName("Casio")
            .countryOfOrigin("Japan")
            .build(),
          WatchBrand.builder()
            .brandName("TAG Heuer")
            .countryOfOrigin("Switzerland")
            .build()
        )
      );
      if (l.count() == 0) {
        int[] ids = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 },
          bs = { 1, 2, 3, 4, 5, 2, 3, 1, 4, 4 };
        for (int i = 0; i < ids.length; i++) l.save(
          BrandWatch.builder()
            .watchId(ids[i])
            .brandId(bs[i])
            .updatedAt(LocalDateTime.now())
            .build()
        );
      }
    };
  }
}
