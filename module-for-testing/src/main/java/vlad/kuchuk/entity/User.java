package vlad.kuchuk.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    @Size(min = 2, max = 100)
    private String login;

    @NotNull
    @Size(min = 2, max = 100)
    private String password;

    @NotNull
    @Size(min = 2, max = 100)
    private String name;

    @Column(name = "session_opening_time")
    @NotNull
    private LocalDateTime sessionOpeningTime;
}