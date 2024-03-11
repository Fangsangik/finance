package zerobase.finiance.persist.entitiy;
import lombok.*;
import zerobase.finiance.model.Company;

import javax.persistence.*;

@Entity
@Table(name = "COMPANY")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)
    //unique = true 중복이 되면 안됨
    private String ticker;

    public CompanyEntity(Company company){
        this.ticker = company.getTicker();
        this.name = company.getName();
    }
}
