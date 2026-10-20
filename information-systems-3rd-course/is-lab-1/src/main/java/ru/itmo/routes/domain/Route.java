package ru.itmo.routes.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "routes")
public class Route implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Название обязательно")
    @Column(nullable = false)
    private String name;

    @Valid
    @NotNull
    @Embedded
    private Coordinates coordinates = new Coordinates();

    @NotNull
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @NotNull(message = "Начальная локация обязательна")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "from_location_id", nullable = false)
    private Location from;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_location_id")
    private Location to;

    @DecimalMin(value = "1.0", inclusive = false, message = "Расстояние должно быть больше 1")
    @Column(nullable = false)
    private float distance;

    @NotNull
    @Positive(message = "Рейтинг должен быть больше 0")
    @Column(nullable = false)
    private Integer rating;

    @PrePersist
    void initializeCreationDate() {
        if (creationDate == null) creationDate = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? null : name.trim(); }
    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public Location getFrom() { return from; }
    public void setFrom(Location from) { this.from = from; }
    public Location getTo() { return to; }
    public void setTo(Location to) { this.to = to; }
    public float getDistance() { return distance; }
    public void setDistance(float distance) { this.distance = distance; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    @Override public boolean equals(Object other) {
        return this == other || other instanceof Route that && id != null && Objects.equals(id, that.id);
    }
    @Override public int hashCode() { return getClass().hashCode(); }
}
