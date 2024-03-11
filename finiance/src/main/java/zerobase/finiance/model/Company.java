package zerobase.finiance.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    @JsonSerialize()
    private String ticker;
    private String name;
}


