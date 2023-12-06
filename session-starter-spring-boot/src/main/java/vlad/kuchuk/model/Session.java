package vlad.kuchuk.model;

import java.time.LocalDateTime;


public record Session(Long id,
                      String login,
                      LocalDateTime openingTime) {

}
