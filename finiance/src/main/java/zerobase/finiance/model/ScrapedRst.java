package zerobase.finiance.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ScrapedRst {
    private Company company;
    private List<Dividend> dividendEntity;

    public ScrapedRst(){
        this.dividendEntity = new ArrayList<>();
    }
}
