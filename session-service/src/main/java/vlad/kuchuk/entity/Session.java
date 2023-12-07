package vlad.kuchuk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "session")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login", unique = true)
    @NotNull
    private String login;

    @Column(name = "opening_time")
    @NotNull
    private LocalDateTime openingTime;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Session session = (Session) object;
        return Objects.equals(id, session.id) && Objects.equals(login, session.login) && Objects.equals(openingTime, session.openingTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, openingTime);
    }
}